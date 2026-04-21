package com.syf.wanandroidcompose.tint

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

/**
 * 应用支持的语言类型
 */
enum class AppLanguage {
    /** 跟随系统 */
    SYSTEM,
    /** 简体中文 */
    ZH_CN,
    /** 英语 */
    EN
}

/**
 * 获取当前应用设置的语言
 */
fun currentAppLanguage(): AppLanguage {
    val locales = AppCompatDelegate.getApplicationLocales()
    if (locales.isEmpty) return AppLanguage.SYSTEM
    val language = locales[0]?.language ?: return AppLanguage.SYSTEM
    return when {
        language.startsWith("zh", ignoreCase = true) -> AppLanguage.ZH_CN
        language.startsWith("en", ignoreCase = true) -> AppLanguage.EN
        else -> AppLanguage.SYSTEM
    }
}

/**
 * 应用指定的语言
 * @param language 目标语言
 */
fun applyAppLanguage(language: AppLanguage) {
    val locales =
            when (language) {
                AppLanguage.SYSTEM -> LocaleListCompat.getEmptyLocaleList()
                AppLanguage.ZH_CN -> LocaleListCompat.forLanguageTags("zh-CN")
                AppLanguage.EN -> LocaleListCompat.forLanguageTags("en")
            }
    AppCompatDelegate.setApplicationLocales(locales)
}
