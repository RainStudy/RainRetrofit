package kim.bifrost.rain.retrofit.internal

import kim.bifrost.rain.retrofit.Call
import kim.bifrost.rain.retrofit.RainRetrofit
import kim.bifrost.rain.retrofit.annotation.*
import kim.bifrost.rain.retrofit.annotation.Field
import okhttp3.FormBody
import okhttp3.Request
import okhttp3.RequestBody
import java.lang.reflect.*

/**
 * kim.bifrost.rain.retrofit.internal.RetrofitProxyHandler
 * RainRetrofit
 *
 * @author 寒雨
 * @since 2022/1/15 19:20
 **/
class RetrofitProxyHandler<T>(
    private val clazz: Class<T>,
    private val retrofit: RainRetrofit
) : InvocationHandler {

    @Suppress("UNCHECKED_CAST")
    fun getInstance(): T {
        return Proxy.newProxyInstance(clazz.classLoader, arrayOf(clazz), this) as T
    }

    override fun invoke(proxy: Any, method: Method, args: Array<out Any>): Any? {
        val annotations = method.annotations
        val httpMethod = annotations.firstOrNull { it is GET || it is POST }
            ?: error("unknown http method with method ${method.name}")
        val params = method.annotatedParameterTypes
        fun handleUrl(sub: String): String {
            var subUrl = sub
            val queryParams = HashMap<String, String>()
            params.forEachIndexed { i, type ->
                val path = type.annotations.firstOrNull { a -> a is Path } as Path?
                val query = type.annotations.firstOrNull { q -> q is Query } as Query?
                path?.let {
                    subUrl = subUrl.replace("{${path.value}}", args[i].toString())
                }
                query?.let {
                    queryParams[it.value] = args[i].toString()
                }
            }
            if (queryParams.isNotEmpty()) {
                val builder = StringBuilder("?")
                val iter = queryParams.iterator()
                while (iter.hasNext()) {
                    val v = iter.next()
                    builder.append("${v.key}=${v.value}")
                    if (iter.hasNext()) builder.append("&")
                }
                subUrl += builder.toString()
            }
            return retrofit.baseurl + subUrl
        }
        when(httpMethod) {
            is GET -> {
                val url = handleUrl(httpMethod.value)
                if (method.returnType.isAssignableFrom(Call::class.java)) {
                    val innerClazz = method.returnType.getGenerics()[0].javaClass
                    return Call(
                        okhttpCall = retrofit.okHttpClient.newCall(
                            Request.Builder()
                                .url(url)
                                .build()
                        ),
                        clazz = innerClazz,
                        converter = retrofit.converter
                    )
                }
                // 协程拓展
                if (isSuspendMethod(method)) {
                    return Call(
                        okhttpCall = retrofit.okHttpClient.newCall(
                            Request.Builder()
                                .url(url)
                                .build()
                        ),
                        clazz = method.returnType,
                        converter = retrofit.converter
                    ).execute()
                }
            }
            is POST -> {
                val url = handleUrl(httpMethod.value)
                if (annotations.any { it is FormUrlEncoded }) {
                    val body = FormBody.Builder().run {
                        params.forEachIndexed { i, type ->
                            val field = type.annotations.firstOrNull { it is Field } as Field? ?: return@forEachIndexed
                            add(field.value, args[i].toString())
                        }
                        build()
                    }
                    if (method.returnType.isAssignableFrom(Call::class.java)) {
                        val innerClazz = method.returnType.getGenerics()[0].javaClass
                        return Call(
                            okhttpCall = retrofit.okHttpClient.newCall(
                                Request.Builder()
                                    .url(url)
                                    .post(body)
                                    .build()
                            ),
                            clazz = innerClazz,
                            converter = retrofit.converter
                        )
                    }
                    // 协程拓展
                    if (isSuspendMethod(method)) {
                        return Call(
                            okhttpCall = retrofit.okHttpClient.newCall(
                                Request.Builder()
                                    .url(url)
                                    .post(body)
                                    .build()
                            ),
                            clazz = method.returnType,
                            converter = retrofit.converter
                        ).execute()
                    }
                }
            }
        }
        return null
    }

    private fun Class<*>.getGenerics(): Array<Type> {
        return (this as ParameterizedType).actualTypeArguments
    }

    // 判断方法是否是挂起函数
    private fun isSuspendMethod(method: Method): Boolean {
        val last = method.typeParameters.lastOrNull() ?: return false
        return last.typeName.startsWith("Continuation")
    }
}