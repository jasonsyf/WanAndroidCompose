package com.syf.wanandroidcompose.home

import com.syf.wanandroidcompose.common.Action
import com.syf.wanandroidcompose.common.State

// 用户的操作意图
sealed class HomeAction : Action {
    // 加载轮播图数据
    object LoadPagerData : HomeAction()

    // 加载文章列表数据
    object LoadArticleData : HomeAction()

    // 加载公众号数据
    object LoadPublic : HomeAction()

    // 加载切换 tab 的数据
    object LoadTab : HomeAction()

    // 刷新主页所有数据
    object RefreshAllData : HomeAction()

    // 加载更多文章
    object LoadMoreArticle : HomeAction()

    // 选择分类
    data class SelectCategory(val categoryId: Int) : HomeAction()

    // 点击用户头像/名称
    data class ClickUser(val userId: String) : HomeAction()

    // 点击文章条目
    data class ClickArticle(val articleId: String) : HomeAction()

    // 导航到详情页后重置状态
    object DetailNavigated : HomeAction()
}

/** 分类 UI 模型 */
data class CategoryUiModel(val name: String, val id: Int)

/** 首页状态 包含所有首页需要展示的数据和状态 */
data class HomeListState(
        // 首次加载或全屏加载状态
        val isLoading: Boolean = false,
        // 下拉刷新状态
        val isRefreshing: Boolean = false,
        // 上拉加载更多状态
        val isLoadingMore: Boolean = false,
        // 是否还有更多数据可加载
        val hasMore: Boolean = true,
        // 文章列表数据
        val getArticleData: List<ArticleData> = emptyList(),
        // 轮播图数据
        val getBannerData: List<BannerData> = emptyList(),
        // 公众号数据
        val getPublicData: List<WeChatAccountData> = emptyList(),
        // 分类列表
        val categories: List<CategoryUiModel> = emptyList(),
        // 当前选中的分类 ID（0 表示全部）
        val selectedCategoryId: Int = 0,
        // Tab 数据（预留字段）
        val getTabData: List<ArticleData> = emptyList(),
        // 错误信息
        val errorMsg: String? = null,
        // 待跳转的文章详情链接
        val navigateToDetail: String? = null,
        // 待跳转的公众号 ID
        val navigateToPublic: String? = null
) : State
