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

class ProjectViewModel(private val repository: ProjectRepository, private val application: Application) :
    BaseViewModelOptimized<ProjectAction, ProjectState>() {

    private var currentPage = 1 // 项目列表页码从 1 开始
    private var currentCid = 0 // 当前选中的分类 ID

    init {
        // 初始加载项目分类
        sendAction(ProjectAction.LoadTree)
        // 初始加载项目列表 (默认全部或第一个分类)
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

    private fun loadProjectTree() {
        launchAction("LoadProjectTree") {
            repository.getProjectTree()
                .onStart {
                    emitState {
                        replayState?.copy(isLoading = true, errorMsg = null) ?: ProjectState(isLoading = true)
                    }
                }
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            emitState {
                                val categories = result.data.map { ProjectCategory(it.id, it.name) }
                                currentCid = categories.firstOrNull()?.id ?: 0 // 默认选择第一个分类
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
                            // 加载第一个分类的项目
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

    private fun loadProjects(cid: Int, page: Int) {
        if (!NetworkUtils.isNetworkAvailable(application)) {
            emitState {
                val currentState = replayState ?: ProjectState()
                currentState.copy(
                    isLoading = false,
                    isRefreshing = false,
                    isLoadingMore = false,
                    errorMsg = application.getString(R.string.error_network_unavailable)
                )
            }
            return
        }

        launchAction("LoadProjects") {
            repository.getProjectList(page, cid)
                .onStart {
                    // 如果是第一页加载，则显示全屏加载或刷新状态
                    if (page == 1) {
                        emitState {
                            if (replayState?.isRefreshing == true) {
                                replayState?.copy(projects = emptyList(), errorMsg = null)
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
                                    result.data
                                } else {
                                    replayState?.projects.orEmpty() + result.data
                                }
                                replayState?.copy(
                                    isLoading = false,
                                    isRefreshing = false,
                                    isLoadingMore = false,
                                    projects = currentProjects,
                                    hasMore = result.data.isNotEmpty(), // TODO: need to get this from API
                                    errorMsg = null
                                ) ?: ProjectState(
                                    projects = currentProjects,
                                    hasMore = result.data.isNotEmpty()
                                )
                            }
                        }
                        is Result.Error -> {
                            if (page > 1) currentPage-- // 加载更多失败，页码回退
                            emitState {
                                replayState?.copy(
                                    isLoading = false,
                                    isRefreshing = false,
                                    isLoadingMore = false,
                                    errorMsg = result.message
                                ) ?: ProjectState(errorMsg = result.message)
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
        currentPage = 1
        emitState {
            replayState?.copy(selectedCid = cid, projects = emptyList(), hasMore = true)
        }
        sendAction(ProjectAction.LoadProjects(currentCid, currentPage))
    }

    private fun toDetail(articleId: String, link: String) {
        emitState { replayState?.copy(navigateToDetail = link) }
    }

    private fun loadMoreProjects() {
        val currentState = replayState ?: return
        if (currentState.isLoadingMore || !currentState.hasMore) return

        currentPage++
        sendAction(ProjectAction.LoadProjects(currentCid, currentPage))
    }

    private fun refreshProjects() {
        currentPage = 1
        emitState {
            replayState?.copy(isRefreshing = true, hasMore = true, errorMsg = null)
        }
        sendAction(ProjectAction.LoadProjects(currentCid, currentPage))
    }

    companion object {
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
