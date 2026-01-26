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
    /** 本地缓存的文章流 */
    val articles: Flow<List<ArticleData>> =
            localDataSource.getAllArticles().map { entities -> entities.map { it.toData() } }

    /** 本地缓存的轮播图流 */
    val banners: Flow<List<BannerData>> =
            localDataSource.getBanners().map { entities -> entities.map { it.toData() } }

    /** 本地缓存的微信公众号流 */
    val weChatAccounts: Flow<List<WeChatAccountData>> =
            localDataSource.getWeChatAccounts().map { entities -> entities.map { it.toData() } }

    /** 刷新文章列表（第 0 页） 使用 Flow 模式，发射 Loading -> Success/Error 成功时更新本地缓存 */
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

    /** 加载更多文章（页码 > 0） 使用 Flow 模式，发射 Loading -> Success/Error 成功时追加到本地缓存 */
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

    /** 获取轮播图数据 使用 Flow 模式，发射 Loading -> Success/Error 成功时更新本地缓存 */
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

    /** 获取微信公众号数据 使用 Flow 模式，发射 Loading -> Success/Error 成功时更新本地缓存 */
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
                    }
                    .flowOn(Dispatchers.IO)
}
