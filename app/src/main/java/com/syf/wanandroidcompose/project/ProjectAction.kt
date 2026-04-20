package com.syf.wanandroidcompose.project

import com.syf.wanandroidcompose.common.Action
import com.syf.wanandroidcompose.common.State
import com.syf.wanandroidcompose.home.ArticleData // 引入 Home 模块的 ArticleData

sealed class ProjectAction : Action {
    object LoadTree : ProjectAction()
    data class LoadProjects(val cid: Int, val page: Int = 1) : ProjectAction()
    data class SelectCategory(val cid: Int) : ProjectAction()
    data class ClickArticle(val articleId: String, val link: String) : ProjectAction()
    object LoadMore : ProjectAction()
    object Refresh : ProjectAction()
    object DetailNavigated : ProjectAction() // 详情页导航后重置状态
}

data class ProjectCategory(
    val id: Int,
    val name: String
)

data class ProjectState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = true,
    val categories: List<ProjectCategory> = emptyList(),
    val selectedCid: Int = 0,
    val projects: List<ArticleData> = emptyList(),
    val errorMsg: String? = null,
    val navigateToDetail: String? = null
) : State
