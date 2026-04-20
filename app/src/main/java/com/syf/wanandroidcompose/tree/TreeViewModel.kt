package com.syf.wanandroidcompose.tree

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.syf.wanandroidcompose.R
import com.syf.wanandroidcompose.WanAndroidApplication
import com.syf.wanandroidcompose.common.BaseViewModelOptimized
import com.syf.wanandroidcompose.network.Result
import com.syf.wanandroidcompose.network.RetrofitClient
import com.syf.wanandroidcompose.home.ArticleData // 引入 Home 模块的 ArticleData
import com.syf.wanandroidcompose.utils.NetworkUtils
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class TreeViewModel(private val repository: TreeRepository, private val application: Application) :
    BaseViewModelOptimized<TreeAction, TreeState>() {

    private var currentPage = 0 // 体系文章列表页码从 0 开始
    private var currentCid = 0 // 当前选中的分类 ID

    init {
        // 初始加载体系分类
        sendAction(TreeAction.LoadTree)
    }

    override fun onAction(action: TreeAction, currentState: TreeState?) {
        when (action) {
            is TreeAction.LoadTree -> loadSystemTree()
            is TreeAction.LoadArticles -> loadSystemArticles(action.cid, action.page)
            is TreeAction.SelectCategory -> selectCategory(action.cid)
            is TreeAction.ClickArticle -> toDetail(action.articleId, action.link)
            is TreeAction.LoadMore -> loadMoreArticles()
            is TreeAction.Refresh -> refreshArticles()
            is TreeAction.DetailNavigated ->
                emitState { replayState?.copy(navigateToDetail = null) }
        }
    }

    private fun loadSystemTree() {
        launchAction("LoadSystemTree") {
            repository.getSystemTree()
                .onStart {
                    emitState {
                        replayState?.copy(isLoading = true, errorMsg = null) ?: TreeState(isLoading = true)
                    }
                }
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            emitState {
                                val categories = result.data.map { mapTreeDataToTreeCategory(it) }
                                currentCid = categories.firstOrNull()?.children?.firstOrNull()?.id ?: categories.firstOrNull()?.id ?: 0 // 默认选择第一个二级分类，如果没有则选择一级分类
                                replayState?.copy(
                                    isLoading = false,
                                    categories = categories,
                                    selectedCid = currentCid,
                                    errorMsg = null
                                ) ?: TreeState(
                                    categories = categories,
                                    selectedCid = currentCid
                                )
                            }
                            // 加载默认分类的文章
                            if (currentCid != 0) {
                                sendAction(TreeAction.LoadArticles(currentCid, 0))
                            }
                        }
                        is Result.Error -> {
                            emitState {
                                replayState?.copy(
                                    isLoading = false,
                                    errorMsg = result.message
                                ) ?: TreeState(errorMsg = result.message)
                            }
                        }
                        Result.Loading -> {}
                    }
                }
        }
    }

    private fun mapTreeDataToTreeCategory(treeData: TreeData): TreeCategory {
        return TreeCategory(
            id = treeData.id,
            name = treeData.name,
            children = treeData.children.map { mapTreeDataToTreeCategory(it) }
        )
    }

    private fun loadSystemArticles(cid: Int, page: Int) {
        if (!NetworkUtils.isNetworkAvailable(application)) {
            emitState {
                val currentState = replayState ?: TreeState()
                currentState.copy(
                    isLoading = false,
                    isRefreshing = false,
                    isLoadingMore = false,
                    errorMsg = application.getString(R.string.error_network_unavailable)
                )
            }
            return
        }

        launchAction("LoadSystemArticles") {
            repository.getSystemArticles(page, cid)
                .onStart {
                    // 如果是第一页加载，则显示全屏加载或刷新状态
                    if (page == 0) {
                        emitState {
                            if (replayState?.isRefreshing == true) {
                                replayState?.copy(articles = emptyList(), errorMsg = null)
                            } else {
                                replayState?.copy(isLoading = true, articles = emptyList(), errorMsg = null)
                            }
                        }
                    } else { // 加载更多
                        emitState {
                            replayState?.copy(isLoadingMore = true, errorMsg = null)
                        }
                    }
                }
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            emitState {
                                val currentArticles = if (page == 0) {
                                    result.data
                                } else {
                                    replayState?.articles.orEmpty() + result.data
                                }
                                replayState?.copy(
                                    isLoading = false,
                                    isRefreshing = false,
                                    isLoadingMore = false,
                                    articles = currentArticles,
                                    hasMore = result.data.isNotEmpty(), // TODO: need to get this from API
                                    errorMsg = null
                                ) ?: TreeState(
                                    articles = currentArticles,
                                    hasMore = result.data.isNotEmpty()
                                )
                            }
                        }
                        is Result.Error -> {
                            if (page > 0) currentPage-- // 加载更多失败，页码回退
                            emitState {
                                replayState?.copy(
                                    isLoading = false,
                                    isRefreshing = false,
                                    isLoadingMore = false,
                                    errorMsg = result.message
                                ) ?: TreeState(errorMsg = result.message)
                            }
                        }
                        Result.Loading -> {}
                    }
                }
        }
    }

    private fun selectCategory(cid: Int) {
        if (currentCid == cid) return // 如果选择了相同的分类，则不刷新
        currentCid = cid
        currentPage = 0
        emitState {
            replayState?.copy(selectedCid = cid, articles = emptyList(), hasMore = true)
        }
        sendAction(TreeAction.LoadArticles(currentCid, currentPage))
    }

    private fun toDetail(articleId: String, link: String) {
        emitState { replayState?.copy(navigateToDetail = link) }
    }

    private fun loadMoreArticles() {
        val currentState = replayState ?: return
        if (currentState.isLoadingMore || !currentState.hasMore) return

        currentPage++
        sendAction(TreeAction.LoadArticles(currentCid, currentPage))
    }

    private fun refreshArticles() {
        currentPage = 0
        emitState {
            replayState?.copy(isRefreshing = true, hasMore = true, errorMsg = null)
        }
        sendAction(TreeAction.LoadArticles(currentCid, currentPage))
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as WanAndroidApplication)
                val apiService = RetrofitClient.create<TreeApiService>()
                val repository = TreeRepository(apiService)
                TreeViewModel(repository, application)
            }
        }
    }
}
