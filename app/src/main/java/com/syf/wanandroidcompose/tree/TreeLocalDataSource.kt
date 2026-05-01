package com.syf.wanandroidcompose.tree

import com.syf.wanandroidcompose.common.local.CategoryEntity
import com.syf.wanandroidcompose.home.ArticleData
import com.syf.wanandroidcompose.home.local.ArticleEntity
import com.syf.wanandroidcompose.home.local.HomeDao
import com.syf.wanandroidcompose.home.local.toData
import com.syf.wanandroidcompose.home.local.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TreeLocalDataSource(private val homeDao: HomeDao) {
    fun getSystemTree(): Flow<List<TreeData>> {
        return homeDao.getCategoriesByType(CategoryEntity.TYPE_TREE).map { entities ->
            buildTree(entities)
        }
    }

    private fun buildTree(entities: List<CategoryEntity>): List<TreeData> {
        val parentMap = entities.groupBy { it.parentId }

        fun mapToTreeData(entity: CategoryEntity): TreeData {
            return TreeData(
                id = entity.id,
                name = entity.name,
                parentChapterId = entity.parentId,
                children = parentMap[entity.id]?.map { mapToTreeData(it) } ?: emptyList(),
            )
        }
        return parentMap[0]?.map { mapToTreeData(it) } ?: emptyList()
    }

    suspend fun saveSystemTree(tree: List<TreeData>) {
        val entities = mutableListOf<CategoryEntity>()

        fun flatten(
            list: List<TreeData>,
            parentId: Int,
        ) {
            list.forEach {
                entities.add(
                    CategoryEntity(
                        id = it.id,
                        name = it.name,
                        type = CategoryEntity.TYPE_TREE,
                        parentId = parentId,
                    ),
                )
                flatten(it.children, it.id)
            }
        }
        flatten(tree, 0)
        homeDao.replaceCategoriesByType(CategoryEntity.TYPE_TREE, entities)
    }

    fun getSystemArticles(cid: Int): Flow<List<ArticleData>> {
        return homeDao.getArticlesByChapter(ArticleEntity.MODULE_TREE, cid).map { entities ->
            entities.map { it.toData() }
        }
    }

    suspend fun saveSystemArticles(
        cid: Int,
        articles: List<ArticleData>,
    ) {
        val entities = articles.map { it.toEntity(ArticleEntity.MODULE_TREE) }
        homeDao.replaceArticlesByChapter(ArticleEntity.MODULE_TREE, cid, entities)
    }
}
