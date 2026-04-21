import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    // Optional, provides the @Serialize annotation for autogeneration of Serializers.
}


android {
    namespace = "com.syf.wanandroidcompose"
    compileSdk {
        version = release(36)
    }

    signingConfigs {
        create("release") {
//            // TODO: 请修改以下配置为您的实际签名信息
//            storeFile = file("path/to/your/keystore.jks")  // 密钥库文件路径
//            storePassword = "your_store_password"          // 密钥库密码
//            keyAlias = "your_key_alias"                    // 密钥别名
//            keyPassword = "your_key_password"              // 密钥密码
//
            // 推荐做法：使用环境变量或 local.properties 存储敏感信息
            // 示例：
            storeFile = file(project.findProperty("KEYSTORE_FILE").toString())
            storePassword = project.findProperty("KEYSTORE_PASSWORD").toString()
            keyAlias = project.findProperty("KEY_ALIAS").toString()
            keyPassword = project.findProperty("KEY_PASSWORD").toString()
        }
    }
    defaultConfig {
        applicationId = "com.syf.wanandroidcompose"
        minSdk = 24
        targetSdk = 36
        versionCode = 2
        versionName = "1.0.1"

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
            signingConfig = signingConfigs.getByName("release")
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
        buildConfig = true
    }
}

android.applicationVariants.all {
    outputs.all {
        val buildTypeName = buildType.name.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }
        val buildDate = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
        (this as com.android.build.gradle.internal.api.BaseVariantOutputImpl).outputFileName =
            "WanAndroid_v${versionName}_${buildTypeName}_${buildDate}.apk"
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}
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
    implementation(libs.coilcompose)
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
    implementation(libs.androidx.room.runtime)
    // 使用 KSP 插件
    ksp(libs.androidx.room.compiler)
    kspAndroidTest(libs.hilt.compiler)
    kspAndroidTest(libs.hilt.android)
    // Kotlin 协程支持
    implementation(libs.androidx.room.ktx)
    // Paging 3 集成支持
    implementation(libs.androidx.room.paging)
    compileOnly(libs.ksp.gradlePlugin)
    //测试
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.material3.adaptive.navigation3)
    implementation(libs.kotlinx.serialization.core)
}