package com.syf.wanandroidcompose.login

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.syf.wanandroidcompose.WanAndroidApplication
import com.syf.wanandroidcompose.common.BaseViewModelOptimized
import com.syf.wanandroidcompose.network.Result
import com.syf.wanandroidcompose.network.RetrofitClient
import com.syf.wanandroidcompose.network.safeApiCall
import kotlinx.coroutines.launch

/**
 * 登录注册模块 ViewModel
 * 负责处理登录与注册的网络请求及输入校验
 */
class LoginViewModel(private val apiService: LoginApiService, private val application: Application) :
    BaseViewModelOptimized<LoginAction, LoginState>() {

    override fun onAction(action: LoginAction, currentState: LoginState?) {
        when (action) {
            is LoginAction.InputUsername -> emitState { replayState?.copy(usernameInput = action.username) }
            is LoginAction.InputPassword -> emitState { replayState?.copy(passwordInput = action.password) }
            is LoginAction.ClickLogin -> login()
            is LoginAction.ClickRegister -> register()
            is LoginAction.LoginSuccess -> emitState { replayState?.copy(loginSuccess = true, errorMsg = null) }
            is LoginAction.LoginFailed -> emitState { replayState?.copy(loginSuccess = false) }
            is LoginAction.RegisterSuccess -> emitState { replayState?.copy(registerSuccess = true, errorMsg = null) }
            is LoginAction.RegisterFailed -> emitState { replayState?.copy(registerSuccess = false) }
            is LoginAction.Navigated -> emitState { replayState?.copy(loginSuccess = false, registerSuccess = false) }
        }
    }

    /**
     * 执行登录逻辑
     */
    private fun login() {
        val currentState = replayState ?: return
        if (currentState.isLoading) return

        // 输入校验（简单示例）
        if (currentState.usernameInput.isEmpty() || currentState.passwordInput.isEmpty()) {
            emitState { replayState?.copy(errorMsg = "用户名或密码不能为空") }
            return
        }

        emitState { replayState?.copy(isLoading = true, errorMsg = null) }

        viewModelScope.launch {
            when (val result = safeApiCall { apiService.login(currentState.usernameInput, currentState.passwordInput) }) {
                is Result.Success -> {
                    // 登录成功，可以在此保存 Token 或 Cookie
                    emitState { replayState?.copy(isLoading = false, loginSuccess = true) }
                }
                is Result.Error -> {
                    emitState { replayState?.copy(isLoading = false, errorMsg = result.message) }
                }
                Result.Loading -> {}
            }
        }
    }

    /**
     * 执行注册逻辑
     */
    private fun register() {
        val currentState = replayState ?: return
        if (currentState.isLoading) return

        if (currentState.usernameInput.isEmpty() || currentState.passwordInput.isEmpty()) {
            emitState { replayState?.copy(errorMsg = "用户名或密码不能为空") }
            return
        }

        emitState { replayState?.copy(isLoading = true, errorMsg = null) }

        viewModelScope.launch {
            // 玩 Android 注册接口需要传入两次密码
            when (val result = safeApiCall { apiService.register(currentState.usernameInput, currentState.passwordInput, currentState.passwordInput) }) {
                is Result.Success -> {
                    emitState { replayState?.copy(isLoading = false, registerSuccess = true) }
                }
                is Result.Error -> {
                    emitState { replayState?.copy(isLoading = false, errorMsg = result.message) }
                }
                Result.Loading -> {}
            }
        }
    }

    companion object {
        /**
         * ViewModel 工厂
         */
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as WanAndroidApplication)
                val apiService = RetrofitClient.create<LoginApiService>()
                LoginViewModel(apiService, application)
            }
        }
    }
}
