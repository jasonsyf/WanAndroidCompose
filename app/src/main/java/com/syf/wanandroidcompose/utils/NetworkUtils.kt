package com.syf.wanandroidcompose.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

/** 网络工具类 */
object NetworkUtils {
    /**
     * 检查网络是否可用
     * @param context 上下文
     * @return true 表示网络可用，false 表示网络不可用
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                ?: return false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            // 移除了 NET_CAPABILITY_VALIDATED 校验，因为它在网络快速切换或某些运营商网络下更新较慢，
            // 容易导致误判（即明明有网但显示不可用）。
        } else {
            @Suppress("DEPRECATION") val networkInfo = connectivityManager.activeNetworkInfo
            @Suppress("DEPRECATION") return networkInfo?.isConnected == true
        }
    }
}
