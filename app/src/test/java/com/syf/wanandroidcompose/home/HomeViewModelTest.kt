package com.syf.wanandroidcompose.home

import android.app.Application
import app.cash.turbine.test
import com.syf.wanandroidcompose.network.Result
import com.syf.wanandroidcompose.utils.NetworkUtils
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @MockK
    lateinit var repository: HomeRepository

    @MockK
    lateinit var application: Application

    private lateinit var viewModel: HomeViewModel

    // 模拟数据流
    private val homeDataFlow = MutableStateFlow(HomeCachedData(emptyList(), emptyList(), emptyList()))

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        mockkObject(NetworkUtils)

        // 默认模拟网络可用
        every { NetworkUtils.isNetworkAvailable(any()) } returns true
        // 模拟资源字符串（简化处理）
        every { application.getString(any()) } returns "mock_string"
        
        // 模拟 Repository 的 Flow 数据
        every { repository.homeData } returns homeDataFlow
        // 模拟 refreshHomeData 返回成功的 Flow
        every { repository.refreshHomeData() } returns flowOf(Result.Success(HomeRefreshMeta(hasMore = true)))

        viewModel = HomeViewModel(repository, application)
        // 推进调度器，让 init 块中的 refreshAllData 执行完毕
        testDispatcher.scheduler.runCurrent()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `refreshAllData updates loading state and success state`() = runTest {
        // 模拟 refreshHomeData 的过程：Loading -> Success
        every { repository.refreshHomeData() } returns flowOf(
            Result.Loading,
            Result.Success(HomeRefreshMeta(hasMore = true))
        )

        viewModel.state.test {
            // 消费初始状态 (由 init 块触发的最后状态)
            val initialState = awaitItem()
            
            // 发送刷新动作
            viewModel.sendAction(HomeAction.RefreshAllData)

            // 验证状态流转
            val loadingState = awaitItem()
            assertTrue("Should be refreshing", loadingState.isRefreshing)

            val successState = awaitItem()
            assertFalse("Should stop refreshing", successState.isRefreshing)
            assertTrue(successState.hasMore)
        }
    }

    @Test
    fun `selectCategory filters article list correctly`() = runTest {
        // 准备模拟数据
        val articles = listOf(
            ArticleData(id = 1, title = "Android", superChapterId = 1, superChapterName = "Tech"),
            ArticleData(id = 2, title = "Kotlin", superChapterId = 1, superChapterName = "Tech"),
            ArticleData(id = 3, title = "News", superChapterId = 2, superChapterName = "Life")
        )
        
        // 更新 Repository 的 Flow 触发 ViewModel 更新
        homeDataFlow.value = HomeCachedData(articles, emptyList(), emptyList())
        
        testDispatcher.scheduler.runCurrent() // 确保 Flow 收集任务执行

        viewModel.state.test {
            val stateAfterDataLoaded = awaitItem()
            assertEquals(3, stateAfterDataLoaded.getArticleData.size)
            
            // 选择分类 1 (Tech)
            viewModel.sendAction(HomeAction.SelectCategory(1))
            
            val filteredState = awaitItem()
            assertEquals(1, filteredState.selectedCategoryId)
            assertEquals(2, filteredState.getArticleData.size)
            assertTrue(filteredState.getArticleData.all { it.superChapterId == 1 })
            
            // 选择分类 0 (全部)
            viewModel.sendAction(HomeAction.SelectCategory(0))
            val allState = awaitItem()
            assertEquals(0, allState.selectedCategoryId)
            assertEquals(3, allState.getArticleData.size)
        }
    }

    @Test
    fun `network unavailable shows error message`() = runTest {
        // 模拟网络不可用
        every { NetworkUtils.isNetworkAvailable(any()) } returns false
        every { application.getString(any()) } returns "No Network"

        viewModel.state.test {
            awaitItem() // 消费初始状态
            
            viewModel.sendAction(HomeAction.RefreshAllData)
            
            val errorState = awaitItem()
            assertEquals("No Network", errorState.errorMsg)
            assertFalse(errorState.isRefreshing)
        }
    }
}
