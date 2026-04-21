package com.syf.wanandroidcompose.common

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/**
 * 带有副作用（Effect）处理的 BaseViewModel。
 *
 * 在 MVI 模式中，Effect 通常用于处理一次性事件，如弹出 Toast、页面导航、显示对话框等。
 *
 * @param A Action 类型，代表用户意图或事件。
 * @param S State 类型，代表 UI 状态。
 * @param E Effect 类型，代表一次性的副作用事件。
 */
abstract class BaseViewModelWithEffect<A : Action, S : State, E : Effect> : ViewModel() {
    /**
     * Action 通道，用于接收来自 UI 或系统内部的事件。
     * [event] 包含用户与 UI 的交互（如点击操作），也有来自后台的消息（如切换自习模式）。
     */
    private val _action = Channel<A>()

    /**
     * [actor] 是一个 SendChannel，用于在 viewModelScope 之外安全地发送 Action。
     */
    val actor: SendChannel<A> by lazy { _action }

    /**
     * [_state] 是一个 SharedFlow，用于聚合和广播页面的全部 UI 状态。
     * 使用 replay=1 来缓存最新的状态。
     * 不使用 StateFlow 因为它需要一个默认值：https://github.com/Kotlin/kotlinx.coroutines/issues/2515
     */
    private val _state = MutableSharedFlow<S>(
        replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val state: Flow<S> by lazy { _state.distinctUntilChanged() }

    /**
     * [replayState] 用于获取当前的 UI 状态缓存。
     */
    val replayState: S?
        get() = _state.replayCache.firstOrNull()

    /**
     * [_effect] 是一个 SharedFlow，用于发送一次性的副作用事件。
     * 例如：弹 Toast、导航 Fragment 等。
     * 它没有 replay 缓存，确保每个 Effect 只被处理一次。
     */
    private val _effect = MutableSharedFlow<E>()
    val effect: SharedFlow<E> by lazy { _effect.asSharedFlow() }

    /**
     * 初始化块，监听 Action 并分发给 onAction 处理。
     */
    init {
        viewModelScope.launch {
            _action.consumeAsFlow().collect {
                onAction(it, replayState)
            }
        }
    }

    /**
     * 发送一个 Action。
     */
    fun sendAction(action: A) = viewModelScope.launch {
        _action.send(action)
    }

    /**
     * 抽象函数，子类必须实现此方法来处理 Action。
     */
    protected abstract fun onAction(action: A, currentState: S?)

    /**
     * 发送一个新的 UI 状态。
     */
    protected fun emitState(builder: suspend () -> S?) = viewModelScope.launch {
        builder()?.let { _state.emit(it) }
    }

    /**
     * 发送一个副作用事件。
     */
    protected fun emitEffect(builder: suspend () -> E?) = viewModelScope.launch {
        builder()?.let { _effect.emit(it) }
    }

    /**
     * 挂起函数，直接发送一个 state。
     */
    protected suspend fun emitState(state: S) = _state.emit(state)

    /**
     * 挂起函数，直接发送一个 effect。
     */
    protected suspend fun emitEffect(effect: E) = _effect.emit(effect)

    /**
     * 尝试立即发送一个 state (非挂起)。
     * 主要用于测试。
     * @return Boolean 返回是否发送成功。
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    fun tryEmitState(state: S) = _state.tryEmit(state)

    /**
     * 尝试立即发送一个 effect (非挂起)。
     * 主要用于测试。
     * @return Boolean 返回是否发送成功。
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    fun tryEmitEffect(effect: E) = _effect.tryEmit(effect)
}
