package com.syf.wanandroidcompose.project

import com.syf.wanandroidcompose.home.ArticleData
import com.syf.wanandroidcompose.network.Result
import com.syf.wanandroidcompose.network.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * 项目模块数据仓库
 * 负责从网络获取项目相关数据，并处理简单的缓存逻辑
 */
class ProjectRepository(private val apiService: ProjectApiService) {

    /** 缓存项目分类数据，避免重复请求 */
    private var cachedProjectTree: List<ProjectTreeData>? = null

    /**
     * 获取项目分类树
     * 
     * 采用内存缓存策略：如果已经加载过，则直接从内存中返回。
     * 
     * @return 返回包含项目分类树结果的 Flow
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
            Result.Loading -> {} // 不会从 safeApiCall 返回 Loading
        }
    }

    /**
     * 获取指定分类的项目列表
     * 
     * @param page 页码，从 1 开始
     * @param cid 分类ID
     * @return 返回包含文章数据列表结果的 Flow
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

