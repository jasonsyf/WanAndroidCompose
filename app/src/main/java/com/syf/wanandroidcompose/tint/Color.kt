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

// --- Contrast Schemes ---

// Medium Contrast Light (Darker text, more vibrant primary)
val primaryLightMediumContrast = Color(0xFFC2410C) // Darker Orange
val onPrimaryLightMediumContrast = Color(0xFFFFFFFF)
val primaryContainerLightMediumContrast = Color(0xFFEA580C)
val onPrimaryContainerLightMediumContrast = Color(0xFFFFFFFF)
val secondaryLightMediumContrast = Color(0xFFC2410C)
val onSecondaryLightMediumContrast = Color(0xFFFFFFFF)
val secondaryContainerLightMediumContrast = Color(0xFFEA580C)
val onSecondaryContainerLightMediumContrast = Color(0xFFFFFFFF)
val tertiaryLightMediumContrast = Color(0xFF1D4ED8) // Darker Blue
val onTertiaryLightMediumContrast = Color(0xFFFFFFFF)
val tertiaryContainerLightMediumContrast = Color(0xFF2563EB)
val onTertiaryContainerLightMediumContrast = Color(0xFFFFFFFF)
val errorLightMediumContrast = Color(0xFF8C1D18)
val onErrorLightMediumContrast = Color(0xFFFFFFFF)
val errorContainerLightMediumContrast = Color(0xFFDA362C)
val onErrorContainerLightMediumContrast = Color(0xFFFFFFFF)
val backgroundLightMediumContrast = Color(0xFFFFF7ED)
val onBackgroundLightMediumContrast = Color(0xFF1F2937)
val surfaceLightMediumContrast = Color(0xFFFFF7ED)
val onSurfaceLightMediumContrast = Color(0xFF111827)
val surfaceVariantLightMediumContrast = Color(0xFFFDE68A)
val onSurfaceVariantLightMediumContrast = Color(0xFF4B5563)
val outlineLightMediumContrast = Color(0xFF4B5563)
val outlineVariantLightMediumContrast = Color(0xFF6B7280)
val scrimLightMediumContrast = Color(0xFF000000)
val inverseSurfaceLightMediumContrast = Color(0xFF374151)
val inverseOnSurfaceLightMediumContrast = Color(0xFFF9FAFB)
val inversePrimaryLightMediumContrast = Color(0xFFFDBA74)
val surfaceDimLightMediumContrast = Color(0xFFE5E7EB)
val surfaceBrightLightMediumContrast = Color(0xFFFFF7ED)
val surfaceContainerLowestLightMediumContrast = Color(0xFFFFFFFF)
val surfaceContainerLowLightMediumContrast = Color(0xFFF9FAFB)
val surfaceContainerLightMediumContrast = Color(0xFFF3F4F6)
val surfaceContainerHighLightMediumContrast = Color(0xFFE5E7EB)
val surfaceContainerHighestLightMediumContrast = Color(0xFFD1D5DB)

// High Contrast Light (Maximum legibility)
val primaryLightHighContrast = Color(0xFF7C2D12) // Very Dark Orange
val onPrimaryLightHighContrast = Color(0xFFFFFFFF)
val primaryContainerLightHighContrast = Color(0xFF9A3412)
val onPrimaryContainerLightHighContrast = Color(0xFFFFFFFF)
val secondaryLightHighContrast = Color(0xFF7C2D12)
val onSecondaryLightHighContrast = Color(0xFFFFFFFF)
val secondaryContainerLightHighContrast = Color(0xFF9A3412)
val onSecondaryContainerLightHighContrast = Color(0xFFFFFFFF)
val tertiaryLightHighContrast = Color(0xFF1E3A8A) // Very Dark Blue
val onTertiaryLightHighContrast = Color(0xFFFFFFFF)
val tertiaryContainerLightHighContrast = Color(0xFF1E40AF)
val onTertiaryContainerLightHighContrast = Color(0xFFFFFFFF)
val errorLightHighContrast = Color(0xFF4E0002)
val onErrorLightHighContrast = Color(0xFFFFFFFF)
val errorContainerLightHighContrast = Color(0xFF8C0009)
val onErrorContainerLightHighContrast = Color(0xFFFFFFFF)
val backgroundLightHighContrast = Color(0xFFFFFFFF)
val onBackgroundLightHighContrast = Color(0xFF000000)
val surfaceLightHighContrast = Color(0xFFFFFFFF)
val onSurfaceLightHighContrast = Color(0xFF000000)
val surfaceVariantLightHighContrast = Color(0xFFFFFFFF)
val onSurfaceVariantLightHighContrast = Color(0xFF000000)
val outlineLightHighContrast = Color(0xFF000000)
val outlineVariantLightHighContrast = Color(0xFF000000)
val scrimLightHighContrast = Color(0xFF000000)
val inverseSurfaceLightHighContrast = Color(0xFF000000)
val inverseOnSurfaceLightHighContrast = Color(0xFFFFFFFF)
val inversePrimaryLightHighContrast = Color(0xFFFDBA74)
val surfaceDimLightHighContrast = Color(0xFFD1D5DB)
val surfaceBrightLightHighContrast = Color(0xFFFFFFFF)
val surfaceContainerLowestLightHighContrast = Color(0xFFFFFFFF)
val surfaceContainerLowLightHighContrast = Color(0xFFF3F4F6)
val surfaceContainerLightHighContrast = Color(0xFFE5E7EB)
val surfaceContainerHighLightHighContrast = Color(0xFFD1D5DB)
val surfaceContainerHighestLightHighContrast = Color(0xFF9CA3AF)

// Medium Contrast Dark (Lighter text, brighter primary)
val primaryDarkMediumContrast = Color(0xFFFDBA74)
val onPrimaryDarkMediumContrast = Color(0xFF431407)
val primaryContainerDarkMediumContrast = Color(0xFFF97316)
val onPrimaryContainerDarkMediumContrast = Color(0xFF000000)
val secondaryDarkMediumContrast = Color(0xFFFDBA74)
val onSecondaryDarkMediumContrast = Color(0xFF431407)
val secondaryContainerDarkMediumContrast = Color(0xFFF97316)
val onSecondaryContainerDarkMediumContrast = Color(0xFF000000)
val tertiaryDarkMediumContrast = Color(0xFF93C5FD)
val onTertiaryDarkMediumContrast = Color(0xFF0C194D)
val tertiaryContainerDarkMediumContrast = Color(0xFF3B82F6)
val onTertiaryContainerDarkMediumContrast = Color(0xFF000000)
val errorDarkMediumContrast = Color(0xFFFFB4AB)
val onErrorDarkMediumContrast = Color(0xFF370001)
val errorContainerDarkMediumContrast = Color(0xFFFF5449)
val onErrorContainerDarkMediumContrast = Color(0xFF000000)
val backgroundDarkMediumContrast = Color(0xFF111827)
val onBackgroundDarkMediumContrast = Color(0xFFF9FAFB)
val surfaceDarkMediumContrast = Color(0xFF111827)
val onSurfaceDarkMediumContrast = Color(0xFFF9FAFB)
val surfaceVariantDarkMediumContrast = Color(0xFF374151)
val onSurfaceVariantDarkMediumContrast = Color(0xFFE5E7EB)
val outlineDarkMediumContrast = Color(0xFF9CA3AF)
val outlineVariantDarkMediumContrast = Color(0xFF6B7280)
val scrimDarkMediumContrast = Color(0xFF000000)
val inverseSurfaceDarkMediumContrast = Color(0xFFF9FAFB)
val inverseOnSurfaceDarkMediumContrast = Color(0xFF1F2937)
val inversePrimaryDarkMediumContrast = Color(0xFFC2410C)
val surfaceDimDarkMediumContrast = Color(0xFF111827)
val surfaceBrightDarkMediumContrast = Color(0xFF374151)
val surfaceContainerLowestDarkMediumContrast = Color(0xFF030712)
val surfaceContainerLowDarkMediumContrast = Color(0xFF1F2937)
val surfaceContainerDarkMediumContrast = Color(0xFF374151)
val surfaceContainerHighDarkMediumContrast = Color(0xFF4B5563)
val surfaceContainerHighestDarkMediumContrast = Color(0xFF6B7280)

// High Contrast Dark (Maximum legibility)
val primaryDarkHighContrast = Color(0xFFFFEDD5)
val onPrimaryDarkHighContrast = Color(0xFF000000)
val primaryContainerDarkHighContrast = Color(0xFFFDBA74)
val onPrimaryContainerDarkHighContrast = Color(0xFF000000)
val secondaryDarkHighContrast = Color(0xFFFFEDD5)
val onSecondaryDarkHighContrast = Color(0xFF000000)
val secondaryContainerDarkHighContrast = Color(0xFFFDBA74)
val onSecondaryContainerDarkHighContrast = Color(0xFF000000)
val tertiaryDarkHighContrast = Color(0xFFDBEAFE)
val onTertiaryDarkHighContrast = Color(0xFF000000)
val tertiaryContainerDarkHighContrast = Color(0xFF93C5FD)
val onTertiaryContainerDarkHighContrast = Color(0xFF000000)
val errorDarkHighContrast = Color(0xFFFFECE9)
val onErrorDarkHighContrast = Color(0xFF000000)
val errorContainerDarkHighContrast = Color(0xFFFFAEA4)
val onErrorContainerDarkHighContrast = Color(0xFF220001)
val backgroundDarkHighContrast = Color(0xFF000000)
val onBackgroundDarkHighContrast = Color(0xFFFFFFFF)
val surfaceDarkHighContrast = Color(0xFF000000)
val onSurfaceDarkHighContrast = Color(0xFFFFFFFF)
val surfaceVariantDarkHighContrast = Color(0xFF000000)
val onSurfaceVariantDarkHighContrast = Color(0xFFFFFFFF)
val outlineDarkHighContrast = Color(0xFFFFFFFF)
val outlineVariantDarkHighContrast = Color(0xFFFFFFFF)
val scrimDarkHighContrast = Color(0xFF000000)
val inverseSurfaceDarkHighContrast = Color(0xFFFFFFFF)
val inverseOnSurfaceDarkHighContrast = Color(0xFF000000)
val inversePrimaryDarkHighContrast = Color(0xFF7C2D12)
val surfaceDimDarkHighContrast = Color(0xFF000000)
val surfaceBrightDarkHighContrast = Color(0xFF374151)
val surfaceContainerLowestDarkHighContrast = Color(0xFF000000)
val surfaceContainerLowDarkHighContrast = Color(0xFF111827)
val surfaceContainerDarkHighContrast = Color(0xFF1F2937)
val surfaceContainerHighDarkHighContrast = Color(0xFF374151)
val surfaceContainerHighestDarkHighContrast = Color(0xFF4B5563)
