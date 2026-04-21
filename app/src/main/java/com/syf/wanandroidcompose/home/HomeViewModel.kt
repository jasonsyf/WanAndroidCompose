package com.syf.wanandroidcompose.home

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.syf.wanandroidcompose.R
import com.syf.wanandroidcompose.WanAndroidApplication
import com.syf.wanandroidcompose.common.BaseViewModelOptimized
import com.syf.wanandroidcompose.home.HomeLocalDataSource
import com.syf.wanandroidcompose.home.HomeRemoteDataSource
import com.syf.wanandroidcompose.network.Result
import com.syf.wanandroidcompose.network.RetrofitClient
import com.syf.wanandroidcompose.utils.NetworkUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

/**
 * 首页 ViewModel
 *
 * 负责处理首页的业务逻辑，连接数据仓库 (Repository) 和 UI 视图 (View)。
 */
class HomeViewModel(private val repository: HomeRepository, private val application: Application) :
        BaseViewModelOptimized<HomeAction, HomeListState>() {

    // 当前加载的文章页码
    private var currentPage = 0

    // 内存中缓存的所有文章数据，用于前端的分类筛选
    private val allArticles = mutableListOf<ArticleData>()

    init {
        // 1. 订阅数据仓库中的聚合数据流
        viewModelScope.launch {
            repository.homeData.collectLatest { cachedData ->
                // 更新内存缓存
                allArticles.clear()
                allArticles.addAll(cachedData.articles)
                // 使用数据库的最新数据更新UI状态
                updateStateWithHomeData(cachedData)
            }
        }
        // 2. 首次进入页面时，静默刷新一次数据
        refreshAllData(isFirstLoad = true, isSilent = true)
    }

    /**
     * 使用从数据库获取的缓存数据更新UI状态。
     * @param cachedData 包含文章、轮播图、公众号的聚合数据。
     */
    private fun updateStateWithHomeData(cachedData: HomeCachedData) {
        emitState {
            val categories = extractCategories(cachedData.articles) // 从文章中提取分类
            val currentSelectedId = replayState?.selectedCategoryId ?: 0 // 获取当前选中的分类ID
            val filteredList = filterArticles(cachedData.articles, currentSelectedId) // 应用分类筛选

            val currentState = replayState ?: HomeListState()
            currentState.copy(
                    getArticleData = filteredList,
                    categories = categories,
                    getBannerData = cachedData.banners,
                    getPublicData = cachedData.weChatAccounts
            )
        }
    }

    override fun onAction(action: HomeAction, currentState: HomeListState?) {
        when (action) {
            is HomeAction.ClickArticle -> toDetail(action.articleId)
            is HomeAction.ClickUser -> loadUserArticle(action.userId)
            is HomeAction.RefreshAllData -> refreshAllData(isFirstLoad = false, isSilent = false)
            is HomeAction.LoadMoreArticle -> loadMoreArticle()
            is HomeAction.SelectCategory -> selectCategory(action.categoryId)
            is HomeAction.DetailNavigated -> emitState { replayState?.copy(navigateToDetail = null) }
            // 以下为已废弃的Action，因为数据加载已完全由Flow驱动
            is HomeAction.LoadPagerData, is HomeAction.LoadPublic,
            is HomeAction.LoadArticleData, is HomeAction.LoadTab -> { /* 由 Flow 自动处理 */ }
        }
    }

    /**
     * 设置状态以导航到文章详情页。
     * @param articleId 文章链接URL。
     */
    private fun toDetail(articleId: String) {
        emitState { replayState?.copy(navigateToDetail = articleId) }
    }

    /**
     * 加载特定用户的文章（功能预留）。
     */
    private fun loadUserArticle(userId: String) {}

    /**
     * 选择分类并根据分类ID筛选文章列表。
     * @param categoryId 分类ID，0代表“全部”。
     */
    private fun selectCategory(categoryId: Int) {
        emitState {
            val filteredList = filterArticles(allArticles, categoryId)
            replayState?.copy(selectedCategoryId = categoryId, getArticleData = filteredList)
        }
    }

    /**
     * 根据分类ID从文章列表中筛选文章。
     * @return 筛选后的文章列表。
     */
    private fun filterArticles(articles: List<ArticleData>, categoryId: Int): List<ArticleData> {
        return if (categoryId == 0) articles else articles.filter { it.superChapterId == categoryId }
    }

    /**
     * 从文章列表中提取出不重复的分类信息。
     */
    private fun extractCategories(articles: List<ArticleData>): List<CategoryUiModel> {
        val categories = mutableListOf(CategoryUiModel(application.getString(R.string.label_all), 0))
        val seenIds = mutableSetOf<Int>()
        articles.forEach { article ->
            val id = article.superChapterId
            if (id != 0 && !seenIds.contains(id) && article.superChapterName.isNotEmpty()) {
                seenIds.add(id)
                categories.add(CategoryUiModel(article.superChapterName, id))
            }
        }
        return categories
    }

    /**
     * 加载更多文章。
     */
    private fun loadMoreArticle() {
        if (!NetworkUtils.isNetworkAvailable(application)) {
            emitState { replayState?.copy(isLoadingMore = false, errorMsg = application.getString(R.string.error_network_unavailable)) }
            return
        }

        val currentState = replayState ?: return
        if (currentState.isLoadingMore || !currentState.hasMore) return

        currentPage++

        launchAction("LoadMoreArticle") {
            repository.fetchMoreArticles(currentPage)
                .onStart { emitState { replayState?.copy(isLoadingMore = true, errorMsg = null) } }
                .collect { result ->
                    when (result) {
                        is Result.Loading -> { /* 在 onStart 中已处理 */ }
                        is Result.Success -> {
                            // 数据已通过 repository->local DB->Flow 自动更新到UI
                            emitState { replayState?.copy(isLoadingMore = false, hasMore = !result.data.over, errorMsg = null) }
                        }
                        is Result.Error -> {
                            currentPage-- // 加载失败，回滚页码
                            emitState { replayState?.copy(isLoadingMore = false, errorMsg = result.message) }
                        }
                    }
                }
        }
    }

    /**
     * 刷新所有首页数据（文章、轮播图、公众号）。
     * @param isFirstLoad 是否是首次加载。
     * @param isSilent 是否是静默刷新（不显示下拉刷新动画）。
     */
    private fun refreshAllData(isFirstLoad: Boolean, isSilent: Boolean = false) {
        val hasCache = allArticles.isNotEmpty()
        if (!NetworkUtils.isNetworkAvailable(application)) {
            // 仅在没有缓存且非静默刷新时显示网络错误
            if (!hasCache && !isSilent) {
                emitState { (replayState ?: HomeListState()).copy(isLoading = false, isRefreshing = false, errorMsg = application.getString(R.string.error_network_unavailable)) }
            } else { // 如果有缓存或静默刷新，则只停止动画
                emitState { (replayState ?: HomeListState()).copy(isLoading = false, isRefreshing = false) }
            }
            return
        }

        currentPage = 0 // 重置分页
        launchAction("RefreshAllData") {
            repository.refreshHomeData().collect { result ->
                val currentState = replayState ?: HomeListState()
                when (result) {
                    is Result.Loading -> {
                        val loadingState = when {
                            isFirstLoad && !hasCache -> currentState.copy(isLoading = true, errorMsg = null)
                            !isSilent -> currentState.copy(isRefreshing = true, hasMore = true, errorMsg = null)
                            else -> currentState // 静默刷新，UI状态不变
                        }
                        emitState { loadingState.copy(selectedCategoryId = 0) } // 重置分类
                    }
                    is Result.Success -> {
                        emitState { currentState.copy(isLoading = false, isRefreshing = false, hasMore = result.data.hasMore ?: currentState.hasMore, errorMsg = result.data.errorMessage) }
                    }
                    is Result.Error -> {
                        // 仅在对用户有意义的情况下显示错误（非静默或无缓存）
                        if (!isSilent || !hasCache) {
                            emitState { currentState.copy(isLoading = false, isRefreshing = false, errorMsg = result.message) }
                        } else {
                            emitState { currentState.copy(isLoading = false, isRefreshing = false) }
                        }
                    }
                }
            }
        }
    }

    companion object {
        // ViewModel 工厂，用于创建 HomeViewModel 实例并注入依赖
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as WanAndroidApplication)
                val remoteDataSource = HomeRemoteDataSource(RetrofitClient.create<HomeApiService>())
                val localDataSource = HomeLocalDataSource(application.database.homeDao())
                val repository = HomeRepository(remoteDataSource = remoteDataSource, localDataSource = localDataSource)
                HomeViewModel(repository, application)
            }
        }
    }
}
