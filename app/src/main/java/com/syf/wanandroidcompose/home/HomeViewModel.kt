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

class HomeViewModel(private val repository: HomeRepository, private val application: Application) :
        BaseViewModelOptimized<HomeAction, HomeListState>() {
    private var currentPage = 0

    // 维护所有加载的文章数据，用于前端筛选
    private val allArticles = mutableListOf<ArticleData>()

    init { // 观察本地数据库数据
        viewModelScope.launch {
            repository.homeData.collectLatest { cachedData ->
                allArticles.clear()
                allArticles.addAll(cachedData.articles)
                updateStateWithHomeData(cachedData)
            }
        } // 初始加载
        refreshAllData(isFirstLoad = true)
    }

    /** 使用本地缓存更新状态，自动提取分类并应用当前筛选条件 */
    private fun updateStateWithHomeData(cachedData: HomeCachedData) {
        emitState {
            val categories = extractCategories(cachedData.articles) // 应用当前筛选
            val currentSelectedId = replayState?.selectedCategoryId ?: 0
            val filteredList = filterArticles(cachedData.articles, currentSelectedId)

            replayState?.copy(
                    getArticleData = filteredList,
                    categories = categories,
                    getBannerData = cachedData.banners,
                    getPublicData = cachedData.weChatAccounts
            )
                    ?: HomeListState(
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
            is HomeAction.RefreshAllData -> refreshAllData(isFirstLoad = false)
            is HomeAction.LoadMoreArticle -> loadMoreArticle()
            is HomeAction.SelectCategory -> selectCategory(action.categoryId)
            is HomeAction.DetailNavigated ->
                    emitState { replayState?.copy(navigateToDetail = null) }
            // 已废弃的 Action - 数据现在通过 Flow 自动加载
            is HomeAction.LoadPagerData -> {
                /* 由 Flow 处理 */
            }
            is HomeAction.LoadPublic -> {
                /* 由 Flow 处理 */
            }
            is HomeAction.LoadArticleData -> {
                /* 由 Flow 处理 */
            }
            is HomeAction.LoadTab -> {
                /* 由 Flow 处理 */
            }
        }
    }

    /** 跳转到文章详情页（临时方法） */
    private fun tcDetail(articleId: String) {
        emitState { replayState?.copy(navigateToDetail = articleId) }
    }

    /** 跳转到文章详情页 */
    private fun toDetail(articleId: String) {
        emitState { replayState?.copy(navigateToDetail = articleId) }
    }

    /** 加载用户文章（预留方法） */
    private fun loadUserArticle(userId: String) {}

    /** 加载文章数据（兼容性方法） */
    private fun loadArticleData() { // 兼容性方法，实际逻辑在 refreshAllData 中
        refreshAllData(isFirstLoad = false)
    }

    /**
     * 选择分类并筛选文章
     * @param categoryId 分类 ID，0 表示全部
     */
    private fun selectCategory(categoryId: Int) {
        emitState {
            val filteredList = filterArticles(allArticles, categoryId)
            replayState?.copy(selectedCategoryId = categoryId, getArticleData = filteredList)
        }
    }

    /**
     * 根据分类 ID 筛选文章
     * @param articles 待筛选的文章列表
     * @param categoryId 分类 ID，0 表示全部
     * @return 筛选后的文章列表
     */
    private fun filterArticles(articles: List<ArticleData>, categoryId: Int): List<ArticleData> {
        return if (categoryId == 0) {
            articles
        } else {
            articles.filter { it.superChapterId == categoryId }
        }
    }

    // 从文章列表中提取分类标签
    private fun extractCategories(articles: List<ArticleData>): List<CategoryUiModel> {
        val categories = mutableListOf<CategoryUiModel>() // 默认添加"全部"
        categories.add(
                CategoryUiModel(application.getString(R.string.label_all), 0)
        ) // 提取不重复的 superChapterName 和 superChapterId
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

    private fun loadMoreArticle() { // 检查网络可用性
        if (!NetworkUtils.isNetworkAvailable(application)) {
            emitState {
                replayState?.copy(
                        isLoadingMore = false,
                        errorMsg = application.getString(R.string.error_network_unavailable)
                )
            }
            return
        }
        val currentState = replayState ?: return

        if (currentState.isLoadingMore || !currentState.hasMore) return

        currentPage++

        launchAction("LoadMoreArticle") {
            repository
                    .fetchMoreArticles(currentPage)
                    .onStart {
                        emitState {
                            replayState?.copy(isLoadingMore = true, errorMsg = null)
                                    ?: HomeListState(isLoadingMore = true)
                        }
                    }
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> { // 已在 onStart 中处理
                            }
                            is Result.Success -> { // 数据通过数据库 Flow 自动更新
                                //  // local DB
                                emitState {
                                    replayState?.copy(
                                            isLoadingMore = false,
                                            hasMore = !result.data.over,
                                            errorMsg = null
                                    )
                                            ?: HomeListState()
                                }
                            }
                            is Result.Error -> {
                                currentPage-- // Rollback page on error
                                emitState {
                                    replayState?.copy(
                                            isLoadingMore = false,
                                            errorMsg = result.message
                                    )
                                            ?: HomeListState()
                                }
                            }
                        }
                    }
        }
    }

    private fun refreshAllData(isFirstLoad: Boolean) { // 检查网络可用性
        if (!NetworkUtils.isNetworkAvailable(application)) {
            emitState {
                val currentState = replayState ?: HomeListState()
                currentState.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMsg = application.getString(R.string.error_network_unavailable)
                )
            }
            return
        }
        // 重置分页
        currentPage = 0
        launchAction("RefreshFinish") {
            repository.refreshHomeData().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        emitState {
                            val defaultState = HomeListState(selectedCategoryId = 0)
                            if (isFirstLoad) {
                                replayState?.copy(
                                        isLoading = true,
                                        selectedCategoryId = 0,
                                        errorMsg = null
                                )
                                        ?: defaultState.copy(isLoading = true)
                            } else {
                                replayState?.copy(
                                        isRefreshing = true,
                                        hasMore = true,
                                        selectedCategoryId = 0,
                                        errorMsg = null
                                )
                                        ?: defaultState.copy(isRefreshing = true, errorMsg = null)
                            }
                        }
                    }
                    is Result.Success -> {
                        emitState {
                            val state = replayState ?: HomeListState()
                            state.copy(
                                    isLoading = false,
                                    isRefreshing = false,
                                    hasMore = result.data.hasMore ?: state.hasMore,
                                    errorMsg = result.data.errorMessage
                            )
                        }
                    }
                    is Result.Error -> {
                        emitState {
                            val state = replayState ?: HomeListState()
                            state.copy(
                                    isLoading = false,
                                    isRefreshing = false,
                                    errorMsg = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as WanAndroidApplication)
                val remoteDataSource =
                        HomeRemoteDataSource(RetrofitClient.create<HomeApiService>())
                val localDataSource = HomeLocalDataSource(application.database.homeDao())
                val repository =
                        HomeRepository(
                                remoteDataSource = remoteDataSource,
                                localDataSource = localDataSource
                        )
                HomeViewModel(repository, application)
            }
        }
    }
}
