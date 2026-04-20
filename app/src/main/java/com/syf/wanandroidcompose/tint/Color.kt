package com.syf.wanandroidcompose.tint

import androidx.compose.ui.graphics.Color

// Vibrant & Block-based Style - Unified with theme/Color.kt
val OrangePrimary = Color(0xFFF97316)
val OrangeSecondary = Color(0xFFFB923C)
val TrustBlue = Color(0xFF2563EB)
val WarmBackground = Color(0xFFFFF7ED)
val DeepText = Color(0xFF9A3412)

// --- Light Scheme ---
val primaryLight = OrangePrimary
val onPrimaryLight = WarmBackground
val primaryContainerLight = OrangeSecondary.copy(alpha = 0.2f)
val onPrimaryContainerLight = DeepText

val secondaryLight = OrangeSecondary
val onSecondaryLight = WarmBackground
val secondaryContainerLight = OrangeSecondary.copy(alpha = 0.1f)
val onSecondaryContainerLight = DeepText

val tertiaryLight = TrustBlue
val onTertiaryLight = WarmBackground
val tertiaryContainerLight = TrustBlue.copy(alpha = 0.1f)
val onTertiaryContainerLight = DeepText

val errorLight = Color(0xFFBA1A1A)
val onErrorLight = Color(0xFFFFFFFF)
val errorContainerLight = Color(0xFFFFDAD6)
val onErrorContainerLight = Color(0xFF93000A)

val backgroundLight = WarmBackground
val onBackgroundLight = DeepText
val surfaceLight = WarmBackground
val onSurfaceLight = DeepText
val surfaceVariantLight = OrangeSecondary.copy(alpha = 0.1f)
val onSurfaceVariantLight = DeepText.copy(alpha = 0.7f)

val outlineLight = Color(0xFF85736E)
val outlineVariantLight = Color(0xFFD8C2BC)
val scrimLight = Color(0xFF000000)
val inverseSurfaceLight = Color(0xFF392E2B)
val inverseOnSurfaceLight = Color(0xFFFFEDE8)
val inversePrimaryLight = Color(0xFFFFB5A0)

val surfaceDimLight = Color(0xFFE8D6D2)
val surfaceBrightLight = Color(0xFFFFF8F6)
val surfaceContainerLowestLight = Color(0xFFFFFFFF)
val surfaceContainerLowLight = Color(0xFFFFF1ED)
val surfaceContainerLight = Color(0xFFFCEAE5)
val surfaceContainerHighLight = Color(0xFFF7E4E0)
val surfaceContainerHighestLight = Color(0xFFF1DFDA)

// --- Dark Scheme ---
val primaryDark = OrangePrimary
val onPrimaryDark = WarmBackground
val primaryContainerDark = DeepText.copy(alpha = 0.3f)
val onPrimaryContainerDark = WarmBackground

val secondaryDark = OrangeSecondary
val onSecondaryDark = WarmBackground
val secondaryContainerDark = Color(0xFF5D4037)
val onSecondaryContainerDark = WarmBackground

val tertiaryDark = TrustBlue
val onTertiaryDark = WarmBackground
val tertiaryContainerDark = Color(0xFF534619)
val onTertiaryContainerDark = WarmBackground

val errorDark = Color(0xFFFFB4AB)
val onErrorDark = Color(0xFF690005)
val errorContainerDark = Color(0xFF93000A)
val onErrorContainerDark = Color(0xFFFFDAD6)

val backgroundDark = Color(0xFF1A110F)
val onBackgroundDark = WarmBackground
val surfaceDark = Color(0xFF1A110F)
val onSurfaceDark = WarmBackground
val surfaceVariantDark = Color(0xFF53433F)
val onSurfaceVariantDark = WarmBackground.copy(alpha = 0.7f)

val outlineDark = Color(0xFFA08C87)
val outlineVariantDark = Color(0xFF53433F)
val scrimDark = Color(0xFF000000)
val inverseSurfaceDark = Color(0xFFF1DFDA)
val inverseOnSurfaceDark = Color(0xFF392E2B)
val inversePrimaryDark = Color(0xFF8F4C38)

val surfaceDimDark = Color(0xFF1A110F)
val surfaceBrightDark = Color(0xFF423734)
val surfaceContainerLowestDark = Color(0xFF140C0A)
val surfaceContainerLowDark = Color(0xFF231917)
val surfaceContainerDark = Color(0xFF271D1B)
val surfaceContainerHighDark = Color(0xFF322825)
val surfaceContainerHighestDark = Color(0xFF3D322F)

// --- Contrast Schemes (Placeholders to avoid compile errors) ---

// Medium Contrast Light
val primaryLightMediumContrast = primaryLight
val onPrimaryLightMediumContrast = onPrimaryLight
val primaryContainerLightMediumContrast = primaryContainerLight
val onPrimaryContainerLightMediumContrast = onPrimaryContainerLight
val secondaryLightMediumContrast = secondaryLight
val onSecondaryLightMediumContrast = onSecondaryLight
val secondaryContainerLightMediumContrast = secondaryContainerLight
val onSecondaryContainerLightMediumContrast = onSecondaryContainerLight
val tertiaryLightMediumContrast = tertiaryLight
val onTertiaryLightMediumContrast = onTertiaryLight
val tertiaryContainerLightMediumContrast = tertiaryContainerLight
val onTertiaryContainerLightMediumContrast = onTertiaryContainerLight
val errorLightMediumContrast = errorLight
val onErrorLightMediumContrast = onErrorLight
val errorContainerLightMediumContrast = errorContainerLight
val onErrorContainerLightMediumContrast = onErrorContainerLight
val backgroundLightMediumContrast = backgroundLight
val onBackgroundLightMediumContrast = onBackgroundLight
val surfaceLightMediumContrast = surfaceLight
val onSurfaceLightMediumContrast = onSurfaceLight
val surfaceVariantLightMediumContrast = surfaceVariantLight
val onSurfaceVariantLightMediumContrast = onSurfaceVariantLight
val outlineLightMediumContrast = outlineLight
val outlineVariantLightMediumContrast = outlineVariantLight
val scrimLightMediumContrast = scrimLight
val inverseSurfaceLightMediumContrast = inverseSurfaceLight
val inverseOnSurfaceLightMediumContrast = inverseOnSurfaceLight
val inversePrimaryLightMediumContrast = inversePrimaryLight
val surfaceDimLightMediumContrast = surfaceDimLight
val surfaceBrightLightMediumContrast = surfaceBrightLight
val surfaceContainerLowestLightMediumContrast = surfaceContainerLowestLight
val surfaceContainerLowLightMediumContrast = surfaceContainerLowLight
val surfaceContainerLightMediumContrast = surfaceContainerLight
val surfaceContainerHighLightMediumContrast = surfaceContainerHighLight
val surfaceContainerHighestLightMediumContrast = surfaceContainerHighestLight

// High Contrast Light
val primaryLightHighContrast = primaryLight
val onPrimaryLightHighContrast = onPrimaryLight
val primaryContainerLightHighContrast = primaryContainerLight
val onPrimaryContainerLightHighContrast = onPrimaryContainerLight
val secondaryLightHighContrast = secondaryLight
val onSecondaryLightHighContrast = onSecondaryLight
val secondaryContainerLightHighContrast = secondaryContainerLight
val onSecondaryContainerLightHighContrast = onSecondaryContainerLight
val tertiaryLightHighContrast = tertiaryLight
val onTertiaryLightHighContrast = onTertiaryLight
val tertiaryContainerLightHighContrast = tertiaryContainerLight
val onTertiaryContainerLightHighContrast = onTertiaryContainerLight
val errorLightHighContrast = errorLight
val onErrorLightHighContrast = onErrorLight
val errorContainerLightHighContrast = errorContainerLight
val onErrorContainerLightHighContrast = onErrorContainerLight
val backgroundLightHighContrast = backgroundLight
val onBackgroundLightHighContrast = onBackgroundLight
val surfaceLightHighContrast = surfaceLight
val onSurfaceLightHighContrast = onSurfaceLight
val surfaceVariantLightHighContrast = surfaceVariantLight
val onSurfaceVariantLightHighContrast = onSurfaceVariantLight
val outlineLightHighContrast = outlineLight
val outlineVariantLightHighContrast = outlineVariantLight
val scrimLightHighContrast = scrimLight
val inverseSurfaceLightHighContrast = inverseSurfaceLight
val inverseOnSurfaceLightHighContrast = inverseOnSurfaceLight
val inversePrimaryLightHighContrast = inversePrimaryLight
val surfaceDimLightHighContrast = surfaceDimLight
val surfaceBrightLightHighContrast = surfaceBrightLight
val surfaceContainerLowestLightHighContrast = surfaceContainerLowestLight
val surfaceContainerLowLightHighContrast = surfaceContainerLowLight
val surfaceContainerLightHighContrast = surfaceContainerLight
val surfaceContainerHighLightHighContrast = surfaceContainerHighLight
val surfaceContainerHighestLightHighContrast = surfaceContainerHighestLight

// Medium Contrast Dark
val primaryDarkMediumContrast = primaryDark
val onPrimaryDarkMediumContrast = onPrimaryDark
val primaryContainerDarkMediumContrast = primaryContainerDark
val onPrimaryContainerDarkMediumContrast = onPrimaryContainerDark
val secondaryDarkMediumContrast = secondaryDark
val onSecondaryDarkMediumContrast = onSecondaryDark
val secondaryContainerDarkMediumContrast = secondaryContainerDark
val onSecondaryContainerDarkMediumContrast = onSecondaryContainerDark
val tertiaryDarkMediumContrast = tertiaryDark
val onTertiaryDarkMediumContrast = onTertiaryDark
val tertiaryContainerDarkMediumContrast = tertiaryContainerDark
val onTertiaryContainerDarkMediumContrast = onTertiaryContainerDark
val errorDarkMediumContrast = errorDark
val onErrorDarkMediumContrast = onErrorDark
val errorContainerDarkMediumContrast = errorContainerDark
val onErrorContainerDarkMediumContrast = onErrorContainerDark
val backgroundDarkMediumContrast = backgroundDark
val onBackgroundDarkMediumContrast = onBackgroundDark
val surfaceDarkMediumContrast = surfaceDark
val onSurfaceDarkMediumContrast = onSurfaceDark
val surfaceVariantDarkMediumContrast = surfaceVariantDark
val onSurfaceVariantDarkMediumContrast = onSurfaceVariantDark
val outlineDarkMediumContrast = outlineDark
val outlineVariantDarkMediumContrast = outlineVariantDark
val scrimDarkMediumContrast = scrimDark
val inverseSurfaceDarkMediumContrast = inverseSurfaceDark
val inverseOnSurfaceDarkMediumContrast = inverseOnSurfaceDark
val inversePrimaryDarkMediumContrast = inversePrimaryDark
val surfaceDimDarkMediumContrast = surfaceDimDark
val surfaceBrightDarkMediumContrast = surfaceBrightDark
val surfaceContainerLowestDarkMediumContrast = surfaceContainerLowestDark
val surfaceContainerLowDarkMediumContrast = surfaceContainerLowDark
val surfaceContainerDarkMediumContrast = surfaceContainerDark
val surfaceContainerHighDarkMediumContrast = surfaceContainerHighDark
val surfaceContainerHighestDarkMediumContrast = surfaceContainerHighestDark

// High Contrast Dark
val primaryDarkHighContrast = primaryDark
val onPrimaryDarkHighContrast = onPrimaryDark
val primaryContainerDarkHighContrast = primaryContainerDark
val onPrimaryContainerDarkHighContrast = onPrimaryContainerDark
val secondaryDarkHighContrast = secondaryDark
val onSecondaryDarkHighContrast = onSecondaryDark
val secondaryContainerDarkHighContrast = secondaryContainerDark
val onSecondaryContainerDarkHighContrast = onSecondaryContainerDark
val tertiaryDarkHighContrast = tertiaryDark
val onTertiaryDarkHighContrast = onTertiaryDark
val tertiaryContainerDarkHighContrast = tertiaryContainerDark
val onTertiaryContainerDarkHighContrast = onTertiaryDark
val errorDarkHighContrast = errorDark
val onErrorDarkHighContrast = onErrorDark
val errorContainerDarkHighContrast = errorContainerDark
val onErrorContainerDarkHighContrast = onErrorDark
val backgroundDarkHighContrast = backgroundDark
val onBackgroundDarkHighContrast = onBackgroundDark
val surfaceDarkHighContrast = surfaceDark
val onSurfaceDarkHighContrast = onSurfaceDark
val surfaceVariantDarkHighContrast = surfaceVariantDark
val onSurfaceVariantDarkHighContrast = onSurfaceVariantDark
val outlineDarkHighContrast = outlineDark
val outlineVariantDarkHighContrast = outlineVariantDark
val scrimDarkHighContrast = scrimDark
val inverseSurfaceDarkHighContrast = inverseSurfaceDark
val inverseOnSurfaceDarkHighContrast = inverseOnSurfaceDark
val inversePrimaryDarkHighContrast = inversePrimaryDark
val surfaceDimDarkHighContrast = surfaceDimDark
val surfaceBrightDarkHighContrast = surfaceBrightDark
val surfaceContainerLowestDarkHighContrast = surfaceContainerLowestDark
val surfaceContainerLowDarkHighContrast = surfaceContainerLowDark
val surfaceContainerDarkHighContrast = surfaceContainerDark
val surfaceContainerHighDarkHighContrast = surfaceContainerHighDark
val surfaceContainerHighestDarkHighContrast = surfaceContainerHighestDark
