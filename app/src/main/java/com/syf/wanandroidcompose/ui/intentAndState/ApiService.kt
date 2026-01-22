package com.syf.wanandroidcompose.ui.intentAndState

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * 统一的网络响应体基类
 * 所有 API 响应都遵循此格式：
 * {
 *     "data": ...,
 *     "errorCode": 0,
 *     "errorMsg": ""
 * }
 *
 * @param T data 字段的类型
 */
data class BaseResponse<T>(
    val data: T?,
    val errorCode: Int,
    val errorMsg: String
) {
    /**
     * 判断请求是否成功
     */
    fun isSuccess(): Boolean = errorCode == 0

    /**
     * 判断是否需要登录（errorCode == -1001）
     */
    fun needLogin(): Boolean = errorCode == -1001
}

interface ApiService {
    /**
     * 用户注册
     * @param username 用户名
     * @param password 密码
     * @param repassword 确认密码
     */
    @FormUrlEncoded
    @POST("user/register")
    suspend fun register(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("repassword") repassword: String
    ): BaseResponse<Any>

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     */
    @FormUrlEncoded
    @POST("user/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): BaseResponse<Any>

}