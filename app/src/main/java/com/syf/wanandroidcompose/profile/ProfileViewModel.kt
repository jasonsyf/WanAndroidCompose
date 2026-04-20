package com.syf.wanandroidcompose.profile

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.syf.wanandroidcompose.WanAndroidApplication
import com.syf.wanandroidcompose.common.BaseViewModelOptimized
import kotlinx.coroutines.launch

class ProfileViewModel(private val application: Application) :
    BaseViewModelOptimized<ProfileAction, ProfileState>() {

    init {
        // 模拟加载用户数据，实际应该从 UserRepository 获取
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

    private fun loadProfile() {
        // 模拟加载逻辑
        viewModelScope.launch {
            emitState { replayState?.copy(isLoading = true) }
            // 假设这里进行网络请求或读取本地数据
            kotlinx.coroutines.delay(1000) // 模拟网络延迟
            emitState {
                val isLoggedIn = false // 假设用户未登录
                replayState?.copy(
                    isLoading = false,
                    username = if (isLoggedIn) "用户昵称" else "请登录",
                    isLogin = isLoggedIn
                )
            }
        }
    }

    private fun navigateToLoginRegister() {
        emitState { replayState?.copy(navigateToLoginRegister = true) }
    }

    private fun resetLoginRegisterNavigation() {
        emitState { replayState?.copy(navigateToLoginRegister = false) }
    }

    private fun clickMyCollection() {
        // TODO: 导航到我的收藏
    }

    private fun clickSettings() {
        // TODO: 导航到设置页面
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as WanAndroidApplication)
                ProfileViewModel(application)
            }
        }
    }
}
