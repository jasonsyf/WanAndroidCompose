package com.syf.wanandroidcompose.network

import com.syf.wanandroidcompose.R
import com.syf.wanandroidcompose.i18n.AppText
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import retrofit2.HttpException

/** 网络异常处理工具 */
object NetworkException {
    /** 异常错误码定义 */
    object ErrorCode {
        const val NETWORK_ERROR = -1000 // 网络错误
        const val TIMEOUT_ERROR = -1001 // 超时错误
        const val HTTP_ERROR = -1002 // HTTP 错误
        const val JSON_ERROR = -1003 // JSON 解析错误
        const val UNKNOWN_ERROR = -1004 // 未知错误
        const val NO_NETWORK = -1005 // 无网络连接
    }

    /** 将异常转换为错误消息 */
    fun Throwable.toErrorMessage(): String {
        return when (this) {
            is UnknownHostException, is ConnectException ->
                    AppText.get(R.string.error_network_connection_failed)
            is SocketTimeoutException -> AppText.get(R.string.error_network_timeout)
            is HttpException -> {
                when (code()) {
                    400 -> AppText.get(R.string.error_bad_request)
                    401 -> AppText.get(R.string.error_unauthorized)
                    403 -> AppText.get(R.string.error_forbidden)
                    404 -> AppText.get(R.string.error_not_found)
                    500 -> AppText.get(R.string.error_server_internal)
                    502 -> AppText.get(R.string.error_bad_gateway)
                    503 -> AppText.get(R.string.error_service_unavailable)
                    else -> AppText.get(R.string.error_network_code_format, code())
                }
            }
            is ApiException -> message
            is IOException -> AppText.get(R.string.error_network_exception)
            else -> message ?: AppText.get(R.string.error_unknown)
        }
    }

    /** 将异常转换为错误码 */
    fun Throwable.toErrorCode(): Int {
        return when (this) {
            is UnknownHostException, is ConnectException -> ErrorCode.NO_NETWORK
            is SocketTimeoutException -> ErrorCode.TIMEOUT_ERROR
            is HttpException -> code()
            is ApiException -> code
            is IOException -> ErrorCode.NETWORK_ERROR
            else -> ErrorCode.UNKNOWN_ERROR
        }
    }
}
