package com.syf.wanandroidcompose.ui.network

import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * 网络异常处理工具
 */
object NetworkException {
    
    /**
     * 异常错误码
     */
    object ErrorCode {
        const val NETWORK_ERROR = -1000 // 网络错误
        const val TIMEOUT_ERROR = -1001 // 超时错误
        const val HTTP_ERROR = -1002 // HTTP错误
        const val JSON_ERROR = -1003 // JSON解析错误
        const val UNKNOWN_ERROR = -1004 // 未知错误
        const val NO_NETWORK = -1005 // 无网络连接
    }

    /**
     * 将异常转换为错误消息
     */
    fun Throwable.toErrorMessage(): String {
        return when (this) {
            is UnknownHostException,
            is ConnectException -> "网络连接失败，请检查网络设置"
            is SocketTimeoutException -> "网络请求超时，请稍后重试"
            is HttpException -> {
                when (code()) {
                    400 -> "请求参数错误"
                    401 -> "未授权，请先登录"
                    403 -> "禁止访问"
                    404 -> "请求的资源不存在"
                    500 -> "服务器内部错误"
                    502 -> "网关错误"
                    503 -> "服务不可用"
                    else -> "网络错误: ${code()}"
                }
            }
            is ApiException -> message
            is IOException -> "网络异常，请检查网络连接"
            else -> message ?: "未知错误"
        }
    }

    /**
     * 将异常转换为错误码
     */
    fun Throwable.toErrorCode(): Int {
        return when (this) {
            is UnknownHostException,
            is ConnectException -> ErrorCode.NO_NETWORK
            is SocketTimeoutException -> ErrorCode.TIMEOUT_ERROR
            is HttpException -> code()
            is ApiException -> code
            is IOException -> ErrorCode.NETWORK_ERROR
            else -> ErrorCode.UNKNOWN_ERROR
        }
    }
}
