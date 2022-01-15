package kim.bifrost.rain.retrofit.annotation

/**
 * kim.bifrost.rain.retrofit.annotation.Query
 * RainRetrofit
 *
 * @author 寒雨
 * @since 2022/1/15 23:27
 **/
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Query(
    val value: String
)
