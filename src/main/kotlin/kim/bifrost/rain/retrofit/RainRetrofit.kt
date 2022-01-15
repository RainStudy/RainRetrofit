package kim.bifrost.rain.retrofit

import kim.bifrost.rain.retrofit.internal.RetrofitProxyHandler
import okhttp3.OkHttpClient

/**
 * kim.bifrost.rain.retrofit.RainRetrofit
 * RainRetrofit
 *
 * @author 寒雨
 * @since 2022/1/15 19:00
 **/
class RainRetrofit private constructor(
    val baseurl: String,
    val okHttpClient: OkHttpClient,
    val converter: Converter,
){
    class Builder {
        private lateinit var baseurl: String
        private lateinit var okHttpClient: OkHttpClient
        private lateinit var converter: Converter

        fun baseurl(baseurl: String): Builder {
            this.baseurl = baseurl
            return this
        }

        fun okhttpClient(okHttpClient: OkHttpClient): Builder {
            this.okHttpClient = okHttpClient
            return this
        }

        fun converter(converter: Converter): Builder {
            this.converter = converter
            return this
        }

        fun build(): RainRetrofit {
            return RainRetrofit(baseurl, okHttpClient, converter)
        }
    }

    fun <T> create(clazz: Class<T>): T {
        return RetrofitProxyHandler(clazz, this).getInstance()
    }
}