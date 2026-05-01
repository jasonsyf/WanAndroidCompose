package com.syf.wanandroidcompose.tree

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.syf.wanandroidcompose.theme.WanAndroidComposeTheme
import org.junit.Rule
import org.junit.Test

/**
 * 体系页面截屏测试
 */
class TreeSnapshotTest {
    @get:Rule
    val paparazzi =
        Paparazzi(
            deviceConfig = DeviceConfig.PIXEL_5,
        )

    @Test
    fun snapshot_tree_initial_state() {
        paparazzi.snapshot {
            WanAndroidComposeTheme {
                TreeContent(
                    state =
                        TreeState(
                            categories =
                                listOf(
                                    TreeCategory(id = 1, name = "体系"),
                                    TreeCategory(id = 2, name = "公众号"),
                                ),
                            selectedParentId = 1,
                        ),
                    onCategoryClick = {},
                    onSubCategoryClick = {},
                    onRefresh = {},
                    onLoadMore = {},
                    onArticleClick = {},
                )
            }
        }
    }
}
