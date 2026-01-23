package com.syf.wanandroidcompose.network.example

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.syf.wanandroidcompose.home.ArticleData
import com.syf.wanandroidcompose.home.BannerData

/**
 * 示例：如何在 Compose UI 中使用网络层
 * 
 * 展示了如何在 Composable 中观察 State 并响应状态变化
 */

@Composable
fun ExampleScreen(
    viewModel: ExampleViewModel = viewModel()
) {
    // 收集 State
    val state by viewModel.state.collectAsState(initial = ExampleState())

    // 首次加载数据
    LaunchedEffect(Unit) {
        viewModel.sendAction(ExampleAction.LoadHomeData)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            // 加载中状态
            state.isLoading && state.articles.isEmpty() -> {
                LoadingView()
            }
            // 错误状态
            state.error != null && state.articles.isEmpty() -> {
                ErrorView(
                    message = state.error!!,
                    onRetry = {
                        viewModel.sendAction(ExampleAction.RefreshData)
                    }
                )
            }
            // 成功状态
            else -> {
                ContentView(
                    state = state,
                    onRefresh = {
                        viewModel.sendAction(ExampleAction.RefreshData)
                    },
                    onLoadMore = {
                        viewModel.sendAction(ExampleAction.LoadPage(state.currentPage + 1))
                    }
                )
            }
        }
    }
}

/**
 * 加载中视图
 */
@Composable
fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

/**
 * 错误视图
 */
@Composable
fun ErrorView(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
            Button(onClick = onRetry) {
                Text("重试")
            }
        }
    }
}

/**
 * 内容视图
 */
@Composable
fun ContentView(
    state: ExampleState,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Banner 部分
        if (state.banners.isNotEmpty()) {
            item {
                BannerSection(banners = state.banners)
            }
        }

        // 文章列表
        items(state.articles) { article ->
            ArticleItem(article = article)
        }

        // 加载更多
        if (state.hasMore) {
            item {
                LoadMoreButton(
                    isLoading = state.isLoading,
                    onClick = onLoadMore
                )
            }
        }
    }
}

/**
 * Banner 区域
 */
@Composable
fun BannerSection(banners: List<BannerData>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Banner",
                style = MaterialTheme.typography.titleMedium
            )
            banners.take(3).forEach { banner ->
                Text(
                    text = "• ${banner.title}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

/**
 * 文章列表项
 */
@Composable
fun ArticleItem(article: ArticleData) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = article.title,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = "作者: ${article.author.ifEmpty { article.shareUser }}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = "${article.superChapterName} / ${article.chapterName}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

/**
 * 加载更多按钮
 */
@Composable
fun LoadMoreButton(
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(onClick = onClick) {
                Text("加载更多")
            }
        }
    }
}
