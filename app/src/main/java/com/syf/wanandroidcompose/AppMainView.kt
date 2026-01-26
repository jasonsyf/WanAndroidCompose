package com.syf.wanandroidcompose

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.syf.wanandroidcompose.home.HomeView
import com.syf.wanandroidcompose.home.detail.DetailView
import com.syf.wanandroidcompose.theme.WanAndroidComposeTheme
import kotlinx.serialization.Serializable

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
    val topTitle: String,
) {
    HOME("首页", Icons.Default.Home, "玩Android"),
    PROJECT(" 项目", Icons.Filled.Face, "开源项目"),
    TREE("体系", Icons.Default.Menu, "知识体系"),
    PROFILE("我的", Icons.Default.Person, "个人中心"),
}

@Serializable
object Profile

@Serializable
object FriendsList

@Composable
fun AppMainView() {
    val rootNavController = rememberNavController()

    NavHost(navController = rootNavController, startDestination = "tabs") {
        composable("tabs") { MainTabsScreen(rootNavController) }
        composable(
            route = "detail/{url}", arguments = listOf(
                navArgument("url") {
                    type = NavType.StringType
                })
        ) { backStackEntry ->
            val url = backStackEntry.arguments?.getString("url")
            if (url != null) {
                DetailView(url = url, onBack = { rootNavController.popBackStack() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTabsScreen(rootNavController: NavController) { // 使用 selectedItem 作为单一状态源
    var selectedItem by rememberSaveable { mutableIntStateOf(0) } // 搜索框输入状态
    var searchQuery by rememberSaveable { mutableStateOf("") } // Use NavigationBarItemDefaults.colors directly for NavigationBarItem
    val myNavigationItemColors = NavigationBarItemDefaults.colors(
        indicatorColor = Color.Transparent,
        selectedTextColor = MaterialTheme.colorScheme.primary,
        unselectedTextColor = Color.Gray,
        selectedIconColor = MaterialTheme.colorScheme.primary,
        unselectedIconColor = Color.Gray
    )

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(
                    text = AppDestinations.entries[selectedItem].topTitle, color = Color.White
                )
            }, // 在 actions 中放置右侧的搜索框
            actions = {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .size(35.dp),
                        contentDescription = "Selected icon button",
                    )
                }
            }, // make the visual background a horizontal green gradient
            modifier = Modifier.background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xff6366f1), // darker
                        Color(0xFFec4899) // lighter
                    )
                )
            ), // ensure the TopAppBar itself doesn't draw an opaque container over the
            // gradient
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            ),
        )
    }, modifier = Modifier.padding(0.dp), bottomBar = {
        NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
            AppDestinations.entries.forEachIndexed { index, it ->
                val isSelected = (selectedItem == index)
                NavigationBarItem(
                    icon = {
                    val iconSize by animateDpAsState(if (isSelected) 30.dp else 26.dp)
                    Icon(
                        it.icon,
                        contentDescription = it.label,
                        modifier = Modifier.size(iconSize)
                    )
                }, label = {
                    val fontSize by animateFloatAsState(
                        targetValue = if (isSelected) 15f else 13f
                    )
                    Text(text = it.label, fontSize = fontSize.sp)
                }, selected = isSelected, onClick = { // 仅更新选中索引
                    selectedItem = index
                }, colors = myNavigationItemColors
                )
            }
        }
    }) { contentPadding -> // 使用 contentPadding 包裹内容，防止底部导航遮挡
        Box(modifier = Modifier.padding(contentPadding)) {
            val currentDestination =
                AppDestinations.entries[selectedItem] // Pass rootNavController to destinations so they can navigate to full-screen detail
            when (currentDestination) {
                AppDestinations.HOME -> HomeDestination(rootNavController)
                AppDestinations.PROJECT -> ProjectDestination(rootNavController)
                AppDestinations.TREE -> TreeDestination(rootNavController)
                AppDestinations.PROFILE -> ProfileDestination(rootNavController)
            }
        }
    }
}

@Composable
fun HomeDestination(rootNavController: NavController) { // Directly use HomeView with the root controller.
    // Assuming HomeView logic (LazyColumn etc) fits here.
    // If Home had internal sub-pages, we'd need nested navigation,
    // but for now it's just the feed -> detail (root).
    HomeView(rootNavController)
}

@Composable
fun ProjectDestination(rootNavController: NavController) { // Reuse HomeView for now as per previous logic
    HomeView(rootNavController)
}

@Composable
fun TreeDestination(rootNavController: NavController) {
    HomeView(rootNavController)
}

@Composable
fun ProfileDestination(rootNavController: NavController) {
    HomeView(rootNavController)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WanAndroidComposeTheme { AppMainView() }
}
