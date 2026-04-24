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
            BannerData(id = 1, title = "Welcome", imagePath = "https://www.wanandroid.com/blogimgs/42da12d8-de56-4439-b40c-eab66c227a4b.png"),
            BannerData(id = 2, title = "Welcome", imagePath = "https://www.wanandroid.com/blogimgs/62c1bd68-b5f3-4a3c-a649-7ca8c7dfabe6.png"),
            BannerData(id = 3, title = "Welcome", imagePath = "https://www.wanandroid.com/blogimgs/50c115c2-cf6c-4802-aa7b-a4334de444cd.png")
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

    @Test
    fun snapshot_errorState() {
        paparazzi.snapshot {
            WanAndroidComposeTheme {
                HomeContent(
                    state = HomeListState(errorMsg = "Network Error, Please Try Again"),
                    onAction = {}
                )
            }
        }
    }
}
