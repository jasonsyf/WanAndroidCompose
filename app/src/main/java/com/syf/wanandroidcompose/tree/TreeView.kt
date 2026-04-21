@file:OptIn(ExperimentalMaterial3Api::class)
package com.syf.wanandroidcompose.tree

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.syf.wanandroidcompose.R
import com.syf.wanandroidcompose.home.ArticleItem
import kotlinx.coroutines.flow.collectLatest
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * 体系页面视图
 * 展示体系二级分类 Tab 栏和对应的文章列表
 * 
 * @param viewModel 体系业务逻辑
 * @param rootNavController 根导航控制器
 */
@Composable
fun TreeView(viewModel: TreeViewModel = viewModel(), rootNavController: NavController) {
    // 订阅 UI 状态
    val state by viewModel.state.collectAsStateWithLifecycle(initialValue = TreeState())
    // Snackbar 状态
    val snackbarHostState = remember { SnackbarHostState() }
    // 下拉刷新状态
    val pullToRefreshState = rememberPullToRefreshState()

    // 错误消息提示
    LaunchedEffect(key1 = state.errorMsg) {
        state.errorMsg?.let { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 初始加载动画
            if (state.isLoading && state.categories.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    // 体系二级分类 Tab 栏
                    if (state.allSubCategories.isNotEmpty()) {
                        val selectedIndex = state.allSubCategories.indexOfFirst { it.id == state.selectedCid }.takeIf { it >= 0 } ?: 0
                        ScrollableTabRow(
                            selectedTabIndex = selectedIndex,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            containerColor = Color.Transparent,
                            edgePadding = 16.dp,
                            divider = {},
                            indicator = { tabPositions ->
                                if (selectedIndex < tabPositions.size) {
                                    TabRowDefaults.PrimaryIndicator(
                                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                                        width = 24.dp,
                                        shape = RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        ) {
                            // 渲染子分类 Tab
                            state.allSubCategories.forEachIndexed { index, category ->
                                val selected = index == selectedIndex
                                Tab(
                                    selected = selected,
                                    onClick = { viewModel.sendAction(TreeAction.SelectCategory(category.id)) },
                                    text = {
                                        Text(
                                            text = category.name,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                )
                            }
                        }
                    }

                    // 下拉刷新区域
                    PullToRefreshBox(
                        isRefreshing = state.isRefreshing,
                        onRefresh = { viewModel.sendAction(TreeAction.Refresh) },
                        state = pullToRefreshState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // 空数据展示
                        if (state.articles.isEmpty() && !state.isLoading && !state.isRefreshing) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = "暂无数据",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            // 文章列表
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(
                                    count = state.articles.size,
                                    key = { index -> state.articles[index].id }
                                ) { index ->
                                    val article = state.articles[index]
                                    // 点击跳转详情
                                    ArticleItem(item = article, onClick = {
                                        val encodedUrl = URLEncoder.encode(article.link, StandardCharsets.UTF_8.toString())
                                        rootNavController.navigate("detail/$encodedUrl")
                                    })
                                    // 自动加载更多
                                    if (index == state.articles.size - 1 && !state.isLoadingMore && state.hasMore) {
                                        LaunchedEffect(article.id) {
                                            viewModel.sendAction(TreeAction.LoadMore)
                                        }
                                    }
                                }
                                // 加载更多动画
                                if (state.isLoadingMore) {
                                    item {
                                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}