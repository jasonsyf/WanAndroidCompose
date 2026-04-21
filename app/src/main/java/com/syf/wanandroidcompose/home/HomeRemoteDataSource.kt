package com.syf.wanandroidcompose.home

import com.syf.wanandroidcompose.network.ApiResponse

/**
 * 首页远程数据源
 *
 * 负责与远程服务器（玩Android API）进行通信，获取网络数据。
 * 它是 HomeRepository 的一部分，负责执行实际的网络请求。
 *
 * @param apiService Retrofit 定义的 API 服务接口。
 */
class HomeRemoteDataSource(private val apiService: HomeApiService) {

    /**
     * 获取指定页码的文章列表。
     * @param page 页码，从 0 开始。
     * @return ApiResponse 包含文章列表数据。
     */
    suspend fun getArticleList(page: Int): ApiResponse<ArticleListData> = apiService.getArticleList(page)

    /**
     * 获取首页 Banner 数据。
     * @return ApiResponse 包含 Banner 列表。
     */
    suspend fun getBanner(): ApiResponse<List<BannerData>> = apiService.getBanner()

    /**
     * 获取公众号账号列表。
     * @return ApiResponse 包含公众号账号列表。
     */
    suspend fun getWeChatAccounts(): ApiResponse<List<WeChatAccountData>> = apiService.getWeChatAccounts()
}
