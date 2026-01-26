package com.syf.wanandroidcompose.network

import kotlinx.serialization.Serializable

/**
 * 通用的 API 响应基类 适配玩 Android API 的响应格式
 *
 * @param T 实际数据类型
 * @property data 响应数据
 * @property errorCode 错误码，0 表示成功
 * @property errorMsg 错误消息
 */
@Serializable
data class ApiResponse<T>(val data: T? = null, val errorCode: Int = 0, val errorMsg: String = "") {
    /** 判断请求是否成功 玩 Android API 的 errorCode 为 0 表示成功 */
    val isSuccess: Boolean
        get() = errorCode == 0

    /** 判断请求是否失败 */
    val isFailure: Boolean
        get() = !isSuccess
}

/**
 * 将 ApiResponse 转换为 Result
 * @return Result 对象，成功时返回 Success，失败时返回 Error
 */
fun <T> ApiResponse<T>.toResult(): Result<T> {
    return if (isSuccess && data != null) {
        Result.Success(data)
    } else {
        Result.Error(
                exception = ApiException(errorCode, errorMsg),
                message = errorMsg.ifEmpty { "请求失败" },
                code = errorCode
        )
    }
}

/**
 * API 异常类
 * @property code 错误码
 * @property message 错误消息
 */
class ApiException(val code: Int, override val message: String) : Exception(message)
