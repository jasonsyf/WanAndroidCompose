package com.syf.wanandroidcompose.common

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

// 定义接口，用于标识不同类型的事件和状态
interface Action // 行为：用户的操作或系统的事件
interface State  // 状态：UI的当前状况
interface Effect // 副作用：一次性事件，如Toast提示、页面导航等

/**
 * ViewModel的基础类，采用MVI（Model-View-Intent）架构模式。
 * @param A Action类型，代表用户意图或事件。
 * @param S State类型，代表UI状态。
 */
abstract class BaseViewModel<A : Action, S : State> : ViewModel() {
    /**
     * Action通道，用于接收来自UI或系统内部的事件。
     * [event] 包含用户与UI的交互（如点击操作），也有来自后台的消息（如切换自习模式）。
     */
    private val _action = Channel<A>()

    /**
     * [actor] 是一个SendChannel，用于在viewModelScope之外安全地发送Action。
     */
    val actor: SendChannel<A> by lazy { _action }

    /**
     * [_state] 是一个SharedFlow，用于聚合和广播页面的全部UI状态。
     * 使用replay=1来缓存最新的状态，新订阅者可以立即收到。
     * 不使用StateFlow因为其需要一个默认值：https://github.com/Kotlin/kotlinx.coroutines/issues/2515
     */
    private val _state = MutableSharedFlow<S>(
        replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    /**
     * 对外暴露的UI状态Flow，使用distinctUntilChanged来避免重复发送相同的状态。
     */
    val state: Flow<S> by lazy { _state.distinctUntilChanged() }

    /**
     * [replayState] 用于重放当前的UI状态，replay始终是1。
     * 可以方便地获取到最新的状态缓存。
     */
    val replayState: S?
        get() = _state.replayCache.firstOrNull()

    /**
     * 初始化块，在ViewModel创建时启动一个协程，持续监听_action通道中的事件，
     * 并调用onAction进行分发处理。
     */
    init {
        viewModelScope.launch {
            _action.consumeAsFlow().collect {
                onAction(it, replayState)
            }
        }
    }

    /**
     * 公开的函数，用于从UI或其他地方发送一个Action。
     */
    fun sendAction(action: A) = viewModelScope.launch {
        _action.send(action)
    }

    /**
     * 抽象函数，子类必须实现此方法来处理接收到的Action。
     * @param action 接收到的具体Action。
     * @param currentState 处理Action前的当前状态。
     */
    protected abstract fun onAction(action: A, currentState: S?)

    /**
     * 在viewModelScope中启动一个协程来构建并发送一个新的UI状态。
     * @param builder 一个挂起函数，用于创建新的状态。
     */
    protected fun emitState(builder: suspend () -> S?) = viewModelScope.launch {
        builder()?.let { _state.emit(it) }
    }


    /**
     * 挂起函数，直接发送一个已经构建好的新状态。
     */
    protected suspend fun emitState(state: S) = _state.emit(state)

    /**
     * 非挂起函数，尝试立即发送一个state。如果通道已满，则会失败。
     * 主要用于测试。
     * @return Boolean 返回是否发送成功。
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    fun tryEmitState(state: S) = _state.tryEmit(state)

}
