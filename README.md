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

- 🎨 **Modern UI** - Built entirely with Jetpack Compose
- 🏗️ **MVI Architecture** - Unidirectional data flow with state management
- 💾 **Offline First** - Room database for local caching
- 🔄 **Reactive Programming** - Kotlin Flow & Coroutines
- 🌐 **Network Layer** - Retrofit + OkHttp with custom interceptors
- 🎯 **Type Safety** - Kotlin Serialization for JSON parsing
- 📱 **Material Design 3** - Latest Material Design components
- 🔧 **Dependency Injection** - Hilt for DI
- 🧪 **Testing Ready** - Architecture designed for testability

## 📸 Screenshots

<div align="center">
<img src="screenshots/home.png" width="250" />
<img src="screenshots/article.png" width="250" />
<img src="screenshots/profile.png" width="250" />
</div>

## 🛠️ Tech Stack

### Architecture & Design Patterns
- **MVI (Model-View-Intent)** - Predictable state management
- **Repository Pattern** - Data layer abstraction
- **Use Case Pattern** - Business logic encapsulation

### Jetpack Components
- **Compose** - Declarative UI framework
- **Navigation Compose** - Type-safe navigation
- **Room** - SQLite database with Flow support
- **Lifecycle** - Lifecycle-aware components
- **ViewModel** - UI state management
- **Hilt** - Dependency injection

### Networking & Data
- **Retrofit** - REST API client
- **OkHttp** - HTTP client with interceptors
- **Kotlin Serialization** - JSON serialization
- **Coil** - Image loading

### Reactive Programming
- **Kotlin Coroutines** - Asynchronous programming
- **Flow** - Reactive data streams
- **StateFlow & SharedFlow** - State management

### Code Quality
- **KSP** - Kotlin Symbol Processing
- **Timber** - Logging utility
- **Chucker** - Network inspector (debug builds)

## 📦 Project Structure

```
WanAndroidCompose/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/syf/wanandroidcompose/
│   │   │   │   ├── common/          # Base classes & utilities
│   │   │   │   │   ├── BaseViewModel.kt
│   │   │   │   │   └── BaseViewModelOptimized.kt
│   │   │   │   ├── home/            # Home feature module
│   │   │   │   │   ├── HomeView.kt  # Compose UI
│   │   │   │   │   ├── HomeViewModel.kt
│   │   │   │   │   ├── HomeRepository.kt
│   │   │   │   │   ├── HomeAction.kt
│   │   │   │   │   ├── local/       # Room database
│   │   │   │   │   └── HomeApiService.kt
│   │   │   │   ├── network/         # Network layer
│   │   │   │   │   ├── ApiService.kt
│   │   │   │   │   ├── Result.kt    # Network result wrapper
│   │   │   │   │   └── ApiExtensions.kt
│   │   │   │   ├── utils/           # Utility classes
│   │   │   │   └── WanAndroidApplication.kt
│   │   │   └── res/                 # Resources
│   │   └── test/                    # Unit tests
│   └── build.gradle.kts
├── gradle/
├── build.gradle.kts
└── settings.gradle.kts
```

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog | 2023.1.1 or higher
- JDK 11 or higher
- Android SDK with API 24+
- Gradle 8.5+

### Building the Project

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/WanAndroidCompose.git
   cd WanAndroidCompose
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory

3. **Sync Gradle**
   - Android Studio should automatically sync Gradle files
   - Wait for dependencies to download

4. **Run the app**
   ```bash
   ./gradlew assembleDebug
   # or click the "Run" button in Android Studio
   ```

### Signing Configuration (Optional)

For release builds, configure signing in `local.properties`:

```properties
KEYSTORE_FILE=/path/to/your/keystore.jks
KEYSTORE_PASSWORD=your_store_password
KEY_ALIAS=your_key_alias
KEY_PASSWORD=your_key_password
```

## 📱 Features Implementation

### Home Screen
- ✅ Article list with pagination
- ✅ Pull-to-refresh functionality
- ✅ Banner carousel with auto-scroll
- ✅ WeChat public accounts section
- ✅ Offline caching with Room
- ✅ Shimmer loading effects
- ✅ Error handling with Snackbar

### Network Layer
- ✅ Generic `Result` wrapper for API responses
- ✅ Automatic loading, success, and error states
- ✅ Flow-based reactive data streams
- ✅ Network availability detection
- ✅ Custom error handling

### State Management
- ✅ MVI pattern with sealed classes
- ✅ Unidirectional data flow
- ✅ StateFlow for UI state
- ✅ Immutable state objects
- ✅ Centralized state updates

## 🎯 Key Highlights

### Optimized Base ViewModel
Custom `BaseViewModelOptimized` with:
- Coroutine exception handling
- Automatic state replay
- Action-based event system
- Network action tracking
- Built-in logging

### Flow-Based Repository
Repositories emit `Flow<Result<T>>` for:
- Automatic loading states
- Consistent error handling
- Easy composability
- Lifecycle awareness

### Local-First Architecture
- Room database for offline support
- Single source of truth pattern
- Network + cache strategy
- Automatic UI updates via Flow

## 📝 Code Example

### Making a Network Request

```kotlin
// Repository
fun fetchArticles(): Flow<Result<List<ArticleData>>> = flow {
    emit(Result.Loading)
    try {
        val response = apiService.getArticleList(page = 0)
        if (response.isSuccess && response.data != null) {
            // Update local cache
            database.insertArticles(response.data.datas)
            emit(Result.Success(response.data.datas))
        } else {
            emit(Result.apiError(response))
        }
    } catch (e: Exception) {
        emit(Result.fromException(e, "Failed to fetch articles"))
    }
}

// ViewModel
viewModelScope.launch {
    repository.fetchArticles().collect { result ->
        when (result) {
            is Result.Loading -> updateState { copy(isLoading = true) }
            is Result.Success -> updateState { copy(isLoading = false, articles = result.data) }
            is Result.Error -> updateState { copy(isLoading = false, error = result.message) }
        }
    }
}
```

## 🧪 Testing

```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

## 📄 License

```
Copyright 2024 Sun Yufeng

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📮 Contact

- **Author**: Sun Yufeng
- **Email**: your.email@example.com
- **GitHub**: [@jasonsyf](https://github.com/jasonsyf)

## 🙏 Acknowledgments

- [WanAndroid API](https://www.wanandroid.com/blog/show/2) - Free and open API
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - Modern Android UI toolkit
- [Material Design 3](https://m3.material.io/) - Design system

---

<div align="center">
Made with ❤️ and Jetpack Compose
</div>
