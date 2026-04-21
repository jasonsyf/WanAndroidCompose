package com.syf.wanandroidcompose.project

import com.syf.wanandroidcompose.home.ArticleData
import com.syf.wanandroidcompose.network.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

import kotlinx.serialization.Serializable

/**
 * 项目模块 API 服务接口
 */
interface ProjectApiService {
    /**
     * 获取项目分类树
     * @return 包含项目分类列表的响应
     */
    @GET("project/tree/json")
    suspend fun getProjectTree(): ApiResponse<List<ProjectTreeData>>

    /**
     * 获取项目列表数据
     * @param page 页码，从 1 开始
     * @param cid 分类ID
     * @return 包含分页项目列表的响应
     */
    @GET("project/list/{page}/json")
    suspend fun getProjectList(
        @Path("page") page: Int,
        @Query("cid") cid: Int
    ): ApiResponse<ProjectPageData>
}

/**
 * 项目分类原始数据模型
 * @param id 分类ID
 * @param name 分类名称
 */
@Serializable
data class ProjectTreeData(
    val id: Int,
    val name: String
)

/**
 * 项目列表分页数据模型
 */
@Serializable
data class ProjectPageData(
    val curPage: Int,
    val datas: List<ArticleData>,
    val over: Boolean,
    val pageCount: Int,
    val size: Int,
    val total: Int
)
