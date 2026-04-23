// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.paparazzi) apply false
    // 依赖注入相关插件
    // Hilt插件，用于依赖注入框架的支持
//    alias(libs.plugins.hilt) apply false
    // KSP (Kotlin Symbol Processing)插件，用于注解处理
//    alias(libs.plugins.ksp) apply false
    // Android库插件，用于构建Android库模块
//    alias(libs.plugins.android.library) apply false
}