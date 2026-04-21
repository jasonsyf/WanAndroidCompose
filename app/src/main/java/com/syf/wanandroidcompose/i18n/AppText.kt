package com.syf.wanandroidcompose.i18n

import androidx.annotation.StringRes
import com.syf.wanandroidcompose.WanAndroidApplication
import com.syf.wanandroidcompose.R

/**
 * 字符串资源工具类
 * 提供在非 Composable 或 Context 环境下获取字符串资源的能力
 */
object AppText {
    /**
     * 获取字符串资源
     * @param resId 字符串资源 ID
     * @param formatArgs 格式化参数
     * @return 格式化后的字符串内容
     */
    fun get(@StringRes resId: Int, vararg formatArgs: Any): String {
        val app = runCatching { WanAndroidApplication.instance }.getOrNull()
        if (app != null) {
            return if (formatArgs.isEmpty()) {
                app.getString(resId)
            } else {
                app.getString(resId, *formatArgs)
            }
        }
        // 当 Application 实例尚未初始化时的降级处理
        return when (resId) {
            R.string.error_unknown -> "未知错误"
            R.string.error_request_failed -> "请求失败"
            else -> ""
        }
    }
}
