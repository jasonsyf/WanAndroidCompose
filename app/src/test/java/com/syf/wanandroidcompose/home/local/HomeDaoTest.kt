package com.syf.wanandroidcompose.home.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class HomeDaoTest {
    private lateinit var database: HomeDatabase
    private lateinit var dao: HomeDao

    @Before
    fun setup() {
        database =
            Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                HomeDatabase::class.java,
            ).allowMainThreadQueries().build()
        dao = database.homeDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun testGetArticlesByModule() =
        runBlocking {
            val projectArticle = createFakeArticle(id = 1, moduleType = ArticleEntity.MODULE_PROJECT)
            val treeArticle = createFakeArticle(id = 2, moduleType = ArticleEntity.MODULE_TREE)
            dao.insertArticles(listOf(projectArticle, treeArticle))

            // 当前 getArticlesByModule 逻辑未实现过滤，预期会返回所有文章，导致 assertEquals 失败
            val result = dao.getArticlesByModule(ArticleEntity.MODULE_PROJECT).first()
            assertEquals(1, result.size)
            assertEquals(1, result[0].id)
            assertEquals(ArticleEntity.MODULE_PROJECT, result[0].moduleType)
        }

    private fun createFakeArticle(
        id: Int,
        moduleType: Int,
    ): ArticleEntity {
        return ArticleEntity(
            id = id,
            title = "Title $id",
            link = "Link $id",
            author = "Author $id",
            shareUser = "",
            chapterName = "",
            superChapterName = "",
            chapterId = 0,
            superChapterId = 0,
            niceDate = "",
            desc = "",
            collect = false,
            fresh = false,
            top = "0",
            tags = emptyList(),
            moduleType = moduleType,
        )
    }
}
