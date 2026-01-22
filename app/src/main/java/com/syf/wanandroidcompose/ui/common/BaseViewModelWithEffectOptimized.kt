package com.syf.wanandroidcompose.ui.common

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * 优化版 BaseViewModelWithEffect
 * 
 * 继承自 BaseViewModelOptimized，添加 Effect 支持
 * 
 * 改进点：
 * 1. 继承 BaseViewModelOptimized，避免代码重复
 * 2. 增加 Effect 缓冲区，防止一次性事件丢失
 * 3. 继承所有 BaseViewModelOptimized 的优化特性
 * 
 * @param A Action 类型
 * @param S State 类型
 * @param E Effect 类型（一次性事件，如 Toast、导航等）
 */
abstract class BaseViewModelWithEffectOptimized<A : Action, S : State, E : Effect> 
    : BaseViewModelOptimized<A, S>() {

    /**
     * Effect 流（一次性事件）
     * 
     * 与 State 不同：
     * - State：持久化的 UI 状态
     * - Effect：一次性事件（Toast、导航、弹窗等）
     * 
     * 配置说明：
     * - replay = 0: 不重放，Effect 是一次性的
     * - extraBufferCapacity = 64: 缓冲 64 个事件，防止丢失
     * - onBufferOverflow = DROP_OLDEST: 缓冲区满时丢弃最旧的事件
     */
    private val _effect = MutableSharedFlow<E>(
        replay = 0,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    /**
     * 暴露给外部的 Effect 流
     * 
     * 使用 asSharedFlow() 防止外部修改
     */
    val effect: SharedFlow<E> by lazy { _effect.asSharedFlow() }

    /**
     * 发送 Effect（suspend 版本）
     * 
     * 直接发送，不启动新协程
     * 
     * @param effect 要发送的 Effect
     */
    protected suspend fun emitEffect(effect: E) {
        _effect.emit(effect)
    }

    /**
     * 发送 Effect（builder 版本）
     * 
     * 启动新协程执行 builder
     * 
     * @param builder 构建 Effect 的 lambda
     */
    protected fun emitEffect(builder: suspend () -> E?) {
        viewModelScope.launch {
            builder()?.let { _effect.emit(it) }
        }
    }

    /**
     * 尝试发送 Effect（非挂起）
     * 
     * 用于测试或特殊场景
     * 
     * @param effect 要发送的 Effect
     * @return 是否成功发送
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    fun tryEmitEffect(effect: E): Boolean {
        return _effect.tryEmit(effect)
    }
}
