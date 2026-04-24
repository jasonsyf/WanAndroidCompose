package com.syf.wanandroidcompose.home

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.syf.wanandroidcompose.theme.WanAndroidComposeTheme
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test

/**
 * 首页 UI 测试
 * 验证在不同 State 下 UI 的渲染正确性
 */
class HomeViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun dataLoaded_showsArticleTitles() {
        // 1. 准备模拟数据
        val articles = listOf(
            ArticleData(id = 1, title = "Hello Compose Test", author = "Jetpack"),
            ArticleData(id = 2, title = "MVI Architecture", author = "Android")
        )
        val state = HomeListState(getArticleData = articles)

        // 2. 渲染 UI
        composeTestRule.setContent {
            WanAndroidComposeTheme {
                HomeContent(
                    state = state,
                    onAction = {}
                )
            }
        }

        // 3. 验证标题是否显示
        composeTestRule.onNodeWithText("Hello Compose Test").assertExists()
        composeTestRule.onNodeWithText("MVI Architecture").assertExists()
    }

    @Test
    fun clickingArticle_triggersAction() {
        // 1. 准备数据和 Mock 回调
        val articles = listOf(ArticleData(id = 1, title = "Click Me", link = "url_1"))
        val state = HomeListState(getArticleData = articles)
        val onAction: (HomeAction) -> Unit = mockk(relaxed = true)

        // 2. 渲染 UI
        composeTestRule.setContent {
            WanAndroidComposeTheme {
                HomeContent(state = state, onAction = onAction)
            }
        }

        // 3. 模拟点击并验证 Action
        composeTestRule.onNodeWithText("Click Me").performClick()
        verify { onAction(HomeAction.ClickArticle("url_1")) }
    }

    @Test
    fun scrollingToBottom_triggersLoadMore() {
        // 1. 准备足够多的数据以产生滚动
        val articles = List(20) { ArticleData(id = it, title = "Article $it", link = "url_$it") }
        val state = HomeListState(getArticleData = articles, hasMore = true)
        val onAction: (HomeAction) -> Unit = mockk(relaxed = true)

        // 2. 渲染 UI
        composeTestRule.setContent {
            WanAndroidComposeTheme {
                HomeContent(state = state, onAction = onAction)
            }
        }

        // 3. 滚动到最后一个可见项 (footer)
        // 注意：totalItemsCount 包含轮播图、标题、分类等，所以 index 需要计算准确
        // 简单处理：直接查找列表并滚动到底部
        composeTestRule.onNode(hasScrollToNodeAction()).performScrollToIndex(articles.size + 3) // 大约位置

        // 4. 验证 Action
        verify { onAction(HomeAction.LoadMoreArticle) }
    }
}
