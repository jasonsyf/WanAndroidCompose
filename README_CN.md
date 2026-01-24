# WanAndroid Compose

<div align="center">

![应用图标](/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png)

**基于 Jetpack Compose 构建的现代化 Android 学习平台**

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-purple.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Compose-最新版-brightgreen.svg)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![API](https://img.shields.io/badge/API-24%2B-orange.svg)](https://android-arsenal.com/api?level=24)

[English](README.md)

</div>

## ✨ 特性

- 🎨 **现代化 UI** - 完全使用 Jetpack Compose 构建
- 🏗️ **MVI 架构** - 单向数据流与状态管理
- 💾 **离线优先** - 使用 Room 数据库进行本地缓存
- 🔄 **响应式编程** - Kotlin Flow 和协程
- 🌐 **网络层** - Retrofit + OkHttp 自定义拦截器
- 🎯 **类型安全** - Kotlin 序列化进行 JSON 解析
- 📱 **Material Design 3** - 最新的 Material Design 组件
- 🔧 **依赖注入** - 使用 Hilt 进行依赖注入
- 🧪 **易于测试** - 为可测试性设计的架构

## 📸 应用截图

<div align="center">
<img src="screenshots/home.png" width="250" />
<img src="screenshots/article.png" width="250" />
<img src="screenshots/profile.png" width="250" />
</div>

## 🛠️ 技术栈

### 架构与设计模式
- **MVI (Model-View-Intent)** - 可预测的状态管理
- **Repository 模式** - 数据层抽象
- **Use Case 模式** - 业务逻辑封装

### Jetpack 组件
- **Compose** - 声明式 UI 框架
- **Navigation Compose** - 类型安全的导航
- **Room** - 支持 Flow 的 SQLite 数据库
- **Lifecycle** - 生命周期感知组件
- **ViewModel** - UI 状态管理
- **Hilt** - 依赖注入

### 网络与数据
- **Retrofit** - REST API 客户端
- **OkHttp** - HTTP 客户端与拦截器
- **Kotlin Serialization** - JSON 序列化
- **Coil** - 图片加载

### 响应式编程
- **Kotlin Coroutines** - 异步编程
- **Flow** - 响应式数据流
- **StateFlow 和 SharedFlow** - 状态管理

### 代码质量
- **KSP** - Kotlin 符号处理
- **Timber** - 日志工具
- **Chucker** - 网络检查器（Debug 版本）

## 📦 项目结构

```
WanAndroidCompose/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/syf/wanandroidcompose/
│   │   │   │   ├── common/          # 基础类和工具
│   │   │   │   │   ├── BaseViewModel.kt
│   │   │   │   │   └── BaseViewModelOptimized.kt
│   │   │   │   ├── home/            # 首页功能模块
│   │   │   │   │   ├── HomeView.kt  # Compose UI
│   │   │   │   │   ├── HomeViewModel.kt
│   │   │   │   │   ├── HomeRepository.kt
│   │   │   │   │   ├── HomeAction.kt
│   │   │   │   │   ├── local/       # Room 数据库
│   │   │   │   │   └── HomeApiService.kt
│   │   │   │   ├── network/         # 网络层
│   │   │   │   │   ├── ApiService.kt
│   │   │   │   │   ├── Result.kt    # 网络请求结果包装
│   │   │   │   │   └── ApiExtensions.kt
│   │   │   │   ├── utils/           # 工具类
│   │   │   │   └── WanAndroidApplication.kt
│   │   │   └── res/                 # 资源文件
│   │   └── test/                    # 单元测试
│   └── build.gradle.kts
├── gradle/
├── build.gradle.kts
└── settings.gradle.kts
```

## 🚀 开始使用

### 环境要求

- Android Studio Hedgehog | 2023.1.1 或更高版本
- JDK 11 或更高版本
- Android SDK API 24+
- Gradle 8.5+

### 构建项目

1. **克隆仓库**
   ```bash
   git clone https://github.com/yourusername/WanAndroidCompose.git
   cd WanAndroidCompose
   ```

2. **在 Android Studio 中打开**
   - 打开 Android Studio
   - 选择 "Open an Existing Project"
   - 导航到克隆的目录

3. **同步 Gradle**
   - Android Studio 会自动同步 Gradle 文件
   - 等待依赖下载完成

4. **运行应用**
   ```bash
   ./gradlew assembleDebug
   # 或在 Android Studio 中点击 "Run" 按钮
   ```

### 签名配置（可选）

对于 Release 构建，在 `local.properties` 中配置签名：

```properties
KEYSTORE_FILE=/path/to/your/keystore.jks
KEYSTORE_PASSWORD=your_store_password
KEY_ALIAS=your_key_alias
KEY_PASSWORD=your_key_password
```

## 📱 功能实现

### 首页
- ✅ 文章列表与分页加载
- ✅ 下拉刷新功能
- ✅ 轮播图及自动滚动
- ✅ 公众号专区
- ✅ Room 离线缓存
- ✅ Shimmer 加载效果
- ✅ Snackbar 错误处理

### 网络层
- ✅ 通用 `Result` 包装器处理 API 响应
- ✅ 自动处理加载、成功和错误状态
- ✅ 基于 Flow 的响应式数据流
- ✅ 网络可用性检测
- ✅ 自定义错误处理

### 状态管理
- ✅ 使用密封类的 MVI 模式
- ✅ 单向数据流
- ✅ StateFlow 管理 UI 状态
- ✅ 不可变状态对象
- ✅ 集中式状态更新

## 🎯 核心亮点

### 优化的 Base ViewModel
自定义 `BaseViewModelOptimized` 包含：
- 协程异常处理
- 自动状态重放
- 基于 Action 的事件系统
- 网络操作跟踪
- 内置日志记录

### 基于 Flow 的 Repository
Repository 发射 `Flow<Result<T>>` 提供：
- 自动加载状态
- 一致的错误处理
- 易于组合
- 生命周期感知

### 本地优先架构
- Room 数据库支持离线
- 单一数据源原则
- 网络 + 缓存策略
- 通过 Flow 自动更新 UI

## 📝 代码示例

### 发起网络请求

```kotlin
// Repository
fun fetchArticles(): Flow<Result<List<ArticleData>>> = flow {
    emit(Result.Loading)
    try {
        val response = apiService.getArticleList(page = 0)
        if (response.isSuccess && response.data != null) {
            // 更新本地缓存
            database.insertArticles(response.data.datas)
            emit(Result.Success(response.data.datas))
        } else {
            emit(Result.apiError(response))
        }
    } catch (e: Exception) {
        emit(Result.fromException(e, "获取文章失败"))
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

## 🧪 测试

```bash
# 运行单元测试
./gradlew test

# 运行仪器化测试
./gradlew connectedAndroidTest
```

## 📄 开源协议

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

## 🤝 贡献

欢迎贡献！请随时提交 Pull Request。

1. Fork 本仓库
2. 创建您的特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交您的更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开一个 Pull Request

## 📮 联系方式

- **作者**: 孙宇峰
- **邮箱**: your.email@example.com
- **GitHub**: [@jasonsyf](https://github.com/jasonsyf)

## 🙏 致谢

- [WanAndroid API](https://www.wanandroid.com/blog/show/2) - 免费开放的 API
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - 现代化 Android UI 工具包
- [Material Design 3](https://m3.material.io/) - 设计系统

---

<div align="center">
用 ❤️ 和 Jetpack Compose 制作
</div>
