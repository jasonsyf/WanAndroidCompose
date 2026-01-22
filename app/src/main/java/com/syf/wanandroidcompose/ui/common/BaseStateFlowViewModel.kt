package com.syf.wanandroidcompose.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 基于 StateFlow 的 BaseViewModel
 * 
 * 适用场景：
 * - 需要默认/初始状态的场景
 * - 需要同步获取当前状态的场景
 * - 不需要区分"无状态"和"有状态"的场景
 * 
 * 与 BaseViewModelOptimized 的区别：
 * - BaseViewModelOptimized: 使用 SharedFlow，可以没有初始状态
 * - BaseStateFlowViewModel: 使用 StateFlow，必须有初始状态
 * 
 * @param A Action 类型
 * @param S State 类型
 * @param initialState 初始状态
 */
abstract class BaseStateFlowViewModel<A : Action, S : State>(
    initialState: S
) : ViewModel() {

    /**
     * 状态流
     * 
     * StateFlow 特点：
     * - 总是有值
     * - 自动去重
     * - 新订阅者立即收到当前值
     */
    private val _state = MutableStateFlow(initialState)
    
    /**
     * 暴露给外部的只读状态流
     */
    val state: StateFlow<S> = _state.asStateFlow()

    /**
     * 当前状态（同步获取）
     * 
     * StateFlow 的优势：可以同步获取当前值
     */
    val currentState: S
        get() = _state.value

    /**
     * 发送 Action
     * 
     * @param action 要处理的 Action
     */
    fun sendAction(action: A) {
        viewModelScope.launch {
            onAction(action, currentState)
        }
    }

    /**
     * 子类实现的 Action 处理方法
     * 
     * @param action 当前 Action
     * @param currentState 当前状态（总是有值）
     */
    protected abstract fun onAction(action: A, currentState: S)

    /**
     * 更新状态（直接设置）
     * 
     * @param state 新状态
     */
    protected fun setState(state: S) {
        _state.value = state
    }

    /**
     * 更新状态（函数式）
     * 
     * 推荐使用这种方式，因为它是线程安全的
     * 
     * @param update 更新函数，接收当前状态，返回新状态
     */
    protected fun updateState(update: (S) -> S) {
        _state.update(update)
    }

    /**
     * 异步更新状态
     * 
     * @param block suspend 函数，返回新状态
     */
    protected fun updateStateAsync(block: suspend (S) -> S) {
        viewModelScope.launch {
            val newState = block(currentState)
            setState(newState)
        }
    }
}
