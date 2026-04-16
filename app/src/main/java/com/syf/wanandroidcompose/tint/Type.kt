package com.syf.wanandroidcompose.tint

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily

enum class AppFontStyle {
    SYSTEM,
    KAITI_LIKE,
    SONGTI_LIKE,
    SERIF,
    MONOSPACE
}

private val baseTypography = Typography()

private fun Typography.withFontFamily(fontFamily: FontFamily): Typography =
    copy(
        displayLarge = displayLarge.copy(fontFamily = fontFamily),
        displayMedium = displayMedium.copy(fontFamily = fontFamily),
        displaySmall = displaySmall.copy(fontFamily = fontFamily),
        headlineLarge = headlineLarge.copy(fontFamily = fontFamily),
        headlineMedium = headlineMedium.copy(fontFamily = fontFamily),
        headlineSmall = headlineSmall.copy(fontFamily = fontFamily),
        titleLarge = titleLarge.copy(fontFamily = fontFamily),
        titleMedium = titleMedium.copy(fontFamily = fontFamily),
        titleSmall = titleSmall.copy(fontFamily = fontFamily),
        bodyLarge = bodyLarge.copy(fontFamily = fontFamily),
        bodyMedium = bodyMedium.copy(fontFamily = fontFamily),
        bodySmall = bodySmall.copy(fontFamily = fontFamily),
        labelLarge = labelLarge.copy(fontFamily = fontFamily),
        labelMedium = labelMedium.copy(fontFamily = fontFamily),
        labelSmall = labelSmall.copy(fontFamily = fontFamily),
    )

fun appTypography(style: AppFontStyle): Typography {
    val fontFamily = when (style) {
        AppFontStyle.SYSTEM -> FontFamily.Default
        AppFontStyle.KAITI_LIKE -> FontFamily.Cursive
        AppFontStyle.SONGTI_LIKE -> FontFamily.Serif
        AppFontStyle.SERIF -> FontFamily.Serif
        AppFontStyle.MONOSPACE -> FontFamily.Monospace
    }
    return baseTypography.withFontFamily(fontFamily)
}
