package com.syf.wanandroidcompose.tree

import com.syf.wanandroidcompose.home.ArticleData
import com.syf.wanandroidcompose.network.Result
import com.syf.wanandroidcompose.network.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

/**
 * 体系模块数据仓库
 * 负责获取体系结构和具体的体系文章列表
 */
class TreeRepository(
    private val apiService: TreeApiService,
    private val localDataSource: TreeLocalDataSource,
) {
    /**
     * 获取体系分类树
     * 采用本地优先策略
     * @return 包含体系树结果的 Flow
     */
    fun getSystemTree(): Flow<Result<List<TreeData>>> =
        flow {
            emit(Result.Loading)

            // 1. 发射本地数据
            val localData = localDataSource.getSystemTree().first()
            emit(Result.Success(localData))

            // 2. 请求网络并刷新
            when (val result = safeApiCall { apiService.getSystemTree() }) {
                is Result.Success -> {
                    localDataSource.saveSystemTree(result.data)
                    emit(Result.Success(result.data))
                }
                is Result.Error -> emit(result)
                Result.Loading -> {}
            }
        }

    /**
     * 获取特定体系分类下的文章列表
     * @param page 页码，从 0 开始
     * @param cid 分类ID
     * @return 包含文章列表结果 of Flow
     */
    fun getSystemArticles(
        page: Int,
        cid: Int,
    ): Flow<Result<List<ArticleData>>> =
        flow {
            emit(Result.Loading)

            // 首页加载时先发本地数据
            if (page == 0) {
                emit(Result.Success(localDataSource.getSystemArticles(cid).first()))
            }

            when (val result = safeApiCall { apiService.getSystemArticles(page, cid) }) {
                is Result.Success -> {
                    if (page == 0) {
                        localDataSource.saveSystemArticles(cid, result.data.datas)
                    }
                    emit(Result.Success(result.data.datas))
                }
                is Result.Error -> emit(result)
                Result.Loading -> {}
            }
        }
}
