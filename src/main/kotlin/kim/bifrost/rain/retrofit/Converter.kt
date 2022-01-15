package kim.bifrost.rain.retrofit

import com.google.gson.Gson
import okhttp3.ResponseBody

/**
 * kim.bifrost.rain.retrofit.Converter
 * RainRetrofit
 * 序列化接口，对接各个序列化库
 *
 * @author 寒雨
 * @since 2022/1/16 0:27
 **/
interface Converter {
    fun <T> convert(body: ResponseBody, clazz: Class<T>): T
}

class GsonConverter private constructor(
    private val gson: Gson
) : Converter {

    override fun <T> convert(body: ResponseBody, clazz: Class<T>): T {
        return gson.fromJson(body.string(), clazz)
    }

    companion object {
        fun create(): GsonConverter {
            return GsonConverter(Gson())
        }

        fun create(gson: Gson): GsonConverter {
            return GsonConverter(gson)
        }
    }
}