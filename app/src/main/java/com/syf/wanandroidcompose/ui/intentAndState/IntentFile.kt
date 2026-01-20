package com.syf.wanandroidcompose.ui.intentAndState

// 用户的操作意图
sealed class HomeListIntent {
    //加载轮播图数据
    object LoadPagerData : HomeListIntent()
    //加载文章列表数据
    object LoadArticleData : HomeListIntent()
    //加载公众号数据
    object LoadPublic : HomeListIntent()
    //加载切换 tab 的数据
    object LoadTab : HomeListIntent()
    //刷新主页所有数据
    object RefreshAllData : HomeListIntent()
    data class ClickUser(val userId: String) : HomeListIntent()
}

// 界面的状态
data class HomeListState(
    //刷新状态
    val isLoading: Boolean = false,
    val users: List<ArticleData> = emptyList(),
    //错误信息
    val errorMsg: String? = null,
    //跳转到详情
    val navigateToDetail: String? = null,
    //跳转到公众号
    val navigateTopublic: String? = null
)

interface ArticleData {
    val name: String
}

