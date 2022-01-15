package kim.bifrost.rain.retrofit.annotation

/**
 * kim.bifrost.rain.retrofit.annotation.Path
 * RainRetrofit
 *
 * @author 寒雨
 * @since 2022/1/15 23:29
 **/
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Path(
    val value: String
)
