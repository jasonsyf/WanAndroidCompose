package com.syf.wanandroidcompose.home

import com.syf.wanandroidcompose.R
import com.syf.wanandroidcompose.home.HomeLocalDataSource
import com.syf.wanandroidcompose.home.HomeRemoteDataSource
import com.syf.wanandroidcompose.home.local.toData
import com.syf.wanandroidcompose.home.local.toEntity
import com.syf.wanandroidcompose.i18n.AppText
import com.syf.wanandroidcompose.network.Result
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

data class HomeCachedData(
    val articles: List<ArticleData>,
    val banners: List<BannerData>,
    val weChatAccounts: List<WeChatAccountData>
)

data class HomeRefreshMeta(
    val hasMore: Boolean? = null,
    val errorMessage: String? = null)

class HomeRepository(
    private val remoteDataSource: HomeRemoteDataSource,
    private val localDataSource: HomeLocalDataSource
) {
    /** 本地缓存的文章流 */
    val articles: Flow<List<ArticleData>> =
        localDataSource.getAllArticles().map { entities -> entities.map { it.toData() } }

    /** 本地缓存的轮播图流 */
    val banners: Flow<List<BannerData>> =
        localDataSource.getBanners().map { entities -> entities.map { it.toData() } }

    /** 本地缓存的微信公众号流 */
    val weChatAccounts: Flow<List<WeChatAccountData>> =
        localDataSource.getWeChatAccounts().map { entities -> entities.map { it.toData() } }

    /** 首页本地缓存聚合流，UI 只需订阅这一条流 */
    val homeData: Flow<HomeCachedData> =
        combine(articles, banners, weChatAccounts) { articleList, bannerList, accountList ->
            HomeCachedData(
                articles = articleList, banners = bannerList, weChatAccounts = accountList
            )
        }

    /** 刷新首页数据（并发请求 + 本地落库） */
    fun refreshHomeData(): Flow<Result<HomeRefreshMeta>> = flow {
        emit(Result.Loading)
        val errors = mutableListOf<String>()
        var hasMore: Boolean? = null

        coroutineScope {
            val articleJob = async { refreshArticlesPage0() }
            val bannerJob = async { refreshBanners() }
            val accountJob = async { refreshWeChatAccounts() }

            val articleResult = articleJob.await()
            hasMore = articleResult.hasMore
            articleResult.errorMessage?.let(errors::add)
            bannerJob.await()?.let(errors::add)
            accountJob.await()?.let(errors::add)
        }

        emit(
            Result.Success(
                HomeRefreshMeta(
                    hasMore = hasMore, errorMessage = errors.firstOrNull()
                )
            )
        )
    }.flowOn(Dispatchers.IO)

    /** 加载更多文章（页码 > 0） 使用 Flow 模式，发射 Loading -> Success/Error 成功时追加到本地缓存 */
    fun fetchMoreArticles(page: Int): Flow<Result<ArticleListData>> = flow {
        emit(Result.Loading)
        try {
            val response = remoteDataSource.getArticleList(page)
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
            emit(Result.fromException(e, AppText.get(R.string.error_network_exception)))
        }
    }.flowOn(Dispatchers.IO)

    private data class ArticleRefreshResult(
        val hasMore: Boolean? = null, val errorMessage: String? = null
    )

    private suspend fun refreshArticlesPage0(): ArticleRefreshResult {
        return try {
            val response = remoteDataSource.getArticleList(0)
            if (response.errorCode == 0 && response.data != null) {
                localDataSource.replaceArticles(response.data.datas.map { it.toEntity() })
                ArticleRefreshResult(hasMore = !response.data.over)
            } else {
                ArticleRefreshResult(errorMessage = Result.apiError(response).message)
            }
        } catch (e: Exception) {
            ArticleRefreshResult(
                errorMessage = Result.fromException(
                    e, AppText.get(R.string.error_network_exception)
                ).message
            )
        }
    }

    private suspend fun refreshBanners(): String? {
        return try {
            val response = remoteDataSource.getBanner()
            if (response.errorCode == 0 && response.data != null) {
                localDataSource.replaceBanners(response.data.map { it.toEntity() })
                null
            } else {
                Result.apiError(response).message
            }
        } catch (e: Exception) {
            Result.fromException(e, AppText.get(R.string.error_network_exception)).message
        }
    }

    private suspend fun refreshWeChatAccounts(): String? {
        return try {
            val response = remoteDataSource.getWeChatAccounts()
            if (response.errorCode == 0 && response.data != null) {
                localDataSource.replaceWeChatAccounts(response.data.map { it.toEntity() })
                null
            } else {
                Result.apiError(response).message
            }
        } catch (e: Exception) {
            Result.fromException(e, AppText.get(R.string.error_network_exception)).message
        }
    }
}
