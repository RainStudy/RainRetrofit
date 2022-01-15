package kim.bifrost.rain.retrofit

import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import okhttp3.Call as OKHttpCall

/**
 * kim.bifrost.rain.retrofit.Call
 * RainRetrofit
 *
 * @author 寒雨
 * @since 2022/1/16 0:35
 **/
class Call<T>(
    private val okhttpCall: OKHttpCall,
    private val clazz: Class<T>,
    private val converter: Converter
) {
    fun execute(): T {
        return converter.convert(okhttpCall.execute().body ?: error("null response body"), clazz)
    }

    fun enqueue(onSuccess: (T) -> Unit, onFailure: (IOException) -> Unit) {
        return okhttpCall.enqueue(object : Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                onFailure(e)
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                onSuccess(converter.convert(response.body ?: error("null response body"), clazz))
            }
        })
    }

    fun cancel() {
        okhttpCall.cancel()
    }
}
