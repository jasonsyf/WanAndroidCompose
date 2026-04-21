package com.syf.wanandroidcompose.login

import com.syf.wanandroidcompose.network.ApiResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * 登录注册模块 API 服务接口
 */
interface LoginApiService {
    /**
     * 登录
     * @param username 用户名
     * @param password 密码
     * @return 登录结果响应
     */
    @POST("user/login")
    @FormUrlEncoded
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): ApiResponse<Any>

    /**
     * 注册
     * @param username 用户名
     * @param password 密码
     * @param repassword 确认密码
     * @return 注册结果响应
     */
    @POST("user/register")
    @FormUrlEncoded
    suspend fun register(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("repassword") repassword: String
    ): ApiResponse<Any>
}
