package com.syf.wanandroidcompose.profile

import com.syf.wanandroidcompose.common.Action
import com.syf.wanandroidcompose.common.State

sealed class ProfileAction : Action {
    object LoadProfile : ProfileAction()
    object ClickLoginRegister : ProfileAction()
    object ClickMyCollection : ProfileAction()
    object ClickSettings : ProfileAction()
    object LoginRegisterNavigated : ProfileAction() // 登录注册页导航后重置状态
}

data class ProfileState(
    val isLoading: Boolean = false,
    val username: String = "未登录",
    val isLogin: Boolean = false,
    val errorMsg: String? = null,
    val navigateToLoginRegister: Boolean = false
) : State
