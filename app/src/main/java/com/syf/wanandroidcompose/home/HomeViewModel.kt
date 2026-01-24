package com.syf.wanandroidcompose.home

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.syf.wanandroidcompose.WanAndroidApplication
import com.syf.wanandroidcompose.common.BaseViewModelOptimized
import com.syf.wanandroidcompose.network.Result
import com.syf.wanandroidcompose.network.RetrofitClient
import com.syf.wanandroidcompose.utils.NetworkUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: HomeRepository, private val application: Application) :
    BaseViewModelOptimized<HomeAction, HomeListState>() {

    private var currentPage = 0

    // 维护所有加载的文章数据，用于前端筛选
    private val allArticles = mutableListOf<ArticleData>()

    init {
        // 观察本地数据库数据
        viewModelScope.launch {
            repository.articles.collectLatest { articles ->
                allArticles.clear()
                allArticles.addAll(articles)
                updateStateWithArticles(articles)
            }
        }
        viewModelScope.launch {
            repository.banners.collectLatest { banners ->
                emitState {
                    replayState?.copy(getBannerData = banners)
                        ?: HomeListState(getBannerData = banners)
                }
            }
        }
        viewModelScope.launch {
            repository.weChatAccounts.collectLatest { accounts ->
                emitState {
                    replayState?.copy(getPublicData = accounts)
                        ?: HomeListState(getPublicData = accounts)
                }
            }
        }
        // 初始加载
        refreshAllData(isFirstLoad = true)
    }

    private fun updateStateWithArticles(articles: List<ArticleData>) {
        emitState {
            val categories = extractCategories(articles)
            // 应用当前筛选
            val currentSelectedId = replayState?.selectedCategoryId ?: 0
            val filteredList = filterArticles(articles, currentSelectedId)

            replayState?.copy(getArticleData = filteredList, categories = categories)
                ?: HomeListState(getArticleData = filteredList, categories = categories)
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
            // Obsolete actions - data now loads automatically via Flow
            is HomeAction.LoadPagerData -> {
                /* Handled by Flow */
            }

            is HomeAction.LoadPublic -> {
                /* Handled by Flow */
            }

            is HomeAction.LoadArticleData -> {
                /* Handled by Flow */
            }

            is HomeAction.LoadTab -> {
                /* Handled by Flow */
            }
        }
    }

    private fun tcDetail(articleId: String) {
        emitState { replayState?.copy(navigateToDetail = articleId) }
    }

    private fun toDetail(articleId: String) {
        emitState { replayState?.copy(navigateToDetail = articleId) }
    }

    private fun loadUserArticle(userId: String) {}

    private fun loadArticleData() {
        // 兼容性方法，实际逻辑主要在 refreshAllData
        refreshAllData(isFirstLoad = false)
    }

    private fun selectCategory(categoryId: Int) {
        emitState {
            val filteredList = filterArticles(allArticles, categoryId)
            replayState?.copy(selectedCategoryId = categoryId, getArticleData = filteredList)
        }
    }

    private fun filterArticles(articles: List<ArticleData>, categoryId: Int): List<ArticleData> {
        return if (categoryId == 0) {
            articles
        } else {
            articles.filter { it.superChapterId == categoryId }
        }
    }

    // 从文章列表中提取分类
    private fun extractCategories(articles: List<ArticleData>): List<CategoryUiModel> {
        val categories = mutableListOf<CategoryUiModel>()
        // 默认添加“全部”
        categories.add(CategoryUiModel("全部", 0))

        // 提取不重复的 superChapterName 和 superChapterId
        val seenIds = mutableSetOf<Int>()
//        val list =
//                articles
//                        .distinctBy { it.superChapterId }
//                        .map { CategoryUiModel(it.superChapterName, it.superChapterId) }
//                        .toMutableList()
//        categories.addAll(list)
        articles.forEach { article ->
            val id = article.superChapterId
            if (id != 0 && !seenIds.contains(id) && article.superChapterName.isNotEmpty()) {
                categories.add(CategoryUiModel(article.superChapterName, id))
                seenIds.add(id)
            }
        }
        return categories
    }

    private fun loadMoreArticle() {
        // Check network availability
        if (!NetworkUtils.isNetworkAvailable(application)) {
            emitState { replayState?.copy(isLoadingMore = false, errorMsg = "网络不可用，请检查网络连接") }
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
                        is Result.Loading -> {
                            // Already handled in onStart
                        }

                        is Result.Success -> {
                            // Data is automatically updated via Flow from local DB
                            emitState {
                                replayState?.copy(
                                    isLoadingMore = false,
                                    hasMore = !result.data.over,
                                    errorMsg = null
                                ) ?: HomeListState()
                            }
                        }

                        is Result.Error -> {
                            currentPage-- // Rollback page on error
                            emitState {
                                replayState?.copy(
                                    isLoadingMore = false,
                                    errorMsg = result.message
                                ) ?: HomeListState()
                            }
                        }
                    }
                }
        }
    }

    private fun refreshAllData(isFirstLoad: Boolean) {
        // Check network availability
        if (!NetworkUtils.isNetworkAvailable(application)) {
            emitState {
                val currentState = replayState ?: HomeListState()
                currentState.copy(
                    isLoading = false,
                    isRefreshing = false,
                    errorMsg = "网络不可用，请检查网络连接"
                )
            }
            return
        }

        // Reset pagination
        currentPage = 0
        allArticles.clear()

        launchAction("RefreshFinish") {
            // Combine all three network requests into a single Flow
            combine(
                repository.fetchBanners(),
                repository.fetchArticles(),
                repository.fetchWeChatAccounts()
            ) { bannerResult, articleResult, accountResult ->
                Triple(bannerResult, articleResult, accountResult)
            }
                .onStart {
                    // Emit loading state at the beginning
                    emitState {
                        val defaultState = HomeListState(selectedCategoryId = 0)
                        if (isFirstLoad) {
                            replayState?.copy(
                                isLoading = true,
                                selectedCategoryId = 0,
                                errorMsg = null
                            ) ?: defaultState.copy(isLoading = true)
                        } else {
                            replayState?.copy(
                                isRefreshing = true,
                                hasMore = true,
                                selectedCategoryId = 0,
                                errorMsg = null
                            ) ?: defaultState.copy(isRefreshing = true, errorMsg = null)
                        }
                    }
                }
                .collect { (bannerResult, articleResult, accountResult) ->
                    // Determine if any request is still loading
                    val isLoading =
                        listOf(bannerResult, articleResult, accountResult)
                            .any { it is Result.Loading }

                    // Collect error messages
                    val errors =
                        listOf(bannerResult, articleResult, accountResult)
                            .filterIsInstance<Result.Error>()
                            .map { it.message }

                    emitState {
                        var state = replayState ?: HomeListState()

                        // Handle article result状态更新
                        if (articleResult is Result.Success) {
                            state =
                                state.copy(
                                    hasMore = !articleResult.data.over,
                                    selectedCategoryId = 0)
                        }

                        // Update loading states and error message
                        state.copy(
                            isLoading = if (isFirstLoad) isLoading else false,
                            isRefreshing = if (!isFirstLoad) isLoading else false,
                            errorMsg = errors.firstOrNull() // Show first error if any
                        )
                    }
                }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as WanAndroidApplication)
                val repository =
                    HomeRepository(
                        RetrofitClient.create<HomeApiService>(),
                        application.database.homeDao()
                    )
                HomeViewModel(repository, application)
            }
        }
    }
}
