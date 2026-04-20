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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.lifecycle.viewmodel.compose.viewModel // <- 添加这个导入
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
import com.syf.wanandroidcompose.project.ProjectViewModel // <- 添加这个导入
import com.syf.wanandroidcompose.project.ProjectView // <- 添加这个导入
import com.syf.wanandroidcompose.tree.TreeViewModel // <- 添加这个导入
import com.syf.wanandroidcompose.tree.TreeView // <- 添加这个导入
import com.syf.wanandroidcompose.profile.ProfileViewModel // <- 添加这个导入
import com.syf.wanandroidcompose.profile.ProfileView // <- 添加这个导入
import kotlinx.serialization.Serializable

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

@Composable
fun AppMainView() {
    val rootNavController = rememberNavController()
    var themeMode by rememberSaveable { mutableStateOf(ThemeMode.SYSTEM) }
    var themeContrast by rememberSaveable { mutableStateOf(ThemeContrast.STANDARD) }
    var fontStyle by rememberSaveable { mutableStateOf(AppFontStyle.SYSTEM) }
    var appLanguage by rememberSaveable { mutableStateOf(currentAppLanguage()) }

    AppTheme(themeMode = themeMode, contrast = themeContrast, fontStyle = fontStyle) {
        NavHost(navController = rootNavController, startDestination = "tabs") {
            composable("tabs") {
                MainTabsScreen(
                    rootNavController = rootNavController,
                    themeMode = themeMode,
                    themeContrast = themeContrast,
                    fontStyle = fontStyle,
                    appLanguage = appLanguage,
                    onToggleThemeMode = {
                        themeMode = when (themeMode) {
                            ThemeMode.SYSTEM -> ThemeMode.LIGHT
                            ThemeMode.LIGHT -> ThemeMode.DARK
                            ThemeMode.DARK -> ThemeMode.SYSTEM
                        }
                    },
                    onToggleThemeContrast = {
                        themeContrast = when (themeContrast) {
                            ThemeContrast.STANDARD -> ThemeContrast.MEDIUM
                            ThemeContrast.MEDIUM -> ThemeContrast.HIGH
                            ThemeContrast.HIGH -> ThemeContrast.STANDARD
                        }
                    },
                    onToggleFontStyle = {
                        fontStyle = when (fontStyle) {
                            AppFontStyle.SYSTEM -> AppFontStyle.KAITI_LIKE
                            AppFontStyle.KAITI_LIKE -> AppFontStyle.SONGTI_LIKE
                            AppFontStyle.SONGTI_LIKE -> AppFontStyle.SERIF
                            AppFontStyle.SERIF -> AppFontStyle.MONOSPACE
                            AppFontStyle.MONOSPACE -> AppFontStyle.SYSTEM
                        }
                    },
                    onToggleLanguage = {
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
            composable("loginRegister") {
                LoginScreen(onBack = { rootNavController.popBackStack() })
            }
        }
    }
}

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
) { // 使用 selectedItem 作为单一状态源
    var selectedItem by rememberSaveable { mutableIntStateOf(0) } // 搜索框输入状态
    var settingsMenuExpanded by rememberSaveable { mutableStateOf(false) }
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val colorScheme = MaterialTheme.colorScheme
    val syncedSystemBarColor = if (isDark) primaryLight else primaryDark
    val syncedSystemBarContentColor = if (isDark) onPrimaryLight else onPrimaryDark
    val themeModeText = when (themeMode) {
        ThemeMode.SYSTEM -> stringResource(R.string.mode_system)
        ThemeMode.LIGHT -> stringResource(R.string.mode_light)
        ThemeMode.DARK -> stringResource(R.string.mode_dark)
    }
    val contrastText = when (themeContrast) {
        ThemeContrast.STANDARD -> stringResource(R.string.contrast_standard)
        ThemeContrast.MEDIUM -> stringResource(R.string.contrast_medium)
        ThemeContrast.HIGH -> stringResource(R.string.contrast_high)
    }
    val fontText = when (fontStyle) {
        AppFontStyle.SYSTEM -> stringResource(R.string.font_system)
        AppFontStyle.KAITI_LIKE -> stringResource(R.string.font_kaiti_like)
        AppFontStyle.SONGTI_LIKE -> stringResource(R.string.font_songti_like)
        AppFontStyle.SERIF -> stringResource(R.string.font_serif)
        AppFontStyle.MONOSPACE -> stringResource(R.string.font_monospace)
    }
    val languageText = when (appLanguage) {
        AppLanguage.SYSTEM -> stringResource(R.string.language_system)
        AppLanguage.ZH_CN -> stringResource(R.string.language_chinese)
        AppLanguage.EN -> stringResource(R.string.language_english)
    }

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
                    color = syncedSystemBarContentColor
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
                        tint = syncedSystemBarContentColor
                    )
                }
                IconButton(onClick = { settingsMenuExpanded = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = stringResource(R.string.action_more_settings),
                        tint = syncedSystemBarContentColor
                    )
                }
                DropdownMenu(
                    expanded = settingsMenuExpanded,
                    onDismissRequest = { settingsMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text =
                                        "${stringResource(R.string.menu_language)}: $languageText"
                            )
                        },
                        onClick = {
                            onToggleLanguage()
                            settingsMenuExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "${stringResource(R.string.menu_theme_mode)}: $themeModeText"
                            )
                        },
                        onClick = {
                            onToggleThemeMode()
                            settingsMenuExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                text =
                                        "${stringResource(R.string.menu_contrast)}: $contrastText"
                            )
                        },
                        onClick = {
                            onToggleThemeContrast()
                            settingsMenuExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(text = "${stringResource(R.string.menu_font)}: $fontText")
                        },
                        onClick = {
                            onToggleFontStyle()
                            settingsMenuExpanded = false
                        }
                    )
                }
            },
            modifier = Modifier.background(syncedSystemBarColor),
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = syncedSystemBarColor
            ),
        )
    }, modifier = Modifier.padding(0.dp), bottomBar = {
        NavigationBar(
            windowInsets = NavigationBarDefaults.windowInsets,
            containerColor = colorScheme.surfaceContainer
        ) {
            AppDestinations.entries.forEachIndexed { index, it ->
                val isSelected = (selectedItem == index)
                NavigationBarItem(
                    icon = {
                    val iconSize by animateDpAsState(if (isSelected) 30.dp else 26.dp)
                    Icon(
                        it.icon,
                        contentDescription = stringResource(it.labelRes),
                        modifier = Modifier.size(iconSize)
                    )
                }, label = {
                    val fontSize by animateFloatAsState(
                        targetValue = if (isSelected) 15f else 13f
                    )
                    Text(text = stringResource(it.labelRes), fontSize = fontSize.sp)
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
fun ProjectDestination(rootNavController: NavController) {
    val viewModel: ProjectViewModel = viewModel(factory = ProjectViewModel.Factory)
    ProjectView(viewModel = viewModel, rootNavController = rootNavController)
}

@Composable
fun TreeDestination(rootNavController: NavController) {
    val viewModel: TreeViewModel = viewModel(factory = TreeViewModel.Factory)
    TreeView(viewModel = viewModel, rootNavController = rootNavController)
}

@Composable
fun ProfileDestination(rootNavController: NavController) {
    val viewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory)
    ProfileView(viewModel = viewModel, rootNavController = rootNavController)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppTheme { AppMainView() }
}