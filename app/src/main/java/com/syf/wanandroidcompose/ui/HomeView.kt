@file:OptIn(ExperimentalMaterial3Api::class)

package com.syf.wanandroidcompose.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.syf.wanandroidcompose.R
import com.syf.wanandroidcompose.ui.home.HomeViewModel
import com.syf.wanandroidcompose.ui.theme.WanAndroidComposeTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeView(navController: NavController) {
    val viewModel: HomeViewModel = viewModel()

    data class PersonItem(
        val id: Int,
        @DrawableRes val imageResId: Int,
        val name: String,
        val link: String
    )

    val personItems = remember {
        listOf(
            PersonItem(0, R.drawable.pig1, "张鸿洋1", "www.baidu.com"),
            PersonItem(1, R.drawable.pig2, "赵鸿洋2", "www.baidu.com"),
            PersonItem(2, R.drawable.pig3, "李鸿洋3", "www.baidu.com"),
            PersonItem(3, R.drawable.pig4, "孙鸿洋4", "www.baidu.com"),
            PersonItem(4, R.drawable.pig5, "马鸿洋5", "www.baidu.com"),
            PersonItem(5, R.drawable.pig4, "闫鸿洋6", "www.baidu.com"),
            PersonItem(6, R.drawable.pig3, "刘鸿洋7", "www.baidu.com"),
            PersonItem(7, R.drawable.pig2, "苏鸿洋8", "www.baidu.com"),
        )
    }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var selectedIndex by remember { mutableStateOf(0) }
    LazyColumn(state = listState, modifier = Modifier.fillMaxSize().background(Color.White)) {
        item {
            Carouse()
        }
        item {
            Text(
                "优质公众号",
                modifier = Modifier
                    .padding(horizontal = 10.dp),
                fontSize = 25.sp,
                fontWeight = FontWeight.W700
            )
        }
        item {
            LazyRow(modifier = Modifier.padding(5.dp)) {
                items(personItems.size) { i ->
                    val item = personItems[i]
                    Column(
                        Modifier
                            .padding(5.dp)
                            .size(60.dp, 80.dp)
                    ) {
                        Image(
                            painter = painterResource(id = item.imageResId),
                            "",
                            modifier = Modifier
                                .clip(shape = CircleShape)
                                .size(60.dp),
                            contentScale = ContentScale.Crop
                        )
                        Text(item.name)
                    }
                }
            }
        }
        stickyHeader {
            ChipTabRow(
                listOf("全部", "面试", "Flutter", "Flutter", "Flutter", "Flutter"),
                selectedIndex,
                onTabSelected = {
                    selectedIndex = it
                    // 切换时滚动到 header（这里 header 的位置是第 3 项）
//                    scope.launch {
//                        listState.animateScrollToItem(3)
//                    }
                }
            )
        }

        items(personItems.size) { i ->
            val item = personItems[i]
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .clip(
                        RoundedCornerShape(20.dp)
                    )
                    .background(Color(0xffF1F5F9))
                    .height(180.dp)

            ) {
                Icon(
                    Icons.Filled.FavoriteBorder,
                    contentDescription = "",
                    modifier = Modifier.padding(15.dp).size(25.dp)
                        .align(Alignment.TopEnd)

                    ,
                )
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                        Text("置顶",
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.Red)
                                .size(50.dp,25.dp),
                            fontSize = 15.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Text("新",
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.Blue)
                                .size(35.dp,25.dp),
                            fontSize = 15.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Text("鸿洋",

                            fontSize = 15.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                    }
                    Text("Jetpack Compose 实战：构建现代化 AndroidUI",
                        fontWeight = FontWeight.W700,
                        fontSize = 18.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(vertical = 5.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Jetpack Compose",
                            modifier = Modifier
                                .border(1.dp,Color.Gray, RoundedCornerShape(7.dp)),
                            fontSize = 15.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                        Text("UI",
                            modifier = Modifier
                                .size(35.dp,25.dp)
                                .border(1.dp,Color.Gray, RoundedCornerShape(7.dp)),

                            fontSize = 15.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                    }
                    Text("开源项目    2025-10-10",
                        modifier = Modifier.padding(vertical = 5.dp),
                        fontSize = 15.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

    }

}


@Composable
fun Carouse() {
    data class CarouselItem(
        val id: Int,
        @DrawableRes val imageResId: Int,
        val contentDescription: String
    )

    val items = remember {
        listOf(
            CarouselItem(0, R.drawable.pig1, "pig1"),
            CarouselItem(1, R.drawable.pig2, "pig2"),
            CarouselItem(2, R.drawable.pig3, "pig3"),
            CarouselItem(3, R.drawable.pig4, "pig4"),
            CarouselItem(4, R.drawable.pig5, "pig5"),
        )
    }
    // 初始页面定在中间某个位置，保证左右都能滑
    val initialPage = Int.MAX_VALUE / 2
    // 修正初始页，使其对应数据列表的第一个元素 (index 0)
    val startIndex = initialPage - (initialPage % items.size)
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    val pagerState = rememberPagerState(
        initialPage = startIndex,
        pageCount = { Int.MAX_VALUE }
    )
    // carousel state
    //    val paperState = rememberPagerState { items.count() }
    Box() {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(top = 16.dp, bottom = 8.dp),
            // make each item occupy full screen width so only one visible at a time
            //            preferredItemWidth = screenWidthDp,
            //            itemSpacing = 0.dp,
            contentPadding = PaddingValues(horizontal = 0.dp)
        ) { i ->
            val actualIndex = i % items.size
            val item = items[actualIndex]
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = 10.dp)
                    .clip(RoundedCornerShape(12.dp)),
                painter = painterResource(id = item.imageResId),
                contentDescription = item.contentDescription,
                contentScale = ContentScale.Crop
            )
        }

        // indicators: dot for unselected, short bar for selected
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .offset(0.dp, -10.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val currentIndex = pagerState.currentPage % items.size
            items.forEachIndexed { index, _ ->
                if (index == currentIndex) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .width(24.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color.Black)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .width(8.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                    )
                }
            }
        }
    }

    // 2. 监听用户是否正在拖拽
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()
    val autoScrollDelay = 3000L
    // 3. 自动轮播逻辑
    // 当 isDragged 为 true 时，Key 发生变化，LaunchedEffect 重启/取消
    // 这里利用 isDragged 作为 key，当用户拖拽时，自动轮播协程会被取消；停止拖拽后重新开始
    LaunchedEffect(isDragged) {
        if (!isDragged) {
            while (true) {
                delay(autoScrollDelay)
                try {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                } catch (e: Exception) {
                    // 忽略异常或处理页面销毁时的取消异常
                }
            }
        }
    }
}

@Composable
fun ChipTabRow(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .background(Color.White)
            .fillMaxWidth()
            .zIndex(1f)
            .padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 10.dp), // 两端留白
        horizontalArrangement = Arrangement.spacedBy(12.dp) // 标签间距
    ) {
        itemsIndexed(tabs) { index, title ->
            val isSelected = selectedTabIndex == index

            // 单个 Tab 的样式
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp)) // 圆角矩形
                    .background(
                        if (isSelected) Color(0xFF1F2531) else Color(0xFFF2F5F9)
                    )
                    .clickable { onTabSelected(index) }
                    .padding(horizontal = 25.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    color = if (isSelected) Color.White else Color(0xFF6B778D),
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
        HomeView(viewModel())
//        Greeting("Android")
    }
}