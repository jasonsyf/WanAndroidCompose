package com.syf.wanandroidcompose.ui.intentAndState

import com.syf.wanandroidcompose.ui.common.Action
import com.syf.wanandroidcompose.ui.common.State

// 用户的操作意图
sealed class HomeAction : Action {
    //加载轮播图数据
    object LoadPagerData : HomeAction()

    //加载文章列表数据
    object LoadArticleData : HomeAction()

    //加载公众号数据
    object LoadPublic : HomeAction()

    //加载切换 tab 的数据
    object LoadTab : HomeAction()

    //刷新主页所有数据
    object RefreshAllData : HomeAction()

    data class ClickUser(val userId: String) : HomeAction()

    data class ClickArticle(val articleId: String) : HomeAction()
}

// 界面的状态
data class HomeListState(
    //刷新状态
    val isLoading: Boolean = false,
    val getArticleData: List<ArticleData> = emptyList(),
    val getBannerData: List<ArticleData> = emptyList(),
    val getPublicData: List<ArticleData> = emptyList(),
    val getTabData: List<ArticleData> = emptyList(),
    //错误信息
    val errorMsg: String? = null,
    //跳转到详情
    val navigateToDetail: String? = null,
    //跳转到公众号
    val navigateToPublic: String? = null
) : State



interface ArticleData {
    val name: String
}

/**
 * 分页信息
 */
data class ArticlePage(
    val curPage: Int,
    val datas: List<Article>,
    val pageCount: Int,
    val size: Int,
    val total: Int
)

/**
 * 文章数据
 */
data class Article(
    val id: Long,
    val title: String?,
    val link: String?,
    val author: String?,
    val shareUser: String?,
    val niceDate: String?,
    val superChapterName: String?,
    val chapterName: String?,
    val desc: String?,
    val envelopePic: String?,
    val collect: Boolean?,
    val publishTime: Long?
)

