package com.syf.wanandroidcompose.project

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
 * 项目模块 ViewModel
 * 负责处理项目分类加载、项目列表分页加载及分类切换逻辑
 */
class ProjectViewModel(private val repository: ProjectRepository, private val application: Application) :
    BaseViewModelOptimized<ProjectAction, ProjectState>() {

    // 当前加载的项目列表页码，从 1 开始
    private var currentPage = 1
    // 当前选中的项目分类 ID
    private var currentCid = 0

    init {
        // 初始加载：获取项目分类树
        sendAction(ProjectAction.LoadTree)
        // 初始加载：加载默认分类的项目列表
        sendAction(ProjectAction.LoadProjects(currentCid, currentPage))
    }

    override fun onAction(action: ProjectAction, currentState: ProjectState?) {
        when (action) {
            is ProjectAction.LoadTree -> loadProjectTree()
            is ProjectAction.LoadProjects -> loadProjects(action.cid, action.page)
            is ProjectAction.SelectCategory -> selectCategory(action.cid)
            is ProjectAction.ClickArticle -> toDetail(action.articleId, action.link)
            is ProjectAction.LoadMore -> loadMoreProjects()
            is ProjectAction.Refresh -> refreshProjects()
            is ProjectAction.DetailNavigated ->
                emitState { replayState?.copy(navigateToDetail = null) }
        }
    }

    /**
     * 加载项目分类树数据
     */
    private fun loadProjectTree() {
        launchAction("LoadProjectTree") {
            repository.getProjectTree()
                .onStart {
                    // 开始加载时更新状态，显示加载中
                    emitState {
                        replayState?.copy(isLoading = true, errorMsg = null) ?: ProjectState(isLoading = true)
                    }
                }
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            emitState {
                                val categories = result.data.map { ProjectCategory(it.id, it.name) }
                                // 默认选择第一个分类
                                currentCid = categories.firstOrNull()?.id ?: 0
                                replayState?.copy(
                                    isLoading = false,
                                    categories = categories,
                                    selectedCid = currentCid,
                                    errorMsg = null
                                ) ?: ProjectState(
                                    categories = categories,
                                    selectedCid = currentCid
                                )
                            }
                            // 成功获取分类后，自动加载第一个分类的项目内容
                            if (currentCid != 0) {
                                sendAction(ProjectAction.LoadProjects(currentCid, 1))
                            }
                        }
                        is Result.Error -> {
                            emitState {
                                replayState?.copy(
                                    isLoading = false,
                                    errorMsg = result.message
                                ) ?: ProjectState(errorMsg = result.message)
                            }
                        }
                        Result.Loading -> {}
                    }
                }
        }
    }

    /**
     * 加载特定分类的项目列表数据
     * @param cid 分类ID
     * @param page 页码
     * @param isSilent 是否为静默加载（不触发 UI 加载状态变化）
     */
    private fun loadProjects(cid: Int, page: Int, isSilent: Boolean = false) {
        val hasCache = (replayState?.projects?.isNotEmpty() == true)
        val isNetworkAvailable = NetworkUtils.isNetworkAvailable(application)

        // 无网络处理
        if (!isNetworkAvailable) {
            if (!hasCache && !isSilent) {
                emitState {
                    val currentState = replayState ?: ProjectState()
                    currentState.copy(
                        isLoading = false,
                        isRefreshing = false,
                        isLoadingMore = false,
                        errorMsg = application.getString(R.string.error_network_unavailable)
                    )
                }
            }
            if (isSilent || hasCache) {
                emitState {
                    replayState?.copy(isLoading = false, isRefreshing = false, isLoadingMore = false) ?: ProjectState()
                }
            }
            return
        }

        launchAction("LoadProjects") {
            repository.getProjectList(page, cid)
                .onStart {
                    // 根据不同的加载场景更新 UI 状态
                    if (page == 1) { // 刷新或首次加载
                        emitState {
                            if (replayState?.isRefreshing == true || isSilent || hasCache) {
                                replayState?.copy(errorMsg = null)
                            } else {
                                replayState?.copy(isLoading = true, projects = emptyList(), errorMsg = null)
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
                                val currentProjects = if (page == 1) {
                                    result.data // 首页则替换
                                } else {
                                    replayState?.projects.orEmpty() + result.data // 否则追加
                                }
                                replayState?.copy(
                                    isLoading = false,
                                    isRefreshing = false,
                                    isLoadingMore = false,
                                    projects = currentProjects,
                                    hasMore = result.data.isNotEmpty(),
                                    errorMsg = null
                                ) ?: ProjectState(
                                    projects = currentProjects,
                                    hasMore = result.data.isNotEmpty()
                                )
                            }
                        }
                        is Result.Error -> {
                            if (page > 1) currentPage-- // 加载更多失败时回退页码
                            if (!isSilent || !hasCache) {
                                emitState {
                                    replayState?.copy(
                                        isLoading = false,
                                        isRefreshing = false,
                                        isLoadingMore = false,
                                        errorMsg = result.message
                                    ) ?: ProjectState(errorMsg = result.message)
                                }
                            } else {
                                emitState {
                                    replayState?.copy(isLoading = false, isRefreshing = false, isLoadingMore = false) ?: ProjectState()
                                }
                            }
                        }
                        Result.Loading -> {}
                    }
                }
        }
    }

    /**
     * 切换选中的项目分类
     */
    private fun selectCategory(cid: Int) {
        if (currentCid == cid) return // 相同分类无需处理
        currentCid = cid
        currentPage = 1
        emitState {
            replayState?.copy(selectedCid = cid, projects = emptyList(), hasMore = true)
        }
        // 切换后立即加载新分类的第一页数据
        sendAction(ProjectAction.LoadProjects(currentCid, currentPage))
    }

    /**
     * 设置导航到详情页的状态
     */
    private fun toDetail(articleId: String, link: String) {
        emitState { replayState?.copy(navigateToDetail = link) }
    }

    /**
     * 处理“加载更多”操作
     */
    private fun loadMoreProjects() {
        val currentState = replayState ?: return
        if (currentState.isLoadingMore || !currentState.hasMore) return

        currentPage++
        // Use silent = false for explicit load more
        loadProjects(currentCid, currentPage, isSilent = false)
    }

    /**
     * 处理“下拉刷新”操作
     */
    private fun refreshProjects() {
        currentPage = 1
        emitState {
            replayState?.copy(isRefreshing = true, hasMore = true, errorMsg = null)
        }
        loadProjects(currentCid, currentPage, isSilent = false)
    }

    companion object {
        /**
         * ViewModel 工厂，用于创建包含依赖的 ProjectViewModel
         */
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as WanAndroidApplication)
                val apiService = RetrofitClient.create<ProjectApiService>()
                val repository = ProjectRepository(apiService)
                ProjectViewModel(repository, application)
            }
        }
    }
}
