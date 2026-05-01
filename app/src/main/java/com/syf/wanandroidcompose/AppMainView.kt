package com.syf.wanandroidcompose

import com.syf.wanandroidcompose.login.LoginScreen
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.syf.wanandroidcompose.tint.AppLanguage
import com.syf.wanandroidcompose.home.HomeView
import com.syf.wanandroidcompose.home.detail.DetailView
import com.syf.wanandroidcompose.tint.AppFontStyle
import com.syf.wanandroidcompose.tint.AppTheme
import com.syf.wanandroidcompose.tint.ThemeContrast
import com.syf.wanandroidcompose.tint.ThemeMode
import com.syf.wanandroidcompose.tint.applyAppLanguage
import com.syf.wanandroidcompose.tint.currentAppLanguage
import com.syf.wanandroidcompose.tint.onPrimaryDark
import com.syf.wanandroidcompose.tint.onPrimaryLight
import com.syf.wanandroidcompose.tint.primaryDark
import com.syf.wanandroidcompose.tint.primaryLight
import com.syf.wanandroidcompose.project.projectViewModel
import com.syf.wanandroidcompose.project.projectView
import com.syf.wanandroidcompose.tree.treeViewModel
import com.syf.wanandroidcompose.tree.treeView
import com.syf.wanandroidcompose.profile.ProfileViewModel
import com.syf.wanandroidcompose.profile.ProfileView
import kotlinx.serialization.Serializable

/**
 * 定义应用的底部导航目标页面
 * @param labelRes 标签文本资源ID
 * @param icon 图标
 * @param topTitleRes 顶部标题资源ID
 */
enum class AppDestinations(
    val labelRes: Int,
    val icon: ImageVector,
    val topTitleRes: Int,
) {
    HOME(R.string.tab_home, Icons.Default.Home, R.string.top_title_home),
    PROJECT(R.string.tab_project, Icons.Filled.Face, R.string.top_title_project),
    TREE(R.string.tab_tree, Icons.Default.Menu, R.string.top_title_tree),
    PROFILE(R.string.tab_profile, Icons.Default.Person, R.string.top_title_profile),
}

@Serializable
object Profile

@Serializable
object FriendsList

/**
 * 应用主视图，包含根导航和主题设置
 */
@Composable
fun AppMainView() {
    // 创建根导航控制器
    val rootNavController = rememberNavController()
    // 主题模式状态
    var themeMode by rememberSaveable { mutableStateOf(ThemeMode.SYSTEM) }
    // 主题对比度状态
    var themeContrast by rememberSaveable { mutableStateOf(ThemeContrast.STANDARD) }
    // 字体样式状态
    var fontStyle by rememberSaveable { mutableStateOf(AppFontStyle.SYSTEM) }
    // 应用语言状态
    var appLanguage by rememberSaveable { mutableStateOf(currentAppLanguage()) }

    // 应用当前主题
    AppTheme(themeMode = themeMode, contrast = themeContrast, fontStyle = fontStyle) {
        // 设置导航路由
        NavHost(navController = rootNavController, startDestination = "tabs") {
            // 主标签页屏幕
            composable("tabs") {
                MainTabsScreen(
                    rootNavController = rootNavController,
                    themeMode = themeMode,
                    themeContrast = themeContrast,
                    fontStyle = fontStyle,
                    appLanguage = appLanguage,
                    onToggleThemeMode = {
                        // 切换主题模式
                        themeMode = when (themeMode) {
                            ThemeMode.SYSTEM -> ThemeMode.LIGHT
                            ThemeMode.LIGHT -> ThemeMode.DARK
                            ThemeMode.DARK -> ThemeMode.SYSTEM
                        }
                    },
                    onToggleThemeContrast = {
                        // 切换主题对比度
                        themeContrast = when (themeContrast) {
                            ThemeContrast.STANDARD -> ThemeContrast.MEDIUM
                            ThemeContrast.MEDIUM -> ThemeContrast.HIGH
                            ThemeContrast.HIGH -> ThemeContrast.STANDARD
                        }
                    },
                    onToggleFontStyle = {
                        // 切换字体样式
                        fontStyle = when (fontStyle) {
                            AppFontStyle.SYSTEM -> AppFontStyle.KAITI_LIKE
                            AppFontStyle.KAITI_LIKE -> AppFontStyle.SONGTI_LIKE
                            AppFontStyle.SONGTI_LIKE -> AppFontStyle.SERIF
                            AppFontStyle.SERIF -> AppFontStyle.MONOSPACE
                            AppFontStyle.MONOSPACE -> AppFontStyle.SYSTEM
                        }
                    },
                    onToggleLanguage = {
                        // 切换应用语言
                        val nextLanguage =
                                when (appLanguage) {
                                    AppLanguage.SYSTEM -> AppLanguage.ZH_CN
                                    AppLanguage.ZH_CN -> AppLanguage.EN
                                    AppLanguage.EN -> AppLanguage.SYSTEM
                                }
                        appLanguage = nextLanguage
                        applyAppLanguage(nextLanguage)
                    }
                )
            }
            // 详情页路由
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
            // 登录注册页路由
            composable("loginRegister") {
                LoginScreen(onBack = { rootNavController.popBackStack() })
            }
        }
    }
}

/**
 * 主标签页屏幕，包含顶部应用栏、底部导航和内容区域
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTabsScreen(
    rootNavController: NavController,
    themeMode: ThemeMode,
    themeContrast: ThemeContrast,
    fontStyle: AppFontStyle,
    appLanguage: AppLanguage,
    onToggleThemeMode: () -> Unit,
    onToggleThemeContrast: () -> Unit,
    onToggleFontStyle: () -> Unit,
    onToggleLanguage: () -> Unit
) {
    // 当前选中的标签页索引
    var selectedItem by rememberSaveable { mutableIntStateOf(0) }
    // 判断当前是否为深色模式
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    // 获取当前颜色方案
    val colorScheme = MaterialTheme.colorScheme
    val syncedSystemBarColor = if (isDark) primaryLight else primaryDark
    val syncedSystemBarContentColor = if (isDark) onPrimaryLight else onPrimaryDark
    // 获取当前主题模式的文本描述
    val themeModeText = when (themeMode) {
        ThemeMode.SYSTEM -> stringResource(R.string.mode_system)
        ThemeMode.LIGHT -> stringResource(R.string.mode_light)
        ThemeMode.DARK -> stringResource(R.string.mode_dark)
    }
    // 获取当前主题对比度的文本描述
    val contrastText = when (themeContrast) {
        ThemeContrast.STANDARD -> stringResource(R.string.contrast_standard)
        ThemeContrast.MEDIUM -> stringResource(R.string.contrast_medium)
        ThemeContrast.HIGH -> stringResource(R.string.contrast_high)
    }
    // 获取当前字体样式的文本描述
    val fontText = when (fontStyle) {
        AppFontStyle.SYSTEM -> stringResource(R.string.font_system)
        AppFontStyle.KAITI_LIKE -> stringResource(R.string.font_kaiti_like)
        AppFontStyle.SONGTI_LIKE -> stringResource(R.string.font_songti_like)
        AppFontStyle.SERIF -> stringResource(R.string.font_serif)
        AppFontStyle.MONOSPACE -> stringResource(R.string.font_monospace)
    }
    // 获取当前应用语言的文本描述
    val languageText = when (appLanguage) {
        AppLanguage.SYSTEM -> stringResource(R.string.language_system)
        AppLanguage.ZH_CN -> stringResource(R.string.language_chinese)
        AppLanguage.EN -> stringResource(R.string.language_english)
    }

    // 自定义底部导航项的颜色
    val myNavigationItemColors = NavigationBarItemDefaults.colors(
        indicatorColor = Color.Transparent,
        selectedTextColor = MaterialTheme.colorScheme.primary,
        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
        selectedIconColor = MaterialTheme.colorScheme.primary,
        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(AppDestinations.entries[selectedItem].topTitleRes),
                    style = MaterialTheme.typography.titleLarge
                )
            },
            actions = { // 右侧操作按钮
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .size(35.dp),
                        contentDescription = "搜索按钮"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
        )
    }, modifier = Modifier.padding(0.dp), bottomBar = {
        NavigationBar(
            windowInsets = NavigationBarDefaults.windowInsets,
            containerColor = colorScheme.surfaceContainer
        ) {
            // 遍历所有导航目标并创建底部导航项
            AppDestinations.entries.forEachIndexed { index, it ->
                val isSelected = (selectedItem == index)
                NavigationBarItem(
                    icon = {
                    val iconSize by animateDpAsState(if (isSelected) 30.dp else 26.dp, label = "")
                    Icon(
                        it.icon,
                        contentDescription = stringResource(it.labelRes),
                        modifier = Modifier.size(iconSize)
                    )
                }, label = {
                    val fontSize by animateFloatAsState(
                        targetValue = if (isSelected) 15f else 13f, label = ""
                    )
                    Text(text = stringResource(it.labelRes), fontSize = fontSize.sp)
                }, selected = isSelected, onClick = {
                    // 点击时仅更新选中的索引
                    selectedItem = index
                }, colors = myNavigationItemColors
                )
            }
        }
    }) { contentPadding ->
        // 使用 contentPadding 来避免内容被底部导航栏遮挡
        Box(modifier = Modifier.padding(contentPadding)) {
            val currentDestination =
                AppDestinations.entries[selectedItem]
            // 根据当前选中的目标来显示不同的内容页面
            when (currentDestination) {
                AppDestinations.HOME -> HomeDestination(rootNavController)
                AppDestinations.PROJECT -> ProjectDestination(rootNavController)
                AppDestinations.TREE -> TreeDestination(rootNavController)
                AppDestinations.PROFILE -> ProfileDestination(
                    rootNavController = rootNavController,
                    themeMode = themeMode,
                    themeContrast = themeContrast,
                    fontStyle = fontStyle,
                    appLanguage = appLanguage,
                    themeModeText = themeModeText,
                    contrastText = contrastText,

                    fontText = fontText,
                    languageText = languageText,
                    onToggleThemeMode = onToggleThemeMode,
                    onToggleThemeContrast = onToggleThemeContrast,
                    onToggleFontStyle = onToggleFontStyle,
                    onToggleLanguage = onToggleLanguage
                )
            }
        }
    }
}

/**
 * “首页”目标的 Composable
 * @param rootNavController 根导航控制器，用于页面跳转
 */
@Composable
fun HomeDestination(rootNavController: NavController) {
    // HomeView 负责展示首页的具体内容，如文章列表
    HomeView(rootNavController)
}

/**
 * “项目”目标的 Composable
 * @param rootNavController 根导航控制器
 */
@Composable
fun ProjectDestination(rootNavController: NavController) {
    val viewModel: ProjectViewModel = viewModel(factory = ProjectViewModel.Factory)
    projectView(viewModel = viewModel, rootNavController = rootNavController)
}

/**
 * “体系”目标的 Composable
 * @param rootNavController 根导航控制器
 */
@Composable
fun TreeDestination(rootNavController: NavController) {
    val viewModel: TreeViewModel = viewModel(factory = TreeViewModel.Factory)
    treeView(viewModel = viewModel, rootNavController = rootNavController)
}

/**
 * “我的”目标的 Composable
 * @param rootNavController 根导航控制器
 * @param themeMode 主题模式
 * @param themeContrast 主题对比度
 * @param fontStyle 字体样式
 * @param appLanguage 应用语言
 * @param themeModeText 主题模式文本
 * @param contrastText 对比度文本
 * @param fontText 字体文本
 * @param languageText 语言文本
 * @param onToggleThemeMode 切换主题模式的回调
 * @param onToggleThemeContrast 切换主题对比度的回调
 * @param onToggleFontStyle 切换字体样式的回调
 * @param onToggleLanguage 切换语言的回调
 */
@Composable
fun ProfileDestination(
    rootNavController: NavController,
    themeMode: ThemeMode,
    themeContrast: ThemeContrast,
    fontStyle: AppFontStyle,
    appLanguage: AppLanguage,
    themeModeText: String,
    contrastText: String,
    fontText: String,
    languageText: String,
    onToggleThemeMode: () -> Unit,
    onToggleThemeContrast: () -> Unit,
    onToggleFontStyle: () -> Unit,
    onToggleLanguage: () -> Unit
) {
    val viewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory)
    ProfileView(
        viewModel = viewModel,
        rootNavController = rootNavController,
        themeModeText = themeModeText,
        contrastText = contrastText,
        fontText = fontText,
        languageText = languageText,
        onToggleThemeMode = onToggleThemeMode,
        onToggleThemeContrast = onToggleThemeContrast,
        onToggleFontStyle = onToggleFontStyle,
        onToggleLanguage = onToggleLanguage
    )
}

/**
 * Composable 预览
 */
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppTheme { AppMainView() }
}
