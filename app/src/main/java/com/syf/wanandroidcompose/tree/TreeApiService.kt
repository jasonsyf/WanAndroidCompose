package com.syf.wanandroidcompose.tree

import com.syf.wanandroidcompose.home.ArticleListData
import com.syf.wanandroidcompose.network.ApiResponse
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TreeApiService {
    @GET("tree/json")
    suspend fun getSystemTree(): ApiResponse<List<TreeData>>

    @GET("article/list/{page}/json")
    suspend fun getSystemArticles(
        @Path("page") page: Int,
        @Query("cid") cid: Int
    ): ApiResponse<ArticleListData>
}

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
