package com.syf.wanandroidcompose.project

import com.syf.wanandroidcompose.home.ArticleData
import com.syf.wanandroidcompose.network.Result
import com.syf.wanandroidcompose.network.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ProjectRepository(private val apiService: ProjectApiService) {

    // 缓存项目分类数据
    private var cachedProjectTree: List<ProjectTreeData>? = null

    /**
     * 获取项目分类树
     * 如果有缓存则返回缓存，否则从网络获取并缓存
     */
    fun getProjectTree(): Flow<Result<List<ProjectTreeData>>> = flow {
        emit(Result.Loading)
        if (cachedProjectTree != null) {
            emit(Result.Success(cachedProjectTree!!))
            return@flow
        }
        when (val result = safeApiCall { apiService.getProjectTree() }) {
            is Result.Success -> {
                cachedProjectTree = result.data
                emit(result)
            }
            is Result.Error -> emit(result)
            Result.Loading -> {} // 不会从 safeApiCall 返回
        }
    }

    /**
     * 获取项目列表
     */
    fun getProjectList(page: Int, cid: Int): Flow<Result<List<ArticleData>>> = flow {
        emit(Result.Loading)
        when (val result = safeApiCall { apiService.getProjectList(page, cid) }) {
            is Result.Success -> emit(Result.Success(result.data.datas))
            is Result.Error -> emit(result)
            Result.Loading -> {}
        }
    }
}
