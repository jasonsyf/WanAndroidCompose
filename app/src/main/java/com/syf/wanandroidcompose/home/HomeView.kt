@file:OptIn(ExperimentalMaterial3Api::class)

package com.syf.wanandroidcompose.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.syf.wanandroidcompose.R
import com.syf.wanandroidcompose.common.PlaceholderHighlight
import com.syf.wanandroidcompose.common.placeholder
import com.syf.wanandroidcompose.common.shimmer
import com.syf.wanandroidcompose.theme.WanAndroidComposeTheme
import kotlinx.coroutines.delay
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * 首页主视图 Composable
 * @param navController 导航控制器，用于页面跳转
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeView(navController: NavController) {
    val colorScheme = MaterialTheme.colorScheme
    val bgColor = listOf(colorScheme.primary, colorScheme.secondary, colorScheme.tertiary, colorScheme.error, colorScheme.primaryContainer, colorScheme.secondaryContainer)
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
    val state by viewModel.state.collectAsState(initial = HomeListState())
    val listState = rememberLazyListState()
    val pullToRefreshState = rememberPullToRefreshState()
    val snackbarHostState = remember { SnackbarHostState() }

    // 监听导航事件
    LaunchedEffect(state.navigateToDetail) {
        state.navigateToDetail?.let { url ->
            val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
            navController.navigate("detail/$encodedUrl")
            viewModel.sendAction(HomeAction.DetailNavigated) // 通知 ViewModel 导航已完成
        }
    }

    // 监听错误消息以显示 Snackbar
    LaunchedEffect(state.errorMsg) {
        state.errorMsg?.let { snackbarHostState.showSnackbar(it) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 下拉刷新容器
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            state = pullToRefreshState,
            onRefresh = { viewModel.sendAction(HomeAction.RefreshAllData) },
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize().background(colorScheme.background)
            ) {
                // 轮播图
                item {
                    if (state.getBannerData.isNotEmpty()) {
                        Carouse(banners = state.getBannerData, onBannerClick = { url -> viewModel.sendAction(HomeAction.ClickArticle(url)) })
                    } else {
                        // 轮播图占位符
                        BannerPlaceholder()
                    }
                }

                // "优质公众号" 标题
                item {
                    if (state.getBannerData.isNotEmpty()) { // 用轮播图数据作为整体加载完成的判断依据
                        SectionTitle(stringResource(R.string.section_quality_accounts))
                    } else {
                        SectionTitlePlaceholder()
                    }
                }

                // 公众号列表
                item {
                    LazyRow(modifier = Modifier.padding(5.dp)) {
                        if (state.getPublicData.isNotEmpty()) {
                            items(state.getPublicData) { item ->
                                WeChatAccountItem(item, bgColor[state.getPublicData.indexOf(item) % bgColor.size])
                            }
                        } else {
                            // 公众号列表占位符
                            items(10) { WeChatAccountPlaceholder() }
                        }
                    }
                }

                // 分类 Tab (粘性头部)
                stickyHeader {
                    if (state.categories.isNotEmpty()) {
                        ChipTabRow(
                            tabs = state.categories,
                            selectedTabId = state.selectedCategoryId,
                            onTabSelected = { id -> viewModel.sendAction(HomeAction.SelectCategory(id)) }
                        )
                    } else {
                        // 分类 Tab 占位符
                        ChipTabRowPlaceholder()
                    }
                }

                // 文章列表
                if (state.getArticleData.isNotEmpty()) {
                    items(state.getArticleData, key = { it.id }) { item ->
                        ArticleItem(item) {
                            viewModel.sendAction(HomeAction.ClickArticle(item.link))
                        }
                    }
                } else {
                    // 文章列表骨架屏
                    items(10) { ArticleSkeletonItem() }
                }

                // 加载更多 Footer
                item {
                    // 当列表滚动到底部时，触发加载更多
                    LaunchedEffect(listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index) {
                        if (listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == listState.layoutInfo.totalItemsCount - 1) {
                            viewModel.sendAction(HomeAction.LoadMoreArticle)
                        }
                    }

                    if (state.getArticleData.isNotEmpty()) {
                        LoadMoreFooter(isLoading = state.isLoadingMore, hasMore = state.hasMore)
                    }
                }
            }
        }

        // 用于显示错误信息的 Snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

/**
 * 文章列表项
 */
@Composable
fun ArticleItem(item: ArticleData, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            // 作者和日期行
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Person, contentDescription = "作者", modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                Text(text = item.author.ifEmpty { item.shareUser }, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 4.dp))
                Spacer(modifier = Modifier.weight(1f))
                Text(text = item.niceDate, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
            }
            Spacer(modifier = Modifier.height(8.dp))
            // 标题
            Text(text = item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(12.dp))
            // 标签和分类行
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (item.top == "1") {
                    TagLabel(stringResource(R.string.label_top), MaterialTheme.colorScheme.error)
                }
                if (item.fresh) {
                    TagLabel(stringResource(R.string.label_new), MaterialTheme.colorScheme.tertiary)
                }
                CategoryLabel("${item.superChapterName} · ${item.chapterName}")
                Spacer(modifier = Modifier.weight(1f))
                Icon(imageVector = if (item.collect) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder, contentDescription = "收藏", modifier = Modifier.size(20.dp), tint = if (item.collect) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
            }
        }
    }
}

/**
 * 轮播图 Composable
 * @param banners 轮播图数据列表
 * @param onBannerClick 点击轮播图的回调
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Carouse(banners: List<BannerData>, onBannerClick: (String) -> Unit) {
    if (banners.isEmpty()) return
    val context = LocalContext.current
    // 初始页面定在中间，实现无限循环效果
    val initialPage = Int.MAX_VALUE / 2 - (Int.MAX_VALUE / 2 % banners.size)
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { Int.MAX_VALUE })

    Box {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth().height(200.dp).padding(top = 16.dp, bottom = 8.dp),
            contentPadding = PaddingValues(horizontal = 0.dp)
        ) { page ->
            val actualIndex = page % banners.size
            val item = banners[actualIndex]
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onBannerClick(item.url) }
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context).data(item.imagePath).crossfade(true).build(),
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        // 指示器
        Row(
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            val currentIndex = pagerState.currentPage % banners.size
            banners.forEachIndexed { index, _ ->
                val width by animateDpAsState(targetValue = if (index == currentIndex) 24.dp else 8.dp, label = "")
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .width(width)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (index == currentIndex) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                )
            }
        }
    }

    // 自动滚动逻辑
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()
    if (!isDragged) {
        LaunchedEffect(Unit) {
            while (true) {
                delay(3000L)
                pagerState.animateScrollToPage(pagerState.currentPage + 1)
            }
        }
    }
}

/**
 * 分类标签行
 * @param tabs 分类数据列表
 * @param selectedTabId 当前选中的 Tab ID
 * @param onTabSelected Tab 选中回调
 */
@Composable
fun ChipTabRow(tabs: List<CategoryUiModel>, selectedTabId: Int, onTabSelected: (Int) -> Unit) {
    if (tabs.isEmpty()) return
    LazyRow(
        modifier = Modifier.background(MaterialTheme.colorScheme.background).fillMaxWidth().zIndex(1f).padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(tabs) { category ->
            val isSelected = selectedTabId == category.id
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh)
                    .clickable { onTabSelected(category.id) }
                    .padding(horizontal = 25.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category.name,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 15.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

/**
 * 公众号 Item
 */
@Composable
fun WeChatAccountItem(item: WeChatAccountData, color: Color) {
    Column(
        Modifier.padding(5.dp).size(60.dp, 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = item.name.firstOrNull()?.toString() ?: "",
            modifier = Modifier.clip(shape = CircleShape).size(60.dp).background(color),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center,
            lineHeight = 60.sp
        )
        Text(
            text = item.name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * 通用标签
 */
@Composable
fun TagLabel(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Bold)
    }
}

/**
 * 文章分类标签
 */
@Composable
fun CategoryLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    )
}

/**
 * "加载更多"的底部视图
 */
@Composable
fun LoadMoreFooter(isLoading: Boolean, hasMore: Boolean) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp))
        } else if (!hasMore) {
            Text(stringResource(R.string.label_no_more_data), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// ----- 占位符和骨架屏 -----

@Composable
fun BannerPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(top = 16.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .placeholder(visible = true, highlight = PlaceholderHighlight.shimmer())
    )
}

@Composable
fun SectionTitle(title: String) {
    Text(
        title,
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
        fontSize = 25.sp,
        fontWeight = FontWeight.W700
    )
}

@Composable
fun SectionTitlePlaceholder() {
    Box(
        modifier = Modifier
            .padding(horizontal = 15.dp, vertical = 10.dp)
            .size(200.dp, 50.dp)
            .clip(RoundedCornerShape(4.dp))
            .placeholder(visible = true, highlight = PlaceholderHighlight.shimmer())
    )
}

@Composable
fun WeChatAccountPlaceholder() {
    Column(
        Modifier.padding(5.dp).size(60.dp, 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.clip(shape = CircleShape).size(60.dp).placeholder(visible = true, highlight = PlaceholderHighlight.shimmer()))
        Box(modifier = Modifier.padding(top = 5.dp).size(40.dp, 12.dp).clip(RoundedCornerShape(4.dp)).placeholder(visible = true, highlight = PlaceholderHighlight.shimmer()))
    }
}

@Composable
fun ChipTabRowPlaceholder() {
    Box(
        modifier = Modifier
            .padding(horizontal = 15.dp, vertical = 5.dp)
            .fillMaxWidth()
            .height(50.dp)
            .placeholder(visible = true, highlight = PlaceholderHighlight.shimmer())
    )
}

@Composable
fun ArticleSkeletonItem() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surface).padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(24.dp).clip(CircleShape).placeholder(true, highlight = PlaceholderHighlight.shimmer()))
            Spacer(modifier = Modifier.width(8.dp))
            Box(modifier = Modifier.height(16.dp).weight(0.3f).placeholder(true, highlight = PlaceholderHighlight.shimmer()))
            Spacer(modifier = Modifier.weight(0.7f))
            Box(modifier = Modifier.height(12.dp).weight(0.2f).placeholder(true, highlight = PlaceholderHighlight.shimmer()))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Box(modifier = Modifier.fillMaxWidth().height(20.dp).placeholder(true, highlight = PlaceholderHighlight.shimmer()))
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth(0.5f).height(20.dp).placeholder(true, highlight = PlaceholderHighlight.shimmer()))
        Spacer(modifier = Modifier.height(12.dp))
        Row {
            Box(modifier = Modifier.height(16.dp).weight(0.4f).placeholder(true, highlight = PlaceholderHighlight.shimmer()))
            Spacer(modifier = Modifier.weight(0.6f))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    WanAndroidComposeTheme {
        HomeView(rememberNavController())
    }
}
