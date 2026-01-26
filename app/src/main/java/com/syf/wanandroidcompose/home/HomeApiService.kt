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
        val curPage: Int = 0, // 当前页码
        val datas: List<ArticleData> = emptyList(), // 文章列表
        val offset: Int = 0, // 偏移量
        val over: Boolean = false, // 是否已加载完所有数据
        val pageCount: Int = 0, // 总页数
        val size: Int = 0, // 每页数量
        val total: Int = 0 // 文章总数
)

/** 文章数据 */
@Serializable
data class ArticleData(
        val id: Int = 0, // 文章 ID
        val title: String = "", // 标题
        val link: String = "", // 文章链接
        val author: String = "", // 作者
        val shareUser: String = "", // 分享者
        val chapterName: String = "", // 所属章节名称
        val superChapterName: String = "", // 所属父章节名称
        val chapterId: Int = 0, // 章节 ID
        val superChapterId: Int = 0, // 父章节 ID
        val niceDate: String = "", // 格式化的发布时间
        val desc: String = "", // 文章描述
        val collect: Boolean = false, // 是否已收藏
        val fresh: Boolean = false, // 是否为最新文章
        val top: String = "0", // 是否置顶（"1" 表示置顶）
        val tags: List<ArticleTag> = emptyList() // 文章标签列表
)

/** 文章标签 */
@Serializable data class ArticleTag(val name: String = "", val url: String = "")

/** Banner 数据 */
@Serializable
data class BannerData(
        val id: Int = 0, // Banner ID
        val title: String = "", // 标题
        val desc: String = "", // 描述
        val imagePath: String = "", // 图片 URL
        val url: String = "", // 跳转链接
        val type: Int = 0, // Banner 类型
        val order: Int = 0 // 排序序号
)

/** 公众号数据 */
@Serializable
data class WeChatAccountData(
        val id: Int = 0, // 公众号 ID
        val name: String = "", // 公众号名称
        val order: Int = 0, // 排序序号
        val courseId: Int = 0, // 课程 ID
        val parentChapterId: Int = 0, // 父章节 ID
        val userControlSetTop: Boolean = false, // 用户是否可控制置顶
        val visible: Int = 0 // 是否可见
)
