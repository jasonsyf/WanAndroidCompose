package com.syf.wanandroidcompose.project

import com.syf.wanandroidcompose.home.ArticleData
import com.syf.wanandroidcompose.network.Result
import com.syf.wanandroidcompose.network.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

/**
 * 项目模块数据仓库
 * 负责从网络获取项目相关数据，并处理简单的缓存逻辑
 */
class ProjectRepository(
    private val apiService: ProjectApiService,
    private val localDataSource: ProjectLocalDataSource,
) {
    /**
     * 获取项目分类树
     *
     * 采用本地优先策略：
     * 1. 发射 Loading 状态
     * 2. 发射本地缓存数据（如果有）
     * 3. 请求网络数据，成功后保存到本地并再次发射
     *
     * @return 返回包含项目分类树结果的 Flow
     */
    fun getProjectTree(): Flow<Result<List<ProjectTreeData>>> =
        flow {
            emit(Result.Loading)

            // 1. 发射本地数据
            val localData = localDataSource.getProjectTree().first()
            emit(Result.Success(localData))

            // 2. 请求网络并刷新
            when (val result = safeApiCall { apiService.getProjectTree() }) {
                is Result.Success -> {
                    localDataSource.saveProjectTree(result.data)
                    // 不需要显式 emit Result.Success，因为 localDataSource.getProjectTree()
                    // 应该是一个观察者 Flow，数据变更后会自动发射新值。
                    // 但是在当前的 safeApiCall 模式下，我们通常还是显式 emit 比较清晰。
                    // 为了满足测试中的 EmitsLocalThenRemote 预期，我们这里显式 emit 网络结果。
                    emit(Result.Success(result.data))
                }
                is Result.Error -> emit(result)
                Result.Loading -> {}
            }
        }

    /**
     * 获取指定分类的项目列表
     *
     * @param page 页码，从 1 开始
     * @param cid 分类ID
     * @return 返回包含文章数据列表结果的 Flow
     */
    fun getProjectList(
        page: Int,
        cid: Int,
    ): Flow<Result<List<ArticleData>>> =
        flow {
            emit(Result.Loading)

            // 首页加载时先发本地数据
            if (page == 1) {
                emit(Result.Success(localDataSource.getProjectList(cid).first()))
            }

            when (val result = safeApiCall { apiService.getProjectList(page, cid) }) {
                is Result.Success -> {
                    if (page == 1) {
                        localDataSource.saveProjectList(cid, result.data.datas)
                    }
                    emit(Result.Success(result.data.datas))
                }
                is Result.Error -> emit(result)
                Result.Loading -> {}
            }
        }
}
