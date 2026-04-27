package com.syf.wanandroidcompose.project

import com.syf.wanandroidcompose.common.local.CategoryEntity
import com.syf.wanandroidcompose.home.ArticleData
import com.syf.wanandroidcompose.home.local.ArticleEntity
import com.syf.wanandroidcompose.home.local.HomeDao
import com.syf.wanandroidcompose.home.local.toData
import com.syf.wanandroidcompose.home.local.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProjectLocalDataSource(private val homeDao: HomeDao) {
    fun getProjectTree(): Flow<List<ProjectTreeData>> {
        return homeDao.getCategoriesByType(CategoryEntity.TYPE_PROJECT).map { entities ->
            entities.map { ProjectTreeData(id = it.id, name = it.name) }
        }
    }

    suspend fun saveProjectTree(tree: List<ProjectTreeData>) {
        val entities =
            tree.map {
                CategoryEntity(id = it.id, name = it.name, type = CategoryEntity.TYPE_PROJECT)
            }
        homeDao.replaceCategoriesByType(CategoryEntity.TYPE_PROJECT, entities)
    }

    fun getProjectList(cid: Int): Flow<List<ArticleData>> {
        return homeDao.getArticlesByChapter(ArticleEntity.MODULE_PROJECT, cid).map { entities ->
            entities.map { it.toData() }
        }
    }

    suspend fun saveProjectList(
        cid: Int,
        articles: List<ArticleData>,
    ) {
        val entities = articles.map { it.toEntity(ArticleEntity.MODULE_PROJECT) }
        homeDao.replaceArticlesByChapter(ArticleEntity.MODULE_PROJECT, cid, entities)
    }
}
