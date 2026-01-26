package com.syf.wanandroidcompose.common

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syf.wanandroidcompose.BuildConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * 优化版 BaseViewModel
 *
 * 改进点：
 * 1. 增加 Action Channel 缓冲区，防止快速点击丢失事件
 * 2. 添加异常处理，防止 Flow 中断
 * 3. 支持 Action 取消机制
 * 4. 添加调试日志支持
 * 5. 线程调度可配置
 *
 * @param A Action 类型
 * @param S State 类型
 */
abstract class BaseViewModelOptimized<A : Action, S : State> : ViewModel() {
    /**
     * Action 通道，使用缓冲区防止事件丢失
     *
     * 使用 Channel.BUFFERED (容量 64) 而不是默认的 RENDEZVOUS (容量 0) 这样可以处理快速连续的用户操作（如快速点击）
     */
    private val _action = Channel<A>(capacity = Channel.BUFFERED)

    /** 外部发送 Action 的通道 可在非 viewModelScope 外使用 */
    val actor: SendChannel<A> by lazy { _action }

    /**
     * UI 状态流
     *
     * 使用 MutableSharedFlow 而不是 StateFlow，因为有时不需要初始值 replay = 1: 新订阅者会收到最新的状态 onBufferOverflow =
     * DROP_OLDEST: 缓冲区满时丢弃最旧的状态
     */
    private val _state =
        MutableSharedFlow<S>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    /** 暴露给外部的状态流 distinctUntilChanged: 只在状态真正改变时才发送 */
    val state: Flow<S> by lazy { _state.distinctUntilChanged() }

    /** 获取当前重放的状态 replay 始终为 1，所以最多有一个状态 */
    val replayState: S?
        get() = _state.replayCache.firstOrNull()

    /** 正在执行的 Action Job 集合 用于支持按 key 取消 Action */
    private val actionJobs = mutableMapOf<String, Job>()

    /** Action 执行的调度器 子类可以重写以在其他线程执行 */
    protected open val actionDispatcher: CoroutineDispatcher = Dispatchers.Main.immediate

    /** 是否启用日志 默认在 Debug 模式下启用 */
    protected open val enableLogging: Boolean = BuildConfig.DEBUG

    /** 初始化：订阅 Action 流并分发处理 */
    init {
        viewModelScope.launch(actionDispatcher) {
            _action.consumeAsFlow().collect { action ->
                try {
                    logAction(action)
                    onAction(action, replayState)
                } catch (e: Exception) {
                    logException(e, action)
                    handleException(e, action)
                }
            }
        }
    }

    /**
     * 发送 Action
     *
     * @param action 要发送的 Action
     */
    fun sendAction(action: A) {
        viewModelScope.launch { _action.send(action) }
    }

    /**
     * 子类实现的 Action 处理方法
     *
     * @param action 当前 Action
     * @param currentState 当前状态（可能为 null）
     */
    protected abstract fun onAction(action: A, currentState: S?)

    /**
     * 发送 State（suspend 版本）
     *
     * 直接发送，不启动新协程，适合在 suspend 函数中使用
     *
     * @param state 要发送的状态
     */
    protected suspend fun emitState(state: S) {
        _state.emit(state)
    }

    /**
     * 发送 State（builder 版本）
     *
     * 启动新协程执行 builder，适合在非 suspend 函数中使用
     *
     * @param builder 构建状态的 lambda
     */
    protected fun emitState(builder: suspend () -> S?) {
        viewModelScope.launch { builder()?.let { _state.emit(it) } }
    }

    /**
     * 尝试发送 State（非挂起）
     *
     * 用于测试或特殊场景
     *
     * @param state 要发送的状态
     * @return 是否成功发送
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    fun tryEmitState(state: S): Boolean {
        return _state.tryEmit(state)
    }

    /**
     * 启动一个可取消的 Action 协程
     *
     * 如果指定了 key，之前相同 key 的 Job 会被取消 这对于搜索、自动保存等场景很有用
     *
     * @param key 唯一标识，相同 key 的 Job 会被取消
     * @param block 要执行的协程代码
     * @return Job
     */
    protected fun launchAction(
        key: String? = null,
        block: suspend CoroutineScope.() -> Unit
    ): Job { // 取消之前相同 key 的 Job
        key?.let { actionJobs[it]?.cancel() } // 创建异常处理器，防止未捕获的异常导致崩溃
        val exceptionHandler = kotlinx.coroutines.CoroutineExceptionHandler { _, exception ->
            if (exception is Exception) {
                logException(exception, null)
                handleException(exception, null)
            } else { // 非 Exception 类型的 Throwable，重新抛出（如 OutOfMemoryError）
                throw Exception()
            }
        }

        return viewModelScope.launch(exceptionHandler) {
                try {
                    block()
                } catch (e: Exception) { // 显式捕获异常，确保不会遗漏
                    logException(e, null)
                    handleException(e, null)
                }
            }.also { job -> // 保存新的 Job
                key?.let {
                    actionJobs[it] = job // Job 完成后清理
                    job.invokeOnCompletion { actionJobs.remove(key) }
                }
            }
    }

    /**
     * 取消指定 key 的 Action
     *
     * @param key Action 的唯一标识
     */
    protected fun cancelAction(key: String) {
        actionJobs[key]?.cancel()
        actionJobs.remove(key)
    }

    /** 取消所有正在执行的 Action */
    protected fun cancelAllActions() {
        actionJobs.values.forEach { it.cancel() }
        actionJobs.clear()
    }

    /**
     * 处理异常
     *
     * 子类可以重写以自定义异常处理逻辑
     *
     * @param exception 异常
     * @param action 触发异常的 Action
     */
    protected open fun handleException(exception: Exception, action: A?) { // 默认不做处理，子类可以重写
        // 例如：发送错误状态、显示 Toast 等
    }

    /** 记录 Action 日志 */
    private fun logAction(action: A) {
        if (enableLogging) {
            Timber.tag("MVI-Action").d("${this::class.simpleName}: ${action::class.simpleName}")
        }
    }

    /** 记录异常日志 */
    private fun logException(exception: Exception, action: A?) {
        if (enableLogging) {
            val actionName = action?.let { "${it::class.simpleName}" } ?: "Unknown"
            Timber.tag("MVI-Error").e(exception, "Error handling action: $actionName")
        }
    }

    /** ViewModel 清理时取消所有 Action */
    override fun onCleared() {
        super.onCleared()
        cancelAllActions()
        _action.close()
    }
}
