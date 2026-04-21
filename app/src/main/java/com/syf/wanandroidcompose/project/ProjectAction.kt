package com.syf.wanandroidcompose.project

import com.syf.wanandroidcompose.common.Action
import com.syf.wanandroidcompose.common.State
import com.syf.wanandroidcompose.home.ArticleData // 引入 Home 模块的 ArticleData

/**
 * 项目模块的用户操作和系统事件
 */
sealed class ProjectAction : Action {
    /** 加载项目分类树 */
    object LoadTree : ProjectAction()
    /** 加载特定分类的项目列表 */
    data class LoadProjects(val cid: Int, val page: Int = 1) : ProjectAction()
    /** 选中一个项目分类 */
    data class SelectCategory(val cid: Int) : ProjectAction()
    /** 点击项目文章，跳转到详情页 */
    data class ClickArticle(val articleId: String, val link: String) : ProjectAction()
    /** 加载更多项目 */
    object LoadMore : ProjectAction()
    /** 刷新当前项目列表 */
    object Refresh : ProjectAction()
    /** 详情页导航完成后的回调，用于重置导航状态 */
    object DetailNavigated : ProjectAction()
}

/**
 * 项目分类数据模型
 * @param id 分类ID
 * @param name 分类名称
 */
data class ProjectCategory(
    val id: Int,
    val name: String
)

/**
 * 项目模块的 UI 状态
 * @param isLoading 是否正在进行全屏加载
 * @param isRefreshing 是否正在执行下拉刷新
 * @param isLoadingMore 是否正在加载更多数据
 * @param hasMore 是否还有更多数据可以加载
 * @param categories 所有可用的项目分类列表
 * @param selectedCid 当前选中的分类ID
 * @param projects 当前展示的项目列表数据
 * @param errorMsg 错误提示信息，为 null 表示无错误
 * @param navigateToDetail 待跳转的详情页链接，为 null 表示不跳转
 */
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
