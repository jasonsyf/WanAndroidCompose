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

    init { // 观察本地数据库数据
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
        } // 初始加载
        refreshAllData(isFirstLoad = true)
    }

    /** 使用文章列表更新状态 自动提取分类并应用当前筛选条件 */
    private fun updateStateWithArticles(articles: List<ArticleData>) {
        emitState {
            val categories = extractCategories(articles) // 应用当前筛选
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
        categories.add(CategoryUiModel("全部", 0)) // 提取不重复的 superChapterName 和 superChapterId
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
                        errorMsg = "网络不可用，请检查网络连接"
                )
            }
            return
        } // 重置分页
        currentPage = 0
        allArticles.clear()

        launchAction("RefreshFinish") { // 合并所有三个网络请求到一个 Flow
            combine(
                            repository.fetchBanners(),
                            repository.fetchArticles(),
                            repository.fetchWeChatAccounts()
                    ) { bannerResult, articleResult, accountResult ->
                Triple(bannerResult, articleResult, accountResult)
            }
                    .onStart { // 在开始时发出加载状态
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
                    .collect { (bannerResult, articleResult, accountResult) -> // 判断是否有请求仍在加载
                        val isLoading =
                                listOf(bannerResult, articleResult, accountResult).any {
                                    it is Result.Loading
                                } // 收集错误信息
                        val errors =
                                listOf(bannerResult, articleResult, accountResult)
                                        .filterIsInstance<Result.Error>()
                                        .map { it.message }

                        emitState {
                            var state = replayState ?: HomeListState() // 处理文章请求结果并更新状态
                            if (articleResult is Result.Success) {
                                state =
                                        state.copy(
                                                hasMore = !articleResult.data.over,
                                                selectedCategoryId = 0
                                        )
                            } // 更新加载状态和错误信息
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
