package com.syf.wanandroidcompose.home

import com.syf.wanandroidcompose.common.BaseViewModelOptimized
import com.syf.wanandroidcompose.network.RetrofitClient
import kotlinx.coroutines.async

class HomeViewModel : BaseViewModelOptimized<HomeAction, HomeListState>() {

    private val apiService by lazy { RetrofitClient.create<HomeApiService>() }

    private var currentPage = 0

    // 维护所有加载的文章数据，用于前端筛选
    private val allArticles = mutableListOf<ArticleData>()

    init {
        // 初始加载
        refreshAllData(isFirstLoad = true)
    }

    override fun onAction(action: HomeAction, currentState: HomeListState?) {
        when (action) {
            is HomeAction.LoadTab -> loadTab()
            is HomeAction.ClickArticle -> toDetail(action.articleId)
            is HomeAction.ClickUser -> loadUserArticle(action.userId)
            is HomeAction.LoadArticleData -> loadArticleData()
            is HomeAction.LoadMoreArticle -> loadMoreArticle()
            is HomeAction.LoadPagerData -> loadPagerData()
            is HomeAction.LoadPublic -> loadPublic()
            is HomeAction.RefreshAllData -> refreshAllData(isFirstLoad = false)
            is HomeAction.SelectCategory -> selectCategory(action.categoryId)
            is HomeAction.DetailNavigated ->
                    emitState { replayState?.copy(navigateToDetail = null) }
        }
    }

    private fun loadTab() {}

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
        val list =
                articles
                        .distinctBy { it.superChapterId }
                        .map { CategoryUiModel(it.superChapterName, it.superChapterId) }
                        .toMutableList()
        categories.addAll(list)
        //        articles.forEach { article ->
        //            val id = article.superChapterId
        //            if (id != 0 && !seenIds.contains(id) && article.superChapterName.isNotEmpty())
        // {
        //                categories.add(CategoryUiModel(article.superChapterName, id))
        //                seenIds.add(id)
        //            }
        //        }
        return categories
    }

    private fun loadMoreArticle() {
        val currentState = replayState ?: return
        if (currentState.isLoadingMore || !currentState.hasMore) return

        emitState { currentState.copy(isLoadingMore = true) }

        launchAction("LoadMoreArticle") {
            try {
                val nextPage = currentPage + 1
                val response = apiService.getArticleList(nextPage)
                val data = response.data
                if (response.errorCode == 0 && data != null) {
                    currentPage = nextPage

                    // 更新总数据源
                    allArticles.addAll(data.datas)

                    emitState {
                        val currentCategories = extractCategories(allArticles)
                        // 应用当前筛选
                        val currentSelectedId = replayState?.selectedCategoryId ?: 0
                        val filteredList = filterArticles(allArticles, currentSelectedId)

                        replayState?.copy(
                                getArticleData = filteredList,
                                categories = currentCategories,
                                isLoadingMore = false,
                                hasMore = !data.over
                        )
                    }
                } else {
                    emitState {
                        replayState?.copy(isLoadingMore = false, errorMsg = response.errorMsg)
                    }
                }
            } catch (e: Exception) {
                emitState { replayState?.copy(isLoadingMore = false, errorMsg = e.message) }
            }
        }
    }

    private fun loadPagerData() {
        launchAction("LoadBanner") {
            try {
                val response = apiService.getBanner()
                val data = response.data
                if (response.errorCode == 0 && data != null) {
                    emitState {
                        replayState?.copy(getBannerData = data)
                                ?: HomeListState(getBannerData = data)
                    }
                }
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    private fun loadPublic() {
        launchAction("LoadPublic") {
            try {
                val response = apiService.getWeChatAccounts()
                val data = response.data
                if (response.errorCode == 0 && data != null) {
                    emitState {
                        replayState?.copy(getPublicData = data)
                                ?: HomeListState(getPublicData = data)
                    }
                }
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    private fun refreshAllData(isFirstLoad: Boolean) {
        // 重置分页和数据源
        currentPage = 0
        allArticles.clear()

        emitState {
            // 重置选中状态为“全部”
            val defaultState = HomeListState(selectedCategoryId = 0)

            if (isFirstLoad) {
                replayState?.copy(isLoading = true, selectedCategoryId = 0)
                        ?: defaultState.copy(isLoading = true)
            } else {
                replayState?.copy(isRefreshing = true, hasMore = true, selectedCategoryId = 0)
                        ?: defaultState.copy(isRefreshing = true)
            }
        }

        launchAction("RefreshFinish") {
            try {
                val bannerDeferred = async { apiService.getBanner() }
                val articleDeferred = async { apiService.getArticleList(0) }
                val publicDeferred = async { apiService.getWeChatAccounts() }

                val bannerRes = bannerDeferred.await()
                val articleRes = articleDeferred.await()
                val publicRes = publicDeferred.await()

                emitState {
                    var state = replayState ?: HomeListState()
                    val bannerData = bannerRes.data
                    if (bannerRes.errorCode == 0 && bannerData != null) {
                        state = state.copy(getBannerData = bannerData)
                    }
                    val articleData = articleRes.data
                    if (articleRes.errorCode == 0 && articleData != null) {
                        allArticles.addAll(articleData.datas)
                        val categories = extractCategories(allArticles)
                        // 刷新后默认显示全部，不需要过滤，直接用 allArticles
                        state =
                                state.copy(
                                        getArticleData = ArrayList(allArticles), // Copy list
                                        categories = categories,
                                        hasMore = !articleData.over,
                                        selectedCategoryId = 0
                                )
                    }
                    val publicData = publicRes.data
                    if (publicRes.errorCode == 0 && publicData != null) {
                        state = state.copy(getPublicData = publicData)
                    }
                    state.copy(isLoading = false, isRefreshing = false)
                }
            } catch (e: Exception) {
                emitState {
                    replayState?.copy(isLoading = false, isRefreshing = false, errorMsg = e.message)
                }
            }
        }
    }
}
