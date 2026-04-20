package com.syf.wanandroidcompose.project

import com.syf.wanandroidcompose.home.ArticleData
import com.syf.wanandroidcompose.network.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

import kotlinx.serialization.Serializable

interface ProjectApiService {
    @GET("project/tree/json")
    suspend fun getProjectTree(): ApiResponse<List<ProjectTreeData>>

    @GET("project/list/{page}/json")
    suspend fun getProjectList(
        @Path("page") page: Int,
        @Query("cid") cid: Int
    ): ApiResponse<ProjectPageData>
}

@Serializable
data class ProjectTreeData(
    val id: Int,
    val name: String
)

@Serializable
data class ProjectPageData(
    val curPage: Int,
    val datas: List<ArticleData>,
    val over: Boolean,
    val pageCount: Int,
    val size: Int,
    val total: Int
)
