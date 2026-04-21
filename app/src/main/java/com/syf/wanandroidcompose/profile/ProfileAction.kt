package com.syf.wanandroidcompose.profile

import com.syf.wanandroidcompose.common.Action
import com.syf.wanandroidcompose.common.State

/**
 * 个人中心模块的用户操作和系统事件
 */
sealed class ProfileAction : Action {
    /** 加载用户信息 */
    object LoadProfile : ProfileAction()
    /** 点击登录/注册 */
    object ClickLoginRegister : ProfileAction()
    /** 点击我的收藏 */
    object ClickMyCollection : ProfileAction()
    /** 点击设置 */
    object ClickSettings : ProfileAction()
    /** 导航到登录注册页后的状态重置 */
    object LoginRegisterNavigated : ProfileAction()
}

/**
 * 个人中心模块的 UI 状态
 * @param isLoading 是否正在加载
 * @param username 用户昵称
 * @param isLogin 是否已登录
 * @param errorMsg 错误提示
 * @param navigateToLoginRegister 是否需要导航到登录注册页
 */
data class ProfileState(
    val isLoading: Boolean = false,
    val username: String = "未登录",
    val isLogin: Boolean = false,
    val errorMsg: String? = null,
    val navigateToLoginRegister: Boolean = false
) : State
