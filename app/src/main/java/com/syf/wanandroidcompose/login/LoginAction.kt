package com.syf.wanandroidcompose.login

import com.syf.wanandroidcompose.common.Action
import com.syf.wanandroidcompose.common.State

/**
 * 登录注册模块的用户操作和系统事件
 */
sealed class LoginAction : Action {
    /** 输入用户名 */
    data class InputUsername(val username: String) : LoginAction()
    /** 输入密码 */
    data class InputPassword(val password: String) : LoginAction()
    /** 点击登录按钮 */
    object ClickLogin : LoginAction()
    /** 点击注册按钮 */
    object ClickRegister : LoginAction()
    /** 登录成功事件 */
    object LoginSuccess : LoginAction()
    /** 登录失败事件 */
    object LoginFailed : LoginAction()
    /** 注册成功事件 */
    object RegisterSuccess : LoginAction()
    /** 注册失败事件 */
    object RegisterFailed : LoginAction()
    /** 导航离开登录页后的状态重置 */
    object Navigated : LoginAction()
}

/**
 * 登录注册模块的 UI 状态
 * @param usernameInput 用户名输入框内容
 * @param passwordInput 密码输入框内容
 * @param isLoading 是否正在提交请求
 * @param errorMsg 错误提示信息
 * @param loginSuccess 是否登录成功
 * @param registerSuccess 是否注册成功
 */
data class LoginState(
    val usernameInput: String = "",
    val passwordInput: String = "",
    val isLoading: Boolean = false,
    val errorMsg: String? = null,
    val loginSuccess: Boolean = false,
    val registerSuccess: Boolean = false
) : State
