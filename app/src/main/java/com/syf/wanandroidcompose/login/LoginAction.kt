package com.syf.wanandroidcompose.login

import com.syf.wanandroidcompose.common.Action
import com.syf.wanandroidcompose.common.State

sealed class LoginAction : Action {
    data class InputUsername(val username: String) : LoginAction()
    data class InputPassword(val password: String) : LoginAction()
    object ClickLogin : LoginAction()
    object ClickRegister : LoginAction()
    object LoginSuccess : LoginAction() // 登录成功
    object LoginFailed : LoginAction() // 登录失败
    object RegisterSuccess : LoginAction() // 注册成功
    object RegisterFailed : LoginAction() // 注册失败
    object Navigated : LoginAction() // 从登录/注册页面导航后重置状态
}

data class LoginState(
    val usernameInput: String = "",
    val passwordInput: String = "",
    val isLoading: Boolean = false,
    val errorMsg: String? = null,
    val loginSuccess: Boolean = false,
    val registerSuccess: Boolean = false
) : State
