package com.syf.wanandroidcompose.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.syf.wanandroidcompose.BuildConfig
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

/**
 * Retrofit 客户端单例
 * 提供统一的网络请求配置
 */
object RetrofitClient {

    /**
     * 玩 Android API 基础地址
     */
    private const val BASE_URL = "https://www.wanandroid.com/"

    /**
     * 连接超时时间（秒）
     */
    private const val CONNECT_TIMEOUT = 30L

    /**
     * 读取超时时间（秒）
     */
    private const val READ_TIMEOUT = 30L

    /**
     * 写入超时时间（秒）
     */
    private const val WRITE_TIMEOUT = 30L

    /**
     * JSON 配置
     * 忽略未知字段，使用宽松模式
     */
    private val json = Json {
        ignoreUnknownKeys = true // 忽略JSON中未定义的字段
        coerceInputValues = true // 强制转换输入值
        isLenient = true // 宽松模式
        explicitNulls = false // 不显式编码 null 值
    }

    /**
     * OkHttp 客户端
     */
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder().apply {
            // 设置超时时间
            connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)

            // 添加日志拦截器（仅在 Debug 模式下）
            if (BuildConfig.DEBUG) {
                addInterceptor(
                    HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    }
                )
            }

            // 重试机制
            retryOnConnectionFailure(true)

            // 可以在这里添加更多拦截器
            // 例如：token 拦截器、公共参数拦截器等
            // addInterceptor(TokenInterceptor())
            // addInterceptor(CommonParamsInterceptor())
        }.build()
    }

    /**
     * Retrofit 实例
     */
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    /**
     * 创建 API 服务
     *
     * @param T API 服务接口类型
     * @return API 服务实例
     */
    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }

    /**
     * 创建 API 服务（Kotlin 泛型扩展）
     *
     * 使用示例：
     * ```
     * val apiService = RetrofitClient.create<HomeApiService>()
     * ```
     */
    inline fun <reified T> create(): T = createService(T::class.java)
}
