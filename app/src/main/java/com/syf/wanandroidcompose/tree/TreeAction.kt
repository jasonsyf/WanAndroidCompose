package com.syf.wanandroidcompose.tree

import com.syf.wanandroidcompose.common.Action
import com.syf.wanandroidcompose.common.State
import com.syf.wanandroidcompose.home.ArticleData // 引入 Home 模块的 ArticleData

/**
 * 体系模块的用户操作和系统事件
 */
sealed class TreeAction : Action {
    /** 加载体系结构树 */
    object LoadTree : TreeAction()

    /** 加载特定体系分类下的文章列表，页码从 0 开始 */
    data class LoadArticles(
        val cid: Int,
        val page: Int = 0,
    ) : TreeAction()

    /** 选中一个主分类 */
    data class SelectParentCategory(val parentId: Int) : TreeAction()

    /** 选中一个体系子分类 */
    data class SelectCategory(val cid: Int) : TreeAction()

    /** 点击体系内的文章 */
    data class ClickArticle(
        val articleId: String,
        val link: String,
    ) : TreeAction()

    /** 加载更多文章 */
    object LoadMore : TreeAction()

    /** 刷新当前文章列表 */
    object Refresh : TreeAction()

    /** 详情页导航完成后的回调 */
    object DetailNavigated : TreeAction()
}

/**
 * 体系分类数据模型（用于 UI 展示）
 * @param id 分类ID
 * @param name 分类名称
 * @param children 子分类列表
 */
data class TreeCategory(
    val id: Int,
    val name: String,
    val children: List<TreeCategory> = emptyList(),
)

/**
 * 体系模块的 UI 状态
 * @param isLoading 是否正在全屏加载
 * @param isRefreshing 是否正在下拉刷新
 * @param isLoadingMore 是否正在加载更多
 * @param hasMore 是否还有更多数据
 * @param categories 顶层体系分类列表
 * @param selectedParentId 当前选中的主分类 ID
 * @param subCategories 当前选中的主分类下的子分类列表
 * @param allSubCategories 兼容字段：当前的二级子分类列表
 * @param selectedCid 当前选中的子分类 ID
 * @param articles 当前展示的文章列表
 * @param errorMsg 错误提示
 * @param navigateToDetail 待跳转的链接
 */
data class TreeState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = true,
    val categories: List<TreeCategory> = emptyList(),
    val selectedParentId: Int = 0,
    val subCategories: List<TreeCategory> = emptyList(),
    val allSubCategories: List<TreeCategory> = emptyList(),
    val selectedCid: Int = 0,
    val articles: List<ArticleData> = emptyList(),
    val errorMsg: String? = null,
    val navigateToDetail: String? = null,
) : State
