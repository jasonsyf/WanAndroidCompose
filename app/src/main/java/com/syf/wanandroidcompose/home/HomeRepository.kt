package com.syf.wanandroidcompose.home

import com.syf.wanandroidcompose.home.local.HomeDao
import com.syf.wanandroidcompose.home.local.toData
import com.syf.wanandroidcompose.home.local.toEntity
import com.syf.wanandroidcompose.network.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class HomeRepository(private val apiService: HomeApiService, private val localDataSource: HomeDao) {

    /** Local cached articles flow */
    val articles: Flow<List<ArticleData>> =
        localDataSource.getAllArticles().map { entities -> entities.map { it.toData() } }

    /** Local cached banners flow */
    val banners: Flow<List<BannerData>> =
        localDataSource.getBanners().map { entities -> entities.map { it.toData() } }

    /** Local cached WeChat accounts flow */
    val weChatAccounts: Flow<List<WeChatAccountData>> =
        localDataSource.getWeChatAccounts().map { entities -> entities.map { it.toData() } }

    /**
     * Refresh articles (Page 0) using Flow pattern Emits Loading -> Success/Error Updates local
     * cache on success
     */
    fun fetchArticles(): Flow<Result<ArticleListData>> =
        flow {
            emit(Result.Loading)
            try {
                val response = apiService.getArticleList(0)
                if (response.errorCode == 0 && response.data != null) {
                    val articles = response.data.datas
                    localDataSource.replaceArticles(articles.map { it.toEntity() })
                    emit(Result.Success(response.data))
                } else {
                    emit(Result.apiError(response))
                }
            } catch (e: Exception) {
                emit(Result.fromException(e, "网络异常，请检查网络连接"))
            }
        }
            .flowOn(Dispatchers.IO)

    /**
     * Load more articles (Page > 0) using Flow pattern Emits Loading -> Success/Error Appends to
     * local cache on success
     */
    fun fetchMoreArticles(page: Int): Flow<Result<ArticleListData>> =
        flow {
            emit(Result.Loading)
            try {
                val response = apiService.getArticleList(page)
                if (response.errorCode == 0 && response.data != null) {
                    val articles = response.data.datas
                    if (articles.isNotEmpty()) {
                        localDataSource.insertArticles(articles.map { it.toEntity() })
                    }
                    emit(Result.Success(response.data))
                } else {
                    emit(Result.apiError(response))
                }
            } catch (e: Exception) {
                emit(Result.fromException(e, "网络异常，请检查网络连接"))
            }
        }
            .flowOn(Dispatchers.IO)

    /**
     * Get banner data using Flow pattern Emits Loading -> Success/Error Updates local cache on
     * success
     */
    fun fetchBanners(): Flow<Result<List<BannerData>>> =
        flow {
            emit(Result.Loading)
            try {
                val response = apiService.getBanner()
                if (response.errorCode == 0 && response.data != null) {
                    localDataSource.replaceBanners(response.data.map { it.toEntity() })
                    emit(Result.Success(response.data))
                } else {
                    emit(Result.apiError(response))
                }
            } catch (e: Exception) {
                emit(Result.fromException(e, "网络异常，请检查网络连接"))
            }
        }
            .flowOn(Dispatchers.IO)

    /**
     * Get WeChat accounts using Flow pattern Emits Loading -> Success/Error Updates local cache on
     * success
     */
    fun fetchWeChatAccounts(): Flow<Result<List<WeChatAccountData>>> =
        flow {
            emit(Result.Loading)
            try {
                val response = apiService.getWeChatAccounts()
                if (response.errorCode == 0 && response.data != null) {
                    localDataSource.replaceWeChatAccounts(
                        response.data.map { it.toEntity() }
                    )
                    emit(Result.Success(response.data))
                } else {
                    emit(Result.apiError(response))
                }
            } catch (e: Exception) {
                emit(Result.fromException(e, "网络异常，请检查网络连接"))
            }
        }.flowOn(Dispatchers.IO)
}
