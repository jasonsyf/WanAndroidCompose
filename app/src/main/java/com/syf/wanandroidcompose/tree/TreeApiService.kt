package com.syf.wanandroidcompose.tree

import com.syf.wanandroidcompose.home.ArticleListData
import com.syf.wanandroidcompose.network.ApiResponse
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 体系模块 API 服务接口
 */
interface TreeApiService {
    /**
     * 获取体系分类树
     * @return 体系数据列表响应
     */
    @GET("tree/json")
    suspend fun getSystemTree(): ApiResponse<List<TreeData>>

    /**
     * 获取特定体系分类下的文章列表
     * @param page 页码，从 0 开始
     * @param cid 分类ID
     * @return 文章分页列表响应
     */
    @GET("article/list/{page}/json")
    suspend fun getSystemArticles(
        @Path("page") page: Int,
        @Query("cid") cid: Int
    ): ApiResponse<ArticleListData>
}

/**
 * 体系结构原始数据模型
 * @param children 子分类列表
 * @param id 分类ID
 * @param name 分类名称
 */
@Serializable
data class TreeData(
    val children: List<TreeData> = emptyList(),
    val courseId: Int = 0,
    val id: Int = 0,
    val name: String = "",
    val order: Int = 0,
    val parentChapterId: Int = 0,
    val userControlSetTop: Boolean = false,
    val visible: Int = 0
)
