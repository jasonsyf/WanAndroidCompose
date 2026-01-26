package com.syf.wanandroidcompose.network

/**
 * 通用的网络请求结果封装 适用于 Flow 和 MVI 模式
 *
 * @param T 数据类型
 */
sealed class Result<out T> {
    /**
     * 成功状态
     * @param data 返回的数据
     */
    data class Success<T>(val data: T) : Result<T>()

    /**
     * 错误状态
     * @param exception 异常信息
     * @param message 错误消息
     * @param code 错误代码
     */
    data class Error(
        val exception: Throwable,
        val message: String = exception.message ?: "未知错误",
        val code: Int = -1
    ) : Result<Nothing>()

    /** 加载中状态 */
    data object Loading : Result<Nothing>()

    /** 判断是否成功 */
    val isSuccess: Boolean
        get() = this is Success

    /** 判断是否失败 */
    val isError: Boolean
        get() = this is Error

    /** 判断是否加载中 */
    val isLoading: Boolean
        get() = this is Loading

    /** 获取数据，如果失败则返回 null */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }

    /** 获取数据，如果失败则抛出异常 */
    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Error -> throw exception
        is Loading -> throw IllegalStateException("数据正在加载中")
    }

    /** 获取数据，如果失败则返回默认值 */
    fun getOrElse(defaultValue: @UnsafeVariance T): T = when (this) {
        is Success -> data
        else -> defaultValue
    }

    /** 映射数据 */
    inline fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
        is Loading -> this
    }

    /** 在成功时执行操作 */
    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(data)
        return this
    }

    /** 在失败时执行操作 */
    inline fun onError(action: (Throwable, String, Int) -> Unit): Result<T> {
        if (this is Error) action(exception, message, code)
        return this
    }

    /** 在加载时执行操作 */
    inline fun onLoading(action: () -> Unit): Result<T> {
        if (this is Loading) action()
        return this
    }

    companion object {
        /** 从 ApiResponse 创建 Error 用于处理 API 返回的错误响应 */
        fun <T> apiError(response: ApiResponse<T>): Error {
            return Error(
                exception = ApiException(response.errorCode, response.errorMsg),
                message = response.errorMsg.ifEmpty { "请求失败" },
                code = response.errorCode
            )
        }

        /** 从异常创建 Error 用于处理网络异常、超时等 */
        fun fromException(exception: Throwable, message: String? = null, code: Int = -1): Error {
            return Error(
                exception = exception,
                message = message ?: exception.message ?: "未知错误",
                code = code
            )
        }
    }
}

/** 将可空类型转换为 Result */
fun <T> T?.toResult(errorMessage: String = "数据为空"): Result<T> {
    return if (this != null) {
        Result.Success(this)
    } else {
        Result.Error(NullPointerException(errorMessage), errorMessage)
    }
}
