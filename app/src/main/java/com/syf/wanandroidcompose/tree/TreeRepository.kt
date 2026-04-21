package com.syf.wanandroidcompose.tree

import com.syf.wanandroidcompose.home.ArticleData
import com.syf.wanandroidcompose.network.Result
import com.syf.wanandroidcompose.network.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * 体系模块数据仓库
 * 负责获取体系结构和具体的体系文章列表
 */
class TreeRepository(private val apiService: TreeApiService) {

    /** 体系树内存缓存 */
    private var cachedSystemTree: List<TreeData>? = null

    /**
     * 获取体系分类树
     * 优先返回内存缓存数据
     * @return 包含体系树结果的 Flow
     */
    fun getSystemTree(): Flow<Result<List<TreeData>>> = flow {
        emit(Result.Loading)
        if (cachedSystemTree != null) {
            emit(Result.Success(cachedSystemTree!!))
            return@flow
        }
        when (val result = safeApiCall { apiService.getSystemTree() }) {
            is Result.Success -> {
                cachedSystemTree = result.data
                emit(result)
            }
            is Result.Error -> emit(result)
            Result.Loading -> {}
        }
    }

    /**
     * 获取特定体系分类下的文章列表
     * @param page 页码，从 0 开始
     * @param cid 分类ID
     * @return 包含文章列表结果的 Flow
     */
    fun getSystemArticles(page: Int, cid: Int): Flow<Result<List<ArticleData>>> = flow {
        emit(Result.Loading)
        when (val result = safeApiCall { apiService.getSystemArticles(page, cid) }) {
            is Result.Success -> emit(Result.Success(result.data.datas))
            is Result.Error -> emit(result)
            Result.Loading -> {}
        }
    }
}
