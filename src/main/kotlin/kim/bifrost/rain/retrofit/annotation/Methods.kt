package kim.bifrost.rain.retrofit.annotation

/**
 * kim.bifrost.rain.retrofit.method.POST
 * RainRetrofit
 *
 * @author 寒雨
 * @since 2022/1/15 22:55
 **/
@Target(AnnotationTarget.FUNCTION)
annotation class POST(
    val value: String
)

@Target(AnnotationTarget.FUNCTION)
annotation class GET(
    val value: String
)

