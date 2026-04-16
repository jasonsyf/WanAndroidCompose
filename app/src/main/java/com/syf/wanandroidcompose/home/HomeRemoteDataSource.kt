package com.syf.wanandroidcompose.home

import com.syf.wanandroidcompose.network.ApiResponse

class HomeRemoteDataSource(private val apiService: HomeApiService) {
    suspend fun getArticleList(page: Int): ApiResponse<ArticleListData> = apiService.getArticleList(page)

    suspend fun getBanner(): ApiResponse<List<BannerData>> = apiService.getBanner()

    suspend fun getWeChatAccounts(): ApiResponse<List<WeChatAccountData>> = apiService.getWeChatAccounts()
}