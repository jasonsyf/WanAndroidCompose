package com.syf.wanandroidcompose.tree

import com.syf.wanandroidcompose.home.ArticleData
import com.syf.wanandroidcompose.network.Result
import com.syf.wanandroidcompose.network.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TreeRepository(private val apiService: TreeApiService) {

    private var cachedSystemTree: List<TreeData>? = null

    /**
     * 获取体系分类树
     * 如果有缓存则返回缓存，否则从网络获取并缓存
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
     * 获取体系文章列表
     * 注意：体系文章列表页码从 0 开始
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
