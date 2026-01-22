package com.syf.wanandroidcompose.ui.network.example

import androidx.lifecycle.viewModelScope
import com.syf.wanandroidcompose.ui.common.BaseViewModel
import com.syf.wanandroidcompose.ui.common.Action
import com.syf.wanandroidcompose.ui.common.State
import com.syf.wanandroidcompose.ui.network.ApiResult
import com.syf.wanandroidcompose.ui.network.ArticleData
import com.syf.wanandroidcompose.ui.network.BannerData
import com.syf.wanandroidcompose.ui.network.RetrofitClient
import com.syf.wanandroidcompose.ui.network.WanAndroidApiService
import com.syf.wanandroidcompose.ui.network.apiRequest
import kotlinx.coroutines.launch

/**
 * 示例：如何在 MVI 架构中使用网络层
 * 
 * 这是一个完整的示例，展示了如何在 ViewModel 中使用封装好的网络请求
 */

// ==================== Action 定义 ====================
sealed class ExampleAction : Action {
    data object LoadHomeData : ExampleAction()
    data object LoadBanner : ExampleAction()
    data object LoadArticles : ExampleAction()
    data object RefreshData : ExampleAction()
    data class LoadPage(val page: Int) : ExampleAction()
}

// ==================== State 定义 ====================
data class ExampleState(
    val isLoading: Boolean = false,
    val banners: List<BannerData> = emptyList(),
    val articles: List<ArticleData> = emptyList(),
    val error: String? = null,
    val currentPage: Int = 0,
    val hasMore: Boolean = true
) : State

// ==================== ViewModel 实现 ====================
class ExampleViewModel : BaseViewModel<ExampleAction, ExampleState>() {

    // 创建 API 服务实例
    private val apiService = RetrofitClient.create<WanAndroidApiService>()

    override fun onAction(action: ExampleAction, currentState: ExampleState?) {
        when (action) {
            is ExampleAction.LoadHomeData -> loadHomeData()
            is ExampleAction.LoadBanner -> loadBanner()
            is ExampleAction.LoadArticles -> loadArticles()
            is ExampleAction.RefreshData -> refreshData()
            is ExampleAction.LoadPage -> loadPage(action.page)
        }
    }

    /**
     * 示例 1：加载首页数据（Banner + 文章列表）
     * 并发请求多个接口
     */
    private fun loadHomeData() {
        viewModelScope.launch {
            // 先设置加载状态
            emitState(ExampleState(isLoading = true))

            // 并发请求
            launch { loadBannerInternal() }
            launch { loadArticlesInternal() }
        }
    }

    /**
     * 示例 2：加载 Banner
     * 演示基本的网络请求使用
     */
    private fun loadBanner() {
        viewModelScope.launch {
            apiRequest { apiService.getBanner() }
                .collect { result ->
                    when (result) {
                        is ApiResult.Loading -> {
                            emitState(replayState?.copy(isLoading = true) ?: ExampleState(isLoading = true))
                        }
                        is ApiResult.Success -> {
                            emitState(
                                replayState?.copy(
                                    isLoading = false,
                                    banners = result.data,
                                    error = null
                                ) ?: ExampleState(banners = result.data)
                            )
                        }
                        is ApiResult.Error -> {
                            emitState(
                                replayState?.copy(
                                    isLoading = false,
                                    error = result.message
                                ) ?: ExampleState(error = result.message)
                            )
                        }
                    }
                }
        }
    }

    /**
     * 示例 3：加载文章列表
     * 演示链式调用
     */
    private fun loadArticles() {
        viewModelScope.launch {
            apiRequest { apiService.getArticleList(0) }
                .collect { result ->
                    result
                        .onLoading {
                            emitState(replayState?.copy(isLoading = true) ?: ExampleState(isLoading = true))
                        }
                        .onSuccess { data ->
                            emitState(
                                replayState?.copy(
                                    isLoading = false,
                                    articles = data.datas,
                                    currentPage = data.curPage,
                                    hasMore = !data.over,
                                    error = null
                                ) ?: ExampleState(
                                    articles = data.datas,
                                    currentPage = data.curPage,
                                    hasMore = !data.over
                                )
                            )
                        }
                        .onError { _, message, _ ->
                            emitState(
                                replayState?.copy(
                                    isLoading = false,
                                    error = message
                                ) ?: ExampleState(error = message)
                            )
                        }
                }
        }
    }

    /**
     * 示例 4：刷新数据
     * 重新加载第一页
     */
    private fun refreshData() {
        viewModelScope.launch {
            emitState(replayState?.copy(isLoading = true) ?: ExampleState(isLoading = true))
            
            apiRequest { apiService.getArticleList(0) }
                .collect { result ->
                    if (result is ApiResult.Success) {
                        emitState(
                            ExampleState(
                                isLoading = false,
                                articles = result.data.datas,
                                currentPage = 0,
                                hasMore = !result.data.over,
                                error = null
                            )
                        )
                    } else if (result is ApiResult.Error) {
                        emitState(replayState?.copy(isLoading = false, error = result.message) ?: ExampleState())
                    }
                }
        }
    }

    /**
     * 示例 5：加载更多（分页）
     * 演示分页加载
     */
    private fun loadPage(page: Int) {
        val currentState = replayState ?: return
        if (!currentState.hasMore || currentState.isLoading) return

        viewModelScope.launch {
            apiRequest { apiService.getArticleList(page) }
                .collect { result ->
                    result
                        .onLoading {
                            emitState(currentState.copy(isLoading = true))
                        }
                        .onSuccess { data ->
                            emitState(
                                currentState.copy(
                                    isLoading = false,
                                    articles = currentState.articles + data.datas, // 追加数据
                                    currentPage = data.curPage,
                                    hasMore = !data.over,
                                    error = null
                                )
                            )
                        }
                        .onError { _, message, _ ->
                            emitState(currentState.copy(isLoading = false, error = message))
                        }
                }
        }
    }

    // ==================== 内部辅助方法 ====================

    private suspend fun loadBannerInternal() {
        apiRequest { apiService.getBanner() }
            .collect { result ->
                if (result is ApiResult.Success) {
                    emitState(replayState?.copy(banners = result.data) ?: ExampleState(banners = result.data))
                }
            }
    }

    private suspend fun loadArticlesInternal() {
        apiRequest { apiService.getArticleList(0) }
            .collect { result ->
                if (result is ApiResult.Success) {
                    emitState(
                        replayState?.copy(
                            isLoading = false,
                            articles = result.data.datas,
                            currentPage = 0,
                            hasMore = !result.data.over
                        ) ?: ExampleState(
                            articles = result.data.datas,
                            currentPage = 0,
                            hasMore = !result.data.over
                        )
                    )
                }
            }
    }
}
