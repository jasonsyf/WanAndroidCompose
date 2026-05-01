package com.syf.wanandroidcompose.project

import com.syf.wanandroidcompose.common.local.CategoryEntity
import com.syf.wanandroidcompose.home.local.HomeDao
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ProjectLocalDataSourceTest {
    private val homeDao = mockk<HomeDao>()
    private val dataSource = ProjectLocalDataSource(homeDao)

    @Test
    fun testGetProjectTree_MapsToDomain() =
        runTest {
            val entity = CategoryEntity(id = 1, name = "Test", type = CategoryEntity.TYPE_PROJECT)
            coEvery { homeDao.getCategoriesByType(CategoryEntity.TYPE_PROJECT) } returns flowOf(listOf(entity))

            val result = dataSource.getProjectTree().first()

            assertEquals(1, result.size)
            assertEquals(1, result[0].id)
            assertEquals("Test", result[0].name)
        }
}
