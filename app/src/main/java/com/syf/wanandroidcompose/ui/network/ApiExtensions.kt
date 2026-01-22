package com.syf.wanandroidcompose.ui.network

import com.syf.wanandroidcompose.ui.network.NetworkException.toErrorCode
import com.syf.wanandroidcompose.ui.network.NetworkException.toErrorMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import timber.log.Timber

/**
 * 网络请求扩展函数
 * 用于简化 MVI 模式下的网络请求调用
 */

/**
 * 发起网络请求并返回 Flow<ApiResult<T>>
 * 适用于 MVI 架构中的 ViewModel
 *
 * 使用示例：
 * ```
 * fun loadData() = flow {
 *     val result = apiRequest { apiService.getData() }
 *     emit(result)
 * }.collectInViewModel()
 * ```
 *
 * @param request 网络请求的 suspend 函数
 * @return Flow<ApiResult<T>>
 */
fun <T> apiRequest(
    request: suspend () -> ApiResponse<T>
): Flow<ApiResult<T>> = flow {
    try {
        val response = request()
        val result = response.toApiResult()
        emit(result)
    } catch (e: Exception) {
        Timber.e(e, "API request failed")
        emit(
            ApiResult.Error(
                exception = e,
                message = e.toErrorMessage(),
                code = e.toErrorCode()
            )
        )
    }
}.onStart {
    // 发起请求前先发送 Loading 状态
    emit(ApiResult.Loading)
}.catch { e ->
    // 捕获异常并转换为 Error 状态
    Timber.e(e, "Flow error")
    emit(
        ApiResult.Error(
            exception = e,
            message = e.toErrorMessage(),
            code = e.toErrorCode()
        )
    )
}.flowOn(Dispatchers.IO) // 在 IO 线程执行

/**
 * 简化的网络请求方法（不自动发送 Loading 状态）
 * 适用于需要手动控制 Loading 状态的场景
 *
 * @param request 网络请求的 suspend 函数
 * @return Flow<ApiResult<T>>
 */
fun <T> apiRequestWithoutLoading(
    request: suspend () -> ApiResponse<T>
): Flow<ApiResult<T>> = flow {
    try {
        val response = request()
        val result = response.toApiResult()
        emit(result)
    } catch (e: Exception) {
        Timber.e(e, "API request failed")
        emit(
            ApiResult.Error(
                exception = e,
                message = e.toErrorMessage(),
                code = e.toErrorCode()
            )
        )
    }
}.catch { e ->
    Timber.e(e, "Flow error")
    emit(
        ApiResult.Error(
            exception = e,
            message = e.toErrorMessage(),
            code = e.toErrorCode()
        )
    )
}.flowOn(Dispatchers.IO)

/**
 * 简化的 suspend 调用方式
 * 直接返回 ApiResult，不使用 Flow
 *
 * 使用示例：
 * ```
 * viewModelScope.launch {
 *     val result = safeApiCall { apiService.getData() }
 *     result.onSuccess { data ->
 *         // 处理成功
 *     }.onError { error, message, code ->
 *         // 处理失败
 *     }
 * }
 * ```
 */
suspend fun <T> safeApiCall(
    request: suspend () -> ApiResponse<T>
): ApiResult<T> {
    return try {
        val response = request()
        response.toApiResult()
    } catch (e: Exception) {
        Timber.e(e, "API call failed")
        ApiResult.Error(
            exception = e,
            message = e.toErrorMessage(),
            code = e.toErrorCode()
        )
    }
}

/**
 * 对已有的 Flow 进行转换，添加异常处理
 */
fun <T> Flow<T>.asApiResult(): Flow<ApiResult<T>> = flow {
    emit(ApiResult.Loading)
    this@asApiResult.collect { data ->
        emit(ApiResult.Success(data))
    }
}.catch { e ->
    emit(ApiResult.Error(exception = e, message = e.toErrorMessage(), code = e.toErrorCode()))
}.flowOn(Dispatchers.IO)