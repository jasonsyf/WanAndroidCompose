@file:OptIn(ExperimentalMaterial3Api::class)

package com.syf.wanandroidcompose.home // import androidx.compose.material.icons.Icons
// import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeView(navController: NavController) {
    val colorScheme = MaterialTheme.colorScheme
    val bgColor = listOf(
        colorScheme.primary,
        colorScheme.secondary,
        colorScheme.tertiary,
        colorScheme.error,
        colorScheme.primaryContainer,
        colorScheme.secondaryContainer
    )
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
    val state by viewModel.state.collectAsState(initial = HomeListState())
    val listState = rememberLazyListState() // 初始数据加载由 ViewModel init 处理
    // 监听跳转详情
    LaunchedEffect(state.navigateToDetail) {
        state.navigateToDetail?.let { url -> // URL 需要编码，防止参数中的特殊字符打断路由
            val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
            navController.navigate("detail/$encodedUrl")
            viewModel.sendAction(HomeAction.DetailNavigated)
        }
    }
    val pullToRefreshState = rememberPullToRefreshState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMsg) { state.errorMsg?.let { snackbarHostState.showSnackbar(it) } }

    Box(modifier = Modifier.fillMaxSize()) {
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            state = pullToRefreshState,
            onRefresh = { viewModel.sendAction(HomeAction.RefreshAllData) },
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                state = listState, modifier = Modifier
                    .fillMaxSize()
                    .background(colorScheme.background)
            ) {
                item {
                    if (state.getBannerData.isNotEmpty()) {
                        Carouse(state.getBannerData)
                    } else { // 轮播图占位符
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(
                                    top = 16.dp, bottom = 8.dp, start = 16.dp, end = 16.dp
                                )
                                .clip(RoundedCornerShape(12.dp))
                                .placeholder(
                                    visible = true, highlight = PlaceholderHighlight.shimmer()
                                )
                        )
                    }
                }
                item {
                    if (state.getBannerData.isNotEmpty()) {
                        Text(
                            stringResource(R.string.section_quality_accounts),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                            fontSize = 25.sp,
                            fontWeight = FontWeight.W700
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 15.dp, vertical = 10.dp)
                                .size(200.dp, 50.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .placeholder(
                                    visible = true, highlight = PlaceholderHighlight.shimmer()
                                )
                        )
                    }
                }
                item {
                    LazyRow(modifier = Modifier.padding(5.dp)) {
                        if (state.getPublicData.isNotEmpty()) { // ... existing code ...
                            items(state.getPublicData.size) { i ->
                                val item = state.getPublicData[i]
                                Column(
                                    Modifier
                                        .padding(5.dp)
                                        .size(60.dp, 80.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = item.name.firstOrNull()?.toString() ?: "",
                                        modifier = Modifier
                                            .clip(shape = CircleShape)
                                            .size(60.dp)
                                            .background(
                                                color = bgColor[i % bgColor.size]
                                            ),
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colorScheme.onPrimary,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 60.sp
                                    )
                                    Text(
                                        text = item.name,
                                        maxLines = 1,
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        } else { // 公众号列表占位符
                            items(10) {
                                Column(
                                    Modifier
                                        .padding(5.dp)
                                        .size(60.dp, 80.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(shape = CircleShape)
                                            .size(60.dp)
                                            .placeholder(
                                                visible = true,
                                                highlight = PlaceholderHighlight.shimmer()
                                            )
                                    )
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 5.dp)
                                            .size(40.dp, 12.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .placeholder(
                                                visible = true,
                                                highlight = PlaceholderHighlight.shimmer()
                                            )
                                    )
                                }
                            }
                        }
                    }
                } // ...
                stickyHeader {
                    if (state.categories.isNotEmpty()) {
                        ChipTabRow(
                            tabs = state.categories,
                            selectedTabId = state.selectedCategoryId,
                            onTabSelected = { id ->
                                viewModel.sendAction(HomeAction.SelectCategory(id))
                            })
                    } else { // 设置鱼骨占位图
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 15.dp, vertical = 5.dp)
                                .fillMaxWidth()
                                .height(50.dp)
                                .placeholder(
                                    visible = true, highlight = PlaceholderHighlight.shimmer()
                                )
                        )
                    }
                } // ... existing code ...
                if (state.getArticleData.isNotEmpty()) {
                    items(state.getArticleData.size) { i ->
                        val item = state.getArticleData[i]
                        ArticleItem(item) {
                            viewModel.sendAction(HomeAction.ClickArticle(item.link))
                        }
                    }
                } else { // 文章列表骨架屏
                    items(10) { ArticleSkeletonItem() }
                } // 加载更多 Footer
                item {
                    LaunchedEffect(Unit) {
                        viewModel.sendAction(HomeAction.LoadMoreArticle)
                    }

                    if (state.getArticleData.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (state.isLoadingMore) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            } else if (!state.hasMore) {
                                Text(
                                        stringResource(R.string.label_no_more_data),
                                        color = colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun ArticleItem(item: ArticleData, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Author and Date
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = item.author.ifEmpty { item.shareUser },
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 4.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = item.niceDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (item.top == "1") {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.label_top),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                if (item.fresh) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.label_new),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Text(
                    text = "${item.superChapterName} · ${item.chapterName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Icon(
                    imageVector = if (item.collect) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "收藏",
                    modifier = Modifier.size(20.dp),
                    tint = if (item.collect) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
            }
        }
    }
}

@Composable
fun Carouse(banners: List<BannerData>) {
    if (banners.isEmpty()) return
    val curContext = LocalContext.current // 初始页面定在中间某个位置，保证左右都能滑
    val initialPage = Int.MAX_VALUE / 2 // 修正初始页，使其对应数据列表的第一个元素 (index 0)
    val startIndex = initialPage - (initialPage % banners.size)
    val pagerState = rememberPagerState(initialPage = startIndex, pageCount = { Int.MAX_VALUE })

    Box {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(top = 16.dp, bottom = 8.dp),
            contentPadding = PaddingValues(horizontal = 0.dp)
        ) { i ->
            val actualIndex = i % banners.size
            val item = banners[actualIndex] // 在真实应用中，使用 Coil 或 Glide 加载 imagePath
            // 目前使用占位符，如果 URL 加载未设置，或者假设 Coil 可用？
            // 因为没看到 Coil 用法，我将使用占位符以确保编译通过且逻辑功能正常，用户可能需要添加 Coil。
            // 实际上，我可以检查 build.gradle...
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = 10.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest) // 占位颜色
            ) {
                val request = remember {
                    ImageRequest.Builder(context = curContext).data(item.imagePath)
                        .placeholder(R.drawable.ic_launcher_foreground).crossfade(true).build()
                }
                AsyncImage(
                    model = request,
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                ) // Text(item.title, Modifier.align(Alignment.Center))
                // 如果有 Coil: AsyncImage(model = item.imagePath, ...)
                // 目前保留结构但指示图片
                //                                Image(
                //                                        painter =
                // painterResource(id =
                // R.drawable.pig1), // 占位符
                //                                        contentDescription =
                // item.title,
                //                                        contentScale =
                // ContentScale.Crop,
                //                                        modifier =
                // Modifier.fillMaxSize()
                //                                )
            }
        } // 指示器
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .offset(0.dp, -10.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val currentIndex = pagerState.currentPage % banners.size
            banners.forEachIndexed { index, _ ->
                if (index == currentIndex) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .width(24.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(MaterialTheme.colorScheme.onSurface)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .width(8.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.4f
                                )
                            )
                    )
                }
            }
        }
    } // 自动滚动逻辑
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()
    LaunchedEffect(isDragged) {
        if (!isDragged) {
            while (true) {
                delay(3000L)
                try {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                } catch (e: Exception) {
                }
            }
        }
    }
}

@Composable
fun ChipTabRow(tabs: List<CategoryUiModel>, selectedTabId: Int, onTabSelected: (Int) -> Unit) {
    if (tabs.isEmpty()) return

    LazyRow(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxWidth()
            .zIndex(1f)
            .padding(vertical = 8.dp), contentPadding = PaddingValues(horizontal = 10.dp), // 两端留白
        horizontalArrangement = Arrangement.spacedBy(12.dp) // 标签间距
    ) {
        itemsIndexed(tabs) { index, category ->
            val isSelected = selectedTabId == category.id // 单个 Tab 的样式
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp)) // 圆角矩形
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceContainerHigh
                )
                    .clickable { onTabSelected(category.id) }
                    .padding(horizontal = 25.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center) {
                Text(
                    text = category.name,
                    color =
                        if (isSelected) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 15.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
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

// 文章列表鱼骨结构
@Composable
fun ArticleSkeletonItem() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(10.dp)
    ) {
        Column(modifier = Modifier.padding(5.dp)) { // Row for tags/author
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp, 16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .placeholder(
                            visible = true, highlight = PlaceholderHighlight.shimmer()
                        )
                )
                Box(
                    modifier = Modifier
                        .size(60.dp, 16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .placeholder(
                            visible = true, highlight = PlaceholderHighlight.shimmer()
                        )
                )
            } // Title
            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(0.7f)
                    .height(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .placeholder(
                        visible = true, highlight = PlaceholderHighlight.shimmer()
                    )
            ) // Chapter
            Box(
                modifier = Modifier
                    .size(80.dp, 16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .placeholder(
                        visible = true, highlight = PlaceholderHighlight.shimmer()
                    )
            ) // Date
            Box(
                modifier = Modifier
                    .padding(top = 5.dp)
                    .size(100.dp, 12.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .placeholder(
                        visible = true, highlight = PlaceholderHighlight.shimmer()
                    )
            )
        }
    }
}
