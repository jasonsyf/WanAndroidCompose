package com.syf.wanandroidcompose.ui.network

import kotlinx.serialization.Serializable

/**
 * 通用的 API 响应基类
 * 适配玩 Android API 的响应格式
 *
 * @param T 实际数据类型
 */
@Serializable
data class ApiResponse<T>(
    val data: T? = null,
    val errorCode: Int = 0,
    val errorMsg: String = ""
) {
    /**
     * 判断请求是否成功
     * 玩 Android API 的 errorCode 为 0 表示成功
     */
    val isSuccess: Boolean
        get() = errorCode == 0

    /**
     * 判断请求是否失败
     */
    val isFailure: Boolean
        get() = !isSuccess
}

/**
 * 将 ApiResponse 转换为 ApiResult
 */
fun <T> ApiResponse<T>.toApiResult(): ApiResult<T> {
    return if (isSuccess && data != null) {
        ApiResult.Success(data)
    } else {
        ApiResult.Error(
            exception = ApiException(errorCode, errorMsg),
            message = errorMsg.ifEmpty { "请求失败" },
            code = errorCode
        )
    }
}

/**
 * API 异常类
 */
class ApiException(
    val code: Int,
    override val message: String
) : Exception(message)
