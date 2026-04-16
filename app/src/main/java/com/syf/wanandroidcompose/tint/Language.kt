package com.syf.wanandroidcompose.tint

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

enum class AppLanguage {
    SYSTEM,
    ZH_CN,
    EN
}

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

fun applyAppLanguage(language: AppLanguage) {
    val locales =
            when (language) {
                AppLanguage.SYSTEM -> LocaleListCompat.getEmptyLocaleList()
                AppLanguage.ZH_CN -> LocaleListCompat.forLanguageTags("zh-CN")
                AppLanguage.EN -> LocaleListCompat.forLanguageTags("en")
            }
    AppCompatDelegate.setApplicationLocales(locales)
}
