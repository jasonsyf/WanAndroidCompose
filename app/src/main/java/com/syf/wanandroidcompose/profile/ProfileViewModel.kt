package com.syf.wanandroidcompose.profile

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.syf.wanandroidcompose.WanAndroidApplication
import com.syf.wanandroidcompose.common.BaseViewModelOptimized
import kotlinx.coroutines.launch

/**
 * 个人中心模块 ViewModel
 * 负责用户信息的展示状态管理
 */
class ProfileViewModel(private val application: Application) :
    BaseViewModelOptimized<ProfileAction, ProfileState>() {

    init {
        // 初始状态，目前仅模拟用户信息。实际应结合 Repository/UserStore 观察登录态。
        emitState { ProfileState(username = "请登录", isLogin = false) }
    }

    override fun onAction(action: ProfileAction, currentState: ProfileState?) {
        when (action) {
            is ProfileAction.LoadProfile -> loadProfile()
            is ProfileAction.ClickLoginRegister -> navigateToLoginRegister()
            is ProfileAction.ClickMyCollection -> clickMyCollection()
            is ProfileAction.ClickSettings -> clickSettings()
            is ProfileAction.LoginRegisterNavigated -> resetLoginRegisterNavigation()
        }
    }

    /**
     * 模拟加载个人资料
     */
    private fun loadProfile() {
        viewModelScope.launch {
            emitState { replayState?.copy(isLoading = true) }
            // 模拟网络请求或本地 IO
            kotlinx.coroutines.delay(1000)
            emitState {
                val isLoggedIn = false // 示例状态
                replayState?.copy(
                    isLoading = false,
                    username = if (isLoggedIn) "用户昵称" else "请登录",
                    isLogin = isLoggedIn
                )
            }
        }
    }

    /**
     * 处理跳转到登录注册
     */
    private fun navigateToLoginRegister() {
        emitState { replayState?.copy(navigateToLoginRegister = true) }
    }

    /**
     * 重置导航状态，防止重复跳转
     */
    private fun resetLoginRegisterNavigation() {
        emitState { replayState?.copy(navigateToLoginRegister = false) }
    }

    private fun clickMyCollection() {
        // TODO: 导航至“我的收藏”页面
    }

    private fun clickSettings() {
        // TODO: 导航至设置详情页面
    }

    companion object {
        /**
         * ViewModel 工厂
         */
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as WanAndroidApplication)
                ProfileViewModel(application)
            }
        }
    }
}
