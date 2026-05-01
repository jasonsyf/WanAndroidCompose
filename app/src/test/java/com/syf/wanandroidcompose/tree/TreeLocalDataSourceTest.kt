package com.syf.wanandroidcompose.tree

import com.syf.wanandroidcompose.common.local.CategoryEntity
import com.syf.wanandroidcompose.home.local.HomeDao
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class TreeLocalDataSourceTest {
    private val homeDao = mockk<HomeDao>()
    private val dataSource = TreeLocalDataSource(homeDao)

    @Test
    fun testGetSystemTree_MapsToDomain() =
        runTest {
            val parent = CategoryEntity(id = 1, name = "Parent", type = CategoryEntity.TYPE_TREE, parentId = 0)
            val child = CategoryEntity(id = 2, name = "Child", type = CategoryEntity.TYPE_TREE, parentId = 1)

            coEvery { homeDao.getCategoriesByType(CategoryEntity.TYPE_TREE) } returns flowOf(listOf(parent, child))

            val result = dataSource.getSystemTree().first()

            assertEquals(1, result.size)
            assertEquals(1, result[0].id)
            assertEquals(1, result[0].children.size)
            assertEquals(2, result[0].children[0].id)
        }
}
