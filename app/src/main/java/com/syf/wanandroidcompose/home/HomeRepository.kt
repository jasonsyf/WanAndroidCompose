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

/**
 * 首页聚合的本地缓存数据模型。
 */
data class HomeCachedData(
    val articles: List<ArticleData>,
    val banners: List<BannerData>,
    val weChatAccounts: List<WeChatAccountData>
)

/**
 * 首页刷新操作的元数据，包含是否有更多数据和错误信息。
 */
data class HomeRefreshMeta(
    val hasMore: Boolean? = null,
    val errorMessage: String? = null
)

/**
 * 首页数据仓库
 *
 * 负责协调远程数据源和本地数据源，为 ViewModel 提供统一的数据访问接口。
 * 实现了“单一数据源”模式，UI 层只关心从本地数据库流出的数据。
 *
 * @param remoteDataSource 远程数据源，负责网络请求。
 * @param localDataSource 本地数据源，负责数据库操作。
 */
class HomeRepository(
    private val remoteDataSource: HomeRemoteDataSource,
    private val localDataSource: HomeLocalDataSource
) {
    /** 从本地数据库获取的文章流，并转换为UI数据模型。 */
    val articles: Flow<List<ArticleData>> =
        localDataSource.getAllArticles().map { entities -> entities.map { it.toData() } }

    /** 从本地数据库获取的轮播图流，并转换为UI数据模型。 */
    val banners: Flow<List<BannerData>> =
        localDataSource.getBanners().map { entities -> entities.map { it.toData() } }

    /** 从本地数据库获取的微信公众号流，并转换为UI数据模型。 */
    val weChatAccounts: Flow<List<WeChatAccountData>> =
        localDataSource.getWeChatAccounts().map { entities -> entities.map { it.toData() } }

    /**
     * 首页本地缓存数据的聚合流。
     * 使用 `combine` 将文章、轮播图和公众号三个流合并成一个，
     * UI 只需订阅这一个流即可获取所有需要展示的数据。
     */
    val homeData: Flow<HomeCachedData> =
        combine(articles, banners, weChatAccounts) { articleList, bannerList, accountList ->
            HomeCachedData(
                articles = articleList, banners = bannerList, weChatAccounts = accountList
            )
        }

    /**
     * 刷新首页数据。
     * 使用 `coroutineScope` 和 `async` 并发执行文章、轮播图和公众号的网络请求，
     * 请求成功后将数据存入本地数据库（Room），数据库的数据变化会通过 Flow 自动通知 UI 更新。
     * @return 返回一个包含刷新结果元数据（如是否有更多数据、错误信息）的 Flow。
     */
    fun refreshHomeData(): Flow<Result<HomeRefreshMeta>> = flow {
        emit(Result.Loading) // 开始刷新，发送 Loading 状态
        val errors = mutableListOf<String>()
        var hasMore: Boolean? = null

        coroutineScope {
            // 并发启动三个网络请求
            val articleJob = async { refreshArticlesPage0() }
            val bannerJob = async { refreshBanners() }
            val accountJob = async { refreshWeChatAccounts() }

            // 等待并处理结果
            val articleResult = articleJob.await()
            hasMore = articleResult.hasMore
            articleResult.errorMessage?.let(errors::add)
            bannerJob.await()?.let(errors::add)
            accountJob.await()?.let(errors::add)
        }

        // 发送最终的成功状态，其中可能包含错误信息
        emit(
            Result.Success(
                HomeRefreshMeta(
                    hasMore = hasMore, errorMessage = errors.firstOrNull()
                )
            )
        )
    }.flowOn(Dispatchers.IO) // 确保整个 Flow 在 IO 线程上执行

    /**
     * 加载更多文章（页码 > 0）。
     * 请求成功后，将新数据追加到本地数据库，而不是替换。
     * @param page 要加载的页码。
     * @return 返回包含文章列表数据的 Flow。
     */
    fun fetchMoreArticles(page: Int): Flow<Result<ArticleListData>> = flow {
        emit(Result.Loading)
        try {
            val response = remoteDataSource.getArticleList(page)
            if (response.errorCode == 0 && response.data != null) {
                val articles = response.data.datas
                if (articles.isNotEmpty()) {
                    // 使用 insertArticles 追加数据
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

    // 文章刷新结果的内部数据类
    private data class ArticleRefreshResult(
        val hasMore: Boolean? = null, val errorMessage: String? = null
    )

    // 刷新第0页文章的私有方法
    private suspend fun refreshArticlesPage0(): ArticleRefreshResult {
        return try {
            val response = remoteDataSource.getArticleList(0)
            if (response.errorCode == 0 && response.data != null) {
                // 使用 replaceArticles 替换首页数据
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

    // 刷新轮播图的私有方法
    private suspend fun refreshBanners(): String? {
        return try {
            val response = remoteDataSource.getBanner()
            if (response.errorCode == 0 && response.data != null) {
                localDataSource.replaceBanners(response.data.map { it.toEntity() })
                null // 成功则返回 null
            } else {
                Result.apiError(response).message // 失败则返回错误信息
            }
        } catch (e: Exception) {
            Result.fromException(e, AppText.get(R.string.error_network_exception)).message
        }
    }

    // 刷新微信公众号列表的私有方法
    private suspend fun refreshWeChatAccounts(): String? {
        return try {
            val response = remoteDataSource.getWeChatAccounts()
            if (response.errorCode == 0 && response.data != null) {
                localDataSource.replaceWeChatAccounts(response.data.map { it.toEntity() })
                null // 成功则返回 null
            } else {
                Result.apiError(response).message // 失败则返回错误信息
            }
        } catch (e: Exception) {
            Result.fromException(e, AppText.get(R.string.error_network_exception)).message
        }
    }
}
