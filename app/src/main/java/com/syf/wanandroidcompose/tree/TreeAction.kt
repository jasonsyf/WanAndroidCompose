package com.syf.wanandroidcompose.tree

import com.syf.wanandroidcompose.common.Action
import com.syf.wanandroidcompose.common.State
import com.syf.wanandroidcompose.home.ArticleData // 引入 Home 模块的 ArticleData

sealed class TreeAction : Action {
    object LoadTree : TreeAction()
    data class LoadArticles(val cid: Int, val page: Int = 0) : TreeAction() // 体系文章页码从 0 开始
    data class SelectCategory(val cid: Int) : TreeAction()
    data class ClickArticle(val articleId: String, val link: String) : TreeAction()
    object LoadMore : TreeAction()
    object Refresh : TreeAction()
    object DetailNavigated : TreeAction() // 详情页导航后重置状态
}

data class TreeCategory(
    val id: Int,
    val name: String,
    val children: List<TreeCategory> = emptyList() // 子分类
)

data class TreeState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = true,
    val categories: List<TreeCategory> = emptyList(),
    val allSubCategories: List<TreeCategory> = emptyList(), // Flattened subcategories for UI
    val selectedCid: Int = 0, // 当前选中的分类 ID
    val articles: List<ArticleData> = emptyList(),
    val errorMsg: String? = null,
    val navigateToDetail: String? = null
) : State
