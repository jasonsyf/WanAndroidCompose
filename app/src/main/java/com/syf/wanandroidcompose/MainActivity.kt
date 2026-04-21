package com.syf.wanandroidcompose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

/**
 * 应用主活动
 *
 * 这是应用启动时的主屏幕。
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 隐藏 ActionBar
        supportActionBar?.hide()
        // 开启全屏沉浸式体验
        enableEdgeToEdge()
        // 设置 Compose 内容
        setContent {
            // AppMainView 是整个应用的 Compose 主视图
            AppMainView()
        }
    }
}
