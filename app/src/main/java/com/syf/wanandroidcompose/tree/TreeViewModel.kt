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

/**
 * 体系模块 ViewModel
 * 负责处理体系分类及其下文章列表的分页加载、分类切换等逻辑
 */
class TreeViewModel(private val repository: TreeRepository, private val application: Application) :
    BaseViewModelOptimized<TreeAction, TreeState>() {

    // 体系文章列表页码从 0 开始
    private var currentPage = 0
    // 当前选中的子分类 ID
    private var currentCid = 0

    init {
        // 初始加载体系结构
        sendAction(TreeAction.LoadTree)
    }

    override fun onAction(action: TreeAction, currentState: TreeState?) {
        when (action) {
            is TreeAction.LoadTree -> loadSystemTree()
            is TreeAction.LoadArticles -> loadSystemArticles(action.cid, action.page)
            is TreeAction.SelectCategory -> selectCategory(action.cid)
            is TreeAction.ClickArticle -> { /* 已经在 UI 层直接处理跳转，此处保留接口以防后用 */ }
            is TreeAction.LoadMore -> loadMoreArticles()
            is TreeAction.Refresh -> refreshArticles()
            is TreeAction.DetailNavigated ->
                emitState { replayState?.copy(navigateToDetail = null) }
        }
    }

    /**
     * 加载体系结构树
     */
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
                            // 转换原始数据为 UI 模型
                            val categories = result.data.map { mapTreeDataToTreeCategory(it) }
                            // 展平所有子分类，方便在 TabRow 中展示
                            val allSubCategories = categories.flatMap { it.children }
                            
                            // 默认选择第一个可用的 CID
                            if (currentCid == 0) {
                                currentCid = allSubCategories.firstOrNull()?.id ?: categories.firstOrNull()?.id ?: 0
                            }

                            emitState {
                                replayState?.copy(
                                    isLoading = false,
                                    categories = categories,
                                    allSubCategories = allSubCategories,
                                    selectedCid = currentCid,
                                    errorMsg = null
                                ) ?: TreeState(
                                    categories = categories,
                                    allSubCategories = allSubCategories,
                                    selectedCid = currentCid
                                )
                            }
                            // 成功加载分类后，如果文章列表为空，则触发首次加载文章
                            if (currentCid != 0 && (replayState?.articles?.isEmpty() == true)) {
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

    /**
     * 映射原始接口数据到 UI 模型（递归处理子分类）
     */
    private fun mapTreeDataToTreeCategory(treeData: TreeData): TreeCategory {
        return TreeCategory(
            id = treeData.id,
            name = treeData.name,
            children = treeData.children.map { mapTreeDataToTreeCategory(it) }
        )
    }

    /**
     * 加载体系下的文章列表
     * @param cid 子分类ID
     * @param page 页码
     */
    private fun loadSystemArticles(cid: Int, page: Int) {
        val currentState = replayState ?: TreeState()
        // 避免重复加载
        if (page > 0 && (currentState.isLoadingMore || !currentState.hasMore)) return
        if (page == 0 && currentState.isLoading && currentState.selectedCid == cid && currentState.articles.isNotEmpty()) return

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
                    if (page == 0) {
                        emitState {
                            if (replayState?.isRefreshing == true) {
                                replayState?.copy(articles = emptyList(), errorMsg = null)
                            } else {
                                replayState?.copy(isLoading = true, articles = emptyList(), errorMsg = null)
                            }
                        }
                    } else {
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
                                    hasMore = result.data.isNotEmpty(),
                                    errorMsg = null
                                ) ?: TreeState(
                                    articles = currentArticles,
                                    hasMore = result.data.isNotEmpty()
                                )
                            }
                        }
                        is Result.Error -> {
                            if (page > 0) currentPage--
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

    /**
     * 选择子分类
     */
    private fun selectCategory(cid: Int) {
        if (currentCid == cid) return
        currentCid = cid
        currentPage = 0
        emitState {
            replayState?.copy(selectedCid = cid, articles = emptyList(), hasMore = true)
        }
        sendAction(TreeAction.LoadArticles(currentCid, currentPage))
    }

    /**
     * 设置详情页导航状态
     */
    private fun toDetail(articleId: String, link: String) {
        emitState { replayState?.copy(navigateToDetail = link) }
    }

    /**
     * 加载更多
     */
    private fun loadMoreArticles() {
        val currentState = replayState ?: return
        if (currentState.isLoadingMore || !currentState.hasMore) return

        currentPage++
        sendAction(TreeAction.LoadArticles(currentCid, currentPage))
    }

    /**
     * 下拉刷新
     */
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
