# WanAndroid Compose

<div align="center">

![App Icon](/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png)

**A Modern Android Learning Platform built with Jetpack Compose**

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-purple.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Compose-Latest-brightgreen.svg)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![API](https://img.shields.io/badge/API-24%2B-orange.svg)](https://android-arsenal.com/api?level=24)

[中文文档](README_CN.md)

</div>

## ✨ Features

- 🎨 **Modern UI** - Built entirely with Jetpack Compose and Material Design 3.
- 🏗️ **MVI Architecture** - Unidirectional data flow with robust state management via `BaseViewModelOptimized`.
- 💾 **Offline First** - Room database with Paging 3 support for seamless local caching.
- 🔄 **Reactive Programming** - Extensive use of Kotlin Flow and Coroutines for asynchronous logic.
- 🌐 **Network Layer** - Retrofit + OkHttp with custom interceptors and Chucker for debugging.
- 🌈 **Dynamic Theming** - Supports multiple theme modes (Light/Dark/System), contrast levels, and custom font styles.
- 🌍 **I18n** - Built-in support for multiple languages (English and Chinese).
- 📱 **Adaptive Navigation** - Ready for different screen sizes with Material 3 Adaptive Navigation Suite.
- 🔧 **Dependency Injection** - Hilt for clean and testable code.

## 📂 App Modules

- 🏠 **Home** - Articles, banners, and daily updates.
- 📂 **Project** - Categorized projects from the WanAndroid community.
- 🌿 **Tree** - Knowledge hierarchy and authority navigation.
- 👤 **Profile** - User management, settings, and personalization.
- 🔑 **Login/Register** - Complete authentication flow.

## 🛠️ Tech Stack

### Core
- **Jetpack Compose** - Modern declarative UI.
- **Navigation Compose** - Type-safe navigation.
- **Hilt** - Dependency injection.
- **Room** - Local persistence with Flow/Paging 3.

### Networking & Data
- **Retrofit & OkHttp** - Networking.
- **Kotlin Serialization** - Modern JSON parsing.
- **Coil** - Image loading for Compose.
- **Chucker** - On-device network inspection.

### Quality & Performance
- **KSP** - Fast annotation processing.
- **Timber** - Structured logging.
- **Strong Skipping Mode** - Optimized Compose performance.
- **Lifecycle-aware State** - Uses `collectAsStateWithLifecycle` for resource efficiency.

## 📦 Project Structure

```
WanAndroidCompose/
├── app/
│   ├── src/main/java/com/syf/wanandroidcompose/
│   │   ├── common/          # Reusable base components (MVI, ViewModels)
│   │   ├── home/            # Home feature (Articles, Banners)
│   │   ├── project/         # Project feature (Categorized lists)
│   │   ├── tree/            # Tree feature (Knowledge hierarchy)
│   │   ├── profile/         # Profile & Settings
│   │   ├── login/           # Auth flow
│   │   ├── network/         # Retrofit setup & Result wrappers
│   │   ├── tint/            # Dynamic Theming & Colors
│   │   ├── i18n/            # Localization support
│   │   └── theme/           # Compose typography & shapes
│   └── build.gradle.kts
├── gradle/libs.versions.toml # Version catalog
└── COMPOSE-AUDIT-REPORT.md   # Performance audit findings
```

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug | 2024.2.1 or higher
- JDK 17 or higher
- Android SDK with API 24+
- Gradle 8.10+

### Building
1. Clone the repo.
2. Open in Android Studio.
3. Sync Gradle and run on an emulator or physical device.

## 🎯 Key Highlights

### MVI with Optimized ViewModel
The project uses a custom `BaseViewModelOptimized` that handles:
- State persistence and replay.
- Coroutine error handling.
- Event-based side effects (Actions).

### Dynamic Tint System
Allows users to switch between:
- **Theme Modes**: Light, Dark, System.
- **Contrast**: Standard, Medium, High.
- **Font Styles**: System, Kaiti, Songti, Serif, Monospace.

### Performance Optimized
Follows Jetpack Compose best practices:
- Efficient state collection with `collectAsStateWithLifecycle`.
- Optimized list performance using `key` in LazyLayouts (In progress).
- Stable domain models for reduced recompositions.

## 📄 License
Copyright 2024 Sun Yufeng. Licensed under the Apache License, Version 2.0.

---
<div align="center">
Made with ❤️ and Jetpack Compose
</div>
