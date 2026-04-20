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

    private fun login() {
        val currentState = replayState ?: return
        if (currentState.isLoading) return

        emitState { replayState?.copy(isLoading = true, errorMsg = null) }

        viewModelScope.launch {
            when (val result = safeApiCall { apiService.login(currentState.usernameInput, currentState.passwordInput) }) {
                is Result.Success -> {
                    // 登录成功，保存用户信息等
                    emitState { replayState?.copy(isLoading = false, loginSuccess = true) }
                }
                is Result.Error -> {
                    emitState { replayState?.copy(isLoading = false, errorMsg = result.message) }
                }
                Result.Loading -> {}
            }
        }
    }

    private fun register() {
        val currentState = replayState ?: return
        if (currentState.isLoading) return

        emitState { replayState?.copy(isLoading = true, errorMsg = null) }

        viewModelScope.launch {
            when (val result = safeApiCall { apiService.register(currentState.usernameInput, currentState.passwordInput, currentState.passwordInput) }) {
                is Result.Success -> {
                    // 注册成功
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
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as WanAndroidApplication)
                val apiService = RetrofitClient.create<LoginApiService>()
                LoginViewModel(apiService, application)
            }
        }
    }
}
