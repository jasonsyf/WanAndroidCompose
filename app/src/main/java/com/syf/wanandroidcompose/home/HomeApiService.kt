package com.syf.wanandroidcompose.home

import com.syf.wanandroidcompose.network.ApiResponse
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/** 玩 Android API 服务接口 定义所有的网络请求 */
interface HomeApiService {

    /**
     * 获取首页文章列表
     * @param page 页码，从 0 开始
     */
    @GET("article/list/{page}/json")
    suspend fun getArticleList(@Path("page") page: Int): ApiResponse<ArticleListData>

    /** 获取首页 Banner */
    @GET("banner/json") suspend fun getBanner(): ApiResponse<List<BannerData>>

    /** 获取置顶文章 */
    @GET("article/top/json") suspend fun getTopArticles(): ApiResponse<List<ArticleData>>

    /**
     * 搜索文章
     * @param page 页码，从 0 开始
     * @param keyword 搜索关键词
     */
    @GET("article/query/{page}/json")
    suspend fun searchArticle(
            @Path("page") page: Int,
            @Query("k") keyword: String
    ): ApiResponse<ArticleListData>

    /** 获取公众号列表 */
    @GET("wxarticle/chapters/json")
    suspend fun getWeChatAccounts(): ApiResponse<List<WeChatAccountData>>

    /**
     * 获取某个公众号的文章列表
     * @param id 公众号 ID
     * @param page 页码，从 1 开始
     */
    @GET("wxarticle/list/{id}/{page}/json")
    suspend fun getWeChatArticles(
            @Path("id") id: Int,
            @Path("page") page: Int
    ): ApiResponse<ArticleListData>
}

// ==================== 数据模型 ====================

/** 文章列表数据 */
@Serializable
data class ArticleListData(
        val curPage: Int = 0,
        val datas: List<ArticleData> = emptyList(),
        val offset: Int = 0,
        val over: Boolean = false,
        val pageCount: Int = 0,
        val size: Int = 0,
        val total: Int = 0
)

/** 文章数据 */
@Serializable
data class ArticleData(
        val id: Int = 0,
        val title: String = "",
        val link: String = "",
        val author: String = "",
        val shareUser: String = "",
        val chapterName: String = "",
        val superChapterName: String = "",
        val chapterId: Int = 0,
        val superChapterId: Int = 0,
        val niceDate: String = "",
        val desc: String = "",
        val collect: Boolean = false,
        val fresh: Boolean = false,
        val top: String = "0",
        val tags: List<ArticleTag> = emptyList()
)

/** 文章标签 */
@Serializable data class ArticleTag(val name: String = "", val url: String = "")

/** Banner 数据 */
@Serializable
data class BannerData(
        val id: Int = 0,
        val title: String = "",
        val desc: String = "",
        val imagePath: String = "",
        val url: String = "",
        val type: Int = 0,
        val order: Int = 0
)

/** 公众号数据 */
@Serializable
data class WeChatAccountData(
        val id: Int = 0,
        val name: String = "",
        val order: Int = 0,
        val courseId: Int = 0,
        val parentChapterId: Int = 0,
        val userControlSetTop: Boolean = false,
        val visible: Int = 0
)
