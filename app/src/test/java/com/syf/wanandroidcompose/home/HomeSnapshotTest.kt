package com.syf.wanandroidcompose.home

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.syf.wanandroidcompose.theme.WanAndroidComposeTheme
import org.junit.Rule
import org.junit.Test

/**
 * 首页截屏测试 (Snapshot Testing)
 * 在 JVM 上运行，无需模拟器。用于捕获像素级 UI 状态。
 */
class HomeSnapshotTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5
    )

    @Test
    fun snapshot_loadingState() {
        paparazzi.snapshot {
            WanAndroidComposeTheme {
                HomeContent(
                    state = HomeListState(isLoading = true),
                    onAction = {}
                )
            }
        }
    }

    @Test
    fun snapshot_dataLoadedState() {
        val articles = listOf(
            ArticleData(id = 1, title = "Snapshot Testing with Paparazzi", author = "WanAndroid"),
            ArticleData(id = 2, title = "MVI Architecture is Great", author = "Compose"),
            ArticleData(id = 3, title = "No Emulator Needed!", author = "Square")
        )
        val banners = listOf(
            BannerData(id = 1, title = "Welcome", imagePath = "")
        )
        
        paparazzi.snapshot {
            WanAndroidComposeTheme {
                HomeContent(
                    state = HomeListState(
                        getArticleData = articles,
                        getBannerData = banners
                    ),
                    onAction = {}
                )
            }
        }
    }
}
