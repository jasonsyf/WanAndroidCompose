package com.syf.wanandroidcompose.i18n

import androidx.annotation.StringRes
import com.syf.wanandroidcompose.WanAndroidApplication
import com.syf.wanandroidcompose.R

object AppText {
    fun get(@StringRes resId: Int, vararg formatArgs: Any): String {
        val app = runCatching { WanAndroidApplication.instance }.getOrNull()
        if (app != null) {
            return if (formatArgs.isEmpty()) {
                app.getString(resId)
            } else {
                app.getString(resId, *formatArgs)
            }
        }
        return when (resId) {
            R.string.error_unknown -> "Unknown error"
            R.string.error_request_failed -> "Request failed"
            else -> ""
        }
    }
}
