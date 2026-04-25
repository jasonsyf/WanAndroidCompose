# WanAndroid Compose

<div align="center">

![App Icon](/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png)

**A Modern Android Learning Platform built with Jetpack Compose**

[![Kotlin](https://img.shields.io/badge/Kotlin-2.1%2B-purple.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Compose-Latest-brightgreen.svg)](https://developer.android.com/jetpack/compose)
[![AGP](https://img.shields.io/badge/AGP-9.1.1-blue.svg)](https://developer.android.com/studio/releases/gradle-plugin)
[![Gradle](https://img.shields.io/badge/Gradle-9.4.1-blue.svg)](https://gradle.org/releases/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

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

## 🛡️ Development Workflow & Quality

The project features a rigorous development workflow powered by **Git Hooks** and **GitHub Actions**:

### ⚓ Git Hooks (Local Quality Gate)
- **Pre-commit**: Automatically runs `ktlint` checks, prevents large image assets (>200KB), and warns about debug logs.
- **Commit-msg**: Enforces **Conventional Commits** and requires Chinese descriptions.
- **Pre-push**: Mandatory **Unit Tests**, **Android Lint**, and **UI Snapshot Verification** before code leaves your machine.

### 🤖 CI/CD Pipeline (GitHub Actions)
- **Parallel Execution**: Lint and Build jobs run in parallel for faster feedback.
- **App Size Monitoring**: Automatically compares APK size between PR and master, posting reports directly to PR comments.
- **Snapshot Testing**: Pixel-perfect UI regression detection using **Paparazzi**.
- **Automated Release**: Tags starting with `v*` automatically trigger a signed APK release.

## 🧪 Testing

The project maintains a high quality standard through a comprehensive testing suite:
- **Unit Tests**: Business logic and state flow validation (JUnit 4 + MockK + Turbine).
- **Snapshot Tests**: Pixel-perfect UI regression (Paparazzi).
- **UI Tests**: Interaction and rendering verification (Compose Test).

See the [TESTING-GUIDE.md](TESTING-GUIDE.md) for more details.

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

### Quality & Performance
- **Gradle 9.4.1** - Latest build system performance.
- **AGP 9.1.1** - Support for modern Android features.
- **Configuration Cache** - Enabled for lightning-fast incremental builds.
- **Strong Skipping Mode** - Optimized Compose performance.

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
│   │   └── theme/           # Compose typography & shapes
├── scripts/
│   └── git-hooks/           # Local quality automation scripts
├── gradle/libs.versions.toml # Version catalog
└── COMPOSE-AUDIT-REPORT.md   # Performance audit findings
```

## 🚀 Getting Started

### Prerequisites
- **Android Studio Meerkat | 2024.3.1** or higher
- JDK 17 or higher
- Android SDK with API 24+
- Gradle 9.4+

### Building
1. Clone the repo.
2. Open in Android Studio.
3. Sync Gradle. The **Git Hooks** will be installed automatically upon the first build.
4. Run on an emulator or physical device.

## 📄 License
Copyright 2024 Sun Yufeng. Licensed under the Apache License, Version 2.0.

---
<div align="center">
Made with ❤️ and Jetpack Compose
</div>
