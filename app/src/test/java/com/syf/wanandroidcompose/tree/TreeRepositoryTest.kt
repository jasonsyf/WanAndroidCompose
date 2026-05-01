package com.syf.wanandroidcompose.tree

import com.syf.wanandroidcompose.network.ApiResponse
import com.syf.wanandroidcompose.network.Result
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TreeRepositoryTest {
    private val apiService = mockk<TreeApiService>()
    private val localDataSource = mockk<TreeLocalDataSource>(relaxed = true)
    private val repository = TreeRepository(apiService, localDataSource)

    @Test
    fun testGetSystemTree_EmitsLocalThenRemote() =
        runTest {
            val localData = listOf(TreeData(id = 1, name = "Local"))
            val remoteData = listOf(TreeData(id = 1, name = "Remote"))

            coEvery { localDataSource.getSystemTree() } returns flowOf(localData)
            coEvery { apiService.getSystemTree() } returns ApiResponse(data = remoteData, errorCode = 0, errorMsg = "")

            val results = repository.getSystemTree().toList()

            // Index 0: Loading
            // Index 1: Local Success
            // Index 2: Remote Success
            assertTrue(results[0] is Result.Loading)
            assertTrue(results[1] is Result.Success)
            assertEquals("Local", (results[1] as Result.Success).data[0].name)
            assertTrue(results[2] is Result.Success)
            assertEquals("Remote", (results[2] as Result.Success).data[0].name)
        }
}
