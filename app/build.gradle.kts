plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
//    alias(libs.plugins.room)
//    alias(libs.plugins.ksp)
//    alias(libs.plugins.hilt)
}

android {
    namespace = "com.syf.wanandroidcompose"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.syf.wanandroidcompose"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
//    kotlin {
//        compilerOptions {
//            optIn.add("kotlin.RequiresOptIn")
//        }
//    }

    buildFeatures {
        compose = true
    }
}

//room {
//    schemaDirectory("$projectDir/schemas")
//}
dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
    implementation(libs.androidx.navigation.compose)
    // 网络相关
    implementation(libs.okhttp3)
    implementation(libs.retrofit)
    implementation(libs.retrofit2.kotlinx.serialization.converter)
    implementation(libs.okhttp.logging)
    implementation(libs.timber)

    // 通过OkHttp的拦截器机制
    // 实现在应用通知栏显示网络请求功能
    // https://github.com/ChuckerTeam/chucker
    // debug 下的依赖
    debugImplementation(libs.chucker)
    // prod 下的空依赖
    releaseImplementation(libs.chucker.no.op)
    //序列化 kotlin
    implementation(libs.kotlinx.serialization.json)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    // Room 数据库支持
//    implementation(libs.androidx.room.runtime)
    // 使用 KSP 插件
//    ksp(libs.androidx.room.compiler)
//    kspAndroidTest(libs.hilt.compiler)
//    kspAndroidTest(libs.hilt.android)
    // Kotlin 协程支持
//    implementation(libs.androidx.room.ktx)
    // Paging 3 集成支持
//    implementation(libs.androidx.room.paging)
//    compileOnly(libs.ksp.gradlePlugin)
    //测试
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}