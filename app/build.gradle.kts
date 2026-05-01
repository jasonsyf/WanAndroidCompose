import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.paparazzi)
    // Optional, provides the @Serialize annotation for autogeneration of Serializers.
}

android {
    namespace = "com.syf.wanandroidcompose"
    compileSdk = 36

    signingConfigs {
        create("release") {
            storeFile = (System.getenv("KEYSTORE_FILE") ?: project.findProperty("KEYSTORE_FILE")?.toString())?.let { file(it) }
            storePassword = System.getenv("KEYSTORE_PASSWORD") ?: project.findProperty("KEYSTORE_PASSWORD")?.toString()
            keyAlias = System.getenv("KEY_ALIAS") ?: project.findProperty("KEY_ALIAS")?.toString()
            keyPassword = System.getenv("KEY_PASSWORD") ?: project.findProperty("KEY_PASSWORD")?.toString()
        }
    }
    defaultConfig {
        applicationId = "com.syf.wanandroidcompose"
        minSdk = 24
        targetSdk = 36
        versionCode = 3
        versionName = "1.0.2"

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE-notice.md"
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

androidComponents {
    onVariants { variant ->
        val buildDate = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
        val capitalName = variant.name.replaceFirstChar { it.uppercase() }
        
        val renameTask = tasks.register<Copy>("rename${capitalName}Apk") {
            from(variant.artifacts.get(com.android.build.api.artifact.SingleArtifact.APK))
            into(layout.buildDirectory.dir("outputs/renamed-apks"))
            
            val versionName = android.defaultConfig.versionName
            val buildTypeName = variant.buildType?.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase() else it.toString()
            } ?: ""
            
            rename { fileName ->
                if (fileName.endsWith(".apk")) {
                    "WanAndroid_v${versionName}_${buildTypeName}_${buildDate}.apk"
                } else fileName
            }
        }

        tasks.matching { it.name == "assemble$capitalName" }.configureEach {
            finalizedBy(renameTask)
        }
    }
}

// 在顶层配置所有 Test 任务
tasks.withType<Test>().configureEach {
    // 关键修复：解决 M1 Mac 上 Robolectric Native 库全量运行时的 UnsatisfiedLinkError
    // 设置每个测试类都在独立进程运行
    forkEvery = 1L
    
    // 设置系统属性
    systemProperty("robolectric.graphicsMode", "NATIVE")
    systemProperty("robolectric.sqliteMode", "NATIVE")
    systemProperty("robolectric.offline", "false")
    
    // Paparazzi 兼容性
    reports.html.required = false
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
    implementation(libs.okhttp3)
    implementation(libs.retrofit)
    implementation(libs.retrofit2.kotlinx.serialization.converter)
    implementation(libs.okhttp.logging)
    implementation(libs.timber)
    implementation(libs.coilcompose)
    debugImplementation(libs.chucker)
    releaseImplementation(libs.chucker.no.op)
    implementation(libs.kotlinx.serialization.json)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    kspAndroidTest(libs.hilt.compiler)
    kspAndroidTest(libs.hilt.android)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)
    compileOnly(libs.ksp.gradlePlugin)
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.paparazzi)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation("io.mockk:mockk-android:1.13.13")
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.material3.adaptive.navigation3)
    implementation(libs.kotlinx.serialization.core)
}
