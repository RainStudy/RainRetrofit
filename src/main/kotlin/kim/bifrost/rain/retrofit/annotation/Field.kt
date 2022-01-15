package kim.bifrost.rain.retrofit.annotation

/**
 * kim.bifrost.rain.retrofit.annotation.Field
 * RainRetrofit
 *
 * @author 寒雨
 * @since 2022/1/15 23:28
 **/
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Field(
    val value: String
)
