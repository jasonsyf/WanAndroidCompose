@file:OptIn(ExperimentalMaterial3Api::class)

package com.syf.wanandroidcompose.tree

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.syf.wanandroidcompose.R
import com.syf.wanandroidcompose.common.PlaceholderHighlight
import com.syf.wanandroidcompose.common.placeholder
import com.syf.wanandroidcompose.common.shimmer
import com.syf.wanandroidcompose.home.ArticleData
import com.syf.wanandroidcompose.home.ArticleItem
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
fun TreeView(
    viewModel: TreeViewModel = viewModel(),
    rootNavController: NavController,
) {
    // 订阅 UI 状态
    val state by viewModel.state.collectAsStateWithLifecycle(initialValue = TreeState())
    // Snackbar 状态
    val snackbarHostState = remember { SnackbarHostState() }

    // 错误消息提示
    LaunchedEffect(key1 = state.errorMsg) {
        state.errorMsg?.let { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    // 优化回调 Lambda，使用 remember 包装以支持重组跳过
    val onCategoryClick =
        remember(viewModel) {
            { parentId: Int -> viewModel.sendAction(TreeAction.SelectParentCategory(parentId)) }
        }

    val onSubCategoryClick =
        remember(viewModel) {
            { cid: Int -> viewModel.sendAction(TreeAction.SelectCategory(cid)) }
        }

    val onRefresh =
        remember(viewModel) {
            { viewModel.sendAction(TreeAction.Refresh) }
        }

    val onLoadMore =
        remember(viewModel) {
            { viewModel.sendAction(TreeAction.LoadMore) }
        }

    val onArticleClick =
        remember(rootNavController) {
            { article: ArticleData ->
                val encodedUrl = URLEncoder.encode(article.link, StandardCharsets.UTF_8.toString())
                rootNavController.navigate("detail/$encodedUrl")
            }
        }

    TreeContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onCategoryClick = onCategoryClick,
        onSubCategoryClick = onSubCategoryClick,
        onRefresh = onRefresh,
        onLoadMore = onLoadMore,
        onArticleClick = onArticleClick,
    )
}

/**
 * 体系页面渲染内容
 * 无状态 Composable，方便测试
 */
@Composable
fun TreeContent(
    state: TreeState,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onCategoryClick: (Int) -> Unit,
    onSubCategoryClick: (Int) -> Unit,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onArticleClick: (ArticleData) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // 左侧：父分类列表
            ParentCategoryList(
                categories = state.categories,
                selectedParentId = state.selectedParentId,
                isLoading = state.isLoading && state.categories.isEmpty(),
                onCategoryClick = onCategoryClick,
            )

            // 右侧：子分类及文章内容
            SubCategoryContent(
                subCategories = state.subCategories,
                selectedCid = state.selectedCid,
                articles = state.articles,
                isLoading = state.isLoading,
                isRefreshing = state.isRefreshing,
                isLoadingMore = state.isLoadingMore,
                hasMore = state.hasMore,
                onSubCategoryClick = onSubCategoryClick,
                onRefresh = onRefresh,
                onLoadMore = onLoadMore,
                onArticleClick = onArticleClick,
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxHeight(),
            )
        }

        // Snackbar 提示
        SnackbarHost(hostState = snackbarHostState)
    }
}

/**
 * 父分类列表（左侧分栏）
 */
@Composable
private fun ParentCategoryList(
    categories: List<TreeCategory>,
    selectedParentId: Int,
    isLoading: Boolean,
    onCategoryClick: (Int) -> Unit,
) {
    LazyColumn(
        modifier =
            Modifier
                .width(100.dp)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surfaceContainerLowest),
    ) {
        if (isLoading) {
            items(15) {
                SkeletonParentCategoryItem()
            }
        } else {
            items(
                items = categories,
                key = { it.id },
                contentType = { "category" },
            ) { category ->
                val isSelected = category.id == selectedParentId
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable { onCategoryClick(category.id) }
                            .background(if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent)
                            .padding(vertical = 16.dp, horizontal = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color =
                            if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                    )
                }
            }
        }
    }
}

/**
 * 子分类内容区域（右侧分栏）
 */
@Composable
private fun SubCategoryContent(
    subCategories: List<TreeCategory>,
    selectedCid: Int,
    articles: List<ArticleData>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    isLoadingMore: Boolean,
    hasMore: Boolean,
    onSubCategoryClick: (Int) -> Unit,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onArticleClick: (ArticleData) -> Unit,
    modifier: Modifier = Modifier,
) {
    // 下拉刷新状态
    val pullToRefreshState = rememberPullToRefreshState()

    Column(modifier = modifier) {
        // 体系二级分类 Tab 栏
        if (subCategories.isNotEmpty()) {
            val selectedIndex by
                remember(subCategories, selectedCid) {
                    derivedStateOf {
                        subCategories.indexOfFirst { it.id == selectedCid }.takeIf { it >= 0 } ?: 0
                    }
                }
            ScrollableTabRow(
                selectedTabIndex = selectedIndex,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                containerColor = Color.Transparent,
                edgePadding = 6.dp,
                divider = {},
                indicator = { tabPositions ->
                    if (selectedIndex < tabPositions.size) {
                        TabRowDefaults.PrimaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                            width = 24.dp,
                            shape = RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp),
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                },
            ) {
                // 渲染子分类 Tab
                subCategories.forEachIndexed { index, category ->
                    val selected = index == selectedIndex
                    Tab(
                        selected = selected,
                        onClick = { onSubCategoryClick(category.id) },
                        text = {
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                color =
                                    if (selected) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                            )
                        },
                    )
                }
            }
        }

        // 下拉刷新区域
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            state = pullToRefreshState,
            modifier = Modifier.fillMaxSize(),
        ) {
            AnimatedContent(
                targetState = isLoading && articles.isEmpty(),
                transitionSpec = {
                    (
                        fadeIn(animationSpec = tween(220, delayMillis = 90)) +
                            scaleIn(initialScale = 0.92f, animationSpec = tween(220, delayMillis = 90))
                    )
                        .togetherWith(
                            fadeOut(animationSpec = tween(90)) +
                                scaleOut(targetScale = 0.92f, animationSpec = tween(90)),
                        )
                },
                label = "TreeContentTransition",
            ) { isContentLoading ->
                if (isContentLoading) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(6) {
                            SkeletonArticleItem()
                        }
                    }
                } else if (articles.isEmpty() && !isRefreshing) {
                    // 空数据展示
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(R.string.error_data_empty),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } else {
                    // 文章列表
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(
                            count = articles.size,
                            key = { index -> articles[index].id },
                            contentType = { "article" },
                        ) { index ->
                            val article = articles[index]
                            // 点击跳转详情
                            ArticleItem(
                                item = article,
                                onClick = { onArticleClick(article) },
                            )
                            // 自动加载更多
                            if (index == articles.size - 1 && !isLoadingMore && hasMore) {
                                LaunchedEffect(article.id) {
                                    onLoadMore()
                                }
                            }
                        }
                        // 加载更多动画
                        if (isLoadingMore) {
                            item(key = "loading_more", contentType = "loading") {
                                Box(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 父分类骨架屏项
 */
@Composable
private fun SkeletonParentCategoryItem() {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .width(60.dp)
                    .height(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .placeholder(
                        visible = true,
                        highlight = PlaceholderHighlight.shimmer(),
                    ),
        )
    }
}

/**
 * 文章列表骨架屏项
 */
@Composable
private fun SkeletonArticleItem() {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp) // 添加外层边距以匹配 ArticleItem
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier =
                    Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .placeholder(
                            visible = true,
                            highlight = PlaceholderHighlight.shimmer(),
                        ),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier =
                    Modifier
                        .height(14.dp)
                        .width(60.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .placeholder(
                            visible = true,
                            highlight = PlaceholderHighlight.shimmer(),
                        ),
            )
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier =
                    Modifier
                        .height(12.dp)
                        .width(40.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .placeholder(
                            visible = true,
                            highlight = PlaceholderHighlight.shimmer(),
                        ),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .placeholder(
                        visible = true,
                        highlight = PlaceholderHighlight.shimmer(),
                    ),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier =
                Modifier
                    .fillMaxWidth(0.6f)
                    .height(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .placeholder(
                        visible = true,
                        highlight = PlaceholderHighlight.shimmer(),
                    ),
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier =
                    Modifier
                        .height(16.dp)
                        .width(80.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .placeholder(
                            visible = true,
                            highlight = PlaceholderHighlight.shimmer(),
                        ),
            )
            Box(
                modifier =
                    Modifier
                        .height(16.dp)
                        .width(60.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .placeholder(
                            visible = true,
                            highlight = PlaceholderHighlight.shimmer(),
                        ),
            )
        }
    }
}
