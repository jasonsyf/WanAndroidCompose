# 玩 Android Compose

<div align="center">

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)

**一个基于 Jetpack Compose 和 MVI 架构的现代化 Android 应用**

[功能特性](#-功能特性) • [技术栈](#-技术栈) • [架构设计](#-架构设计) • [快速开始](#-快速开始) • [项目结构](#-项目结构)

</div>

---

## 📱 应用简介

玩 Android Compose 是一款采用 **Jetpack Compose** 构建的玩 Android 客户端，完全使用 **MVI (Model-View-Intent)** 架构模式开发，展示了现代 Android 开发的最佳实践。

### ✨ 亮点

- 🎨 **纯 Compose UI** - 100% 声明式 UI，无 XML
- 🏗️ **MVI 架构** - 单向数据流，状态管理清晰
- 🚀 **优化的网络层** - 基于 Retrofit + Kotlinx Serialization
- 🔧 **强大的 BaseViewModel** - 支持异常处理、Action 取消、自动日志
- 📦 **模块化设计** - 清晰的分层架构
- 🎯 **类型安全** - 完全使用 Kotlin 协程和 Flow

---

## 🎯 功能特性

### 已实现功能

- ✅ **首页**
  - 轮播图展示
  - 文章列表（支持分页）
  - 置顶文章
  - 优质公众号展示
  - Tab 切换

- ✅ **导航系统**
  - 底部导航栏（首页、项目、体系、我的）
  - 页面切换动画
  - 状态保存

- ✅ **网络请求**
  - 基于 Retrofit 的网络封装
  - 统一的错误处理
  - Loading/Success/Error 状态管理
  - 支持并发和取消

### 规划中功能

- 🔲 用户登录/注册
- 🔲 文章收藏
- 🔲 搜索功能
- 🔲 项目列表
- 🔲 知识体系
- 🔲 个人中心

---

## 🛠 技术栈

### 核心框架

| 技术 | 版本 | 说明 |
|------|------|------|
| Kotlin | 2.2.21 | 主要开发语言 |
| Jetpack Compose | 2024.09.00 | 声明式 UI 框架 |
| Android Gradle Plugin | 8.13.2 | 构建工具 |
| Minimum SDK | 24 | 最低支持 Android 7.0 |
| Target SDK | 36 | 目标 Android 版本 |

### Jetpack 组件

- **Compose UI** - 声明式 UI
- **Navigation Compose** (2.9.6) - 导航管理
- **Lifecycle** (2.10.0) - 生命周期管理
- **ViewModel** - 状态管理
- **Coroutines & Flow** - 异步编程

### 网络请求

- **Retrofit** (3.0.0) - HTTP 客户端
- **OkHttp** (5.3.2) - 网络请求库
- **Kotlinx Serialization** (1.6.3) - JSON 序列化
- **Chucker** (4.2.0) - 网络调试工具

### 其他库

- **Coil Compose** (2.7.0) - 图片加载
- **Timber** (5.0.1) - 日志框架
- **Material3** - Material Design 3

---

## 🏗 架构设计

### MVI 架构模式

```
┌─────────────────────────────────────────┐
│              Compose UI                 │
│  (HomeView, AppMainView, etc.)          │
└──────────────┬──────────────────────────┘
               │ User Actions
               ▼
┌─────────────────────────────────────────┐
│           ViewModel                     │
│  (BaseViewModelOptimized)               │
│                                         │
│  ┌──────────┐    ┌──────────┐          │
│  │  Action  │───▶│   State  │          │
│  └──────────┘    └──────────┘          │
│       │               │                 │
│       ▼               │                 │
│  ┌──────────┐         │                 │
│  │  Effect  │         │                 │
│  └──────────┘         │                 │
└───────────┬───────────┴─────────────────┘
            │
            ▼
┌─────────────────────────────────────────┐
│         Network Layer                   │
│  (RetrofitClient, ApiService)           │
└─────────────────────────────────────────┘
```

### 核心概念

#### **Action** - 用户意图
```kotlin
sealed class HomeAction : Action {
    object LoadArticleData : HomeAction()
    object RefreshAllData : HomeAction()
    data class ClickArticle(val articleId: String) : HomeAction()
}
```

#### **State** - UI 状态
```kotlin
data class HomeListState(
    val isLoading: Boolean = false,
    val articles: List<ArticleData> = emptyList(),
    val error: String? = null
) : State
```

#### **Effect** - 一次性事件
```kotlin
sealed class HomeEffect : Effect {
    data class ShowToast(val message: String) : HomeEffect()
    data class NavigateToDetail(val url: String) : HomeEffect()
}
```

### BaseViewModel 优化特性

我们的 `BaseViewModelOptimized` 提供了强大的功能：

- ✅ **Action 缓冲区** - 64 容量，防止快速点击丢失
- ✅ **异常自动捕获** - 不会中断 Flow
- ✅ **取消机制** - 支持按 key 取消 Action
- ✅ **自动日志** - Debug 模式下记录所有 Action
- ✅ **线程控制** - 可配置调度器
- ✅ **资源清理** - 自动清理协程

**示例代码：**

```kotlin
class HomeViewModel : BaseViewModelOptimized<HomeAction, HomeState>() {
    
    override fun onAction(action: HomeAction, currentState: HomeState?) {
        when (action) {
            is HomeAction.Search -> search(action.query)
        }
    }
    
    // 防抖搜索 - 自动取消旧请求
    private fun search(query: String) {
        launchAction(key = "search") {
            delay(300)  // 防抖
            // 搜索逻辑
        }
    }
}
```

---

## 🌐 网络层封装

### 核心组件

#### **ApiResult** - 请求结果封装
```kotlin
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val exception: Throwable, val message: String) : ApiResult<Nothing>()
    data object Loading : ApiResult<Nothing>()
}
```

#### **ApiResponse** - API 响应模型
```kotlin
@Serializable
data class ApiResponse<T>(
    val data: T? = null,
    val errorCode: Int = 0,
    val errorMsg: String = ""
)
```

### 使用示例

```kotlin
// 在 ViewModel 中
private fun loadArticles() {
    viewModelScope.launch {
        apiRequest { apiService.getArticleList(0) }
            .collect { result ->
                result
                    .onLoading { emitState(currentState?.copy(isLoading = true)) }
                    .onSuccess { data -> emitState(currentState?.copy(articles = data.datas)) }
                    .onError { _, msg, _ -> emitState(currentState?.copy(error = msg)) }
            }
    }
}

// 在 Compose 中
@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val state by viewModel.state.collectAsState(initial = HomeState())
    
    when {
        state.isLoading -> LoadingView()
        state.error != null -> ErrorView(state.error!!)
        else -> ArticleList(state.articles)
    }
}
```

---

## 🚀 快速开始

### 环境要求

- **Android Studio** Hedgehog (2023.1.1) 或更高版本
- **JDK** 11 或更高版本
- **Minimum SDK** 24 (Android 7.0)

### 克隆项目

```bash
git clone https://github.com/yourusername/WanAndroidCompose.git
cd WanAndroidCompose
```

### 构建运行

```bash
# 构建 Debug 版本
./gradlew assembleDebug

# 安装到设备
./gradlew installDebug

# 或者直接在 Android Studio 中运行
```

### 配置说明

项目使用 **Kotlin Version Catalog**，所有依赖在 `gradle/libs.versions.toml` 中统一管理。

---

## 📁 项目结构

```
WanAndroidCompose/
├── app/
│   └── src/
│       └── main/
│           └── java/com/syf/wanandroidcompose/
│               ├── MainActivity.kt                    # 主 Activity
│               └── ui/
│                   ├── AppMainView.kt                # 应用主界面
│                   ├── HomeView.kt                   # 首页
│                   │
│                   ├── common/                       # 通用组件
│                   │   ├── BaseViewModel.kt          # 基础 ViewModel
│                   │   ├── BaseViewModelOptimized.kt # 优化版 ViewModel ⭐
│                   │   ├── BaseViewModelWithEffectOptimized.kt
│                   │   └── BaseStateFlowViewModel.kt # StateFlow 版本
│                   │
│                   ├── network/                      # 网络层 ⭐
│                   │   ├── ApiResult.kt              # 结果封装
│                   │   ├── ApiResponse.kt            # 响应模型
│                   │   ├── ApiExtensions.kt          # 扩展函数
│                   │   ├── NetworkException.kt       # 异常处理
│                   │   ├── RetrofitClient.kt         # Retrofit 配置
│                   │   ├── WanAndroidApiService.kt   # API 接口
│                   │   ├── README.md                 # 网络层文档
│                   │   └── example/                  # 使用示例
│                   │       ├── ExampleViewModel.kt
│                   │       └── ExampleScreen.kt
│                   │
│                   ├── intentAndState/               # MVI 模式
│                   │   ├── HomeAction.kt             # Action 定义
│                   │   └── ApiService.kt
│                   │
│                   ├── home/                         # 首页模块
│                   │   └── HomeViewModel.kt
│                   │
│                   └── theme/                        # 主题配置
│                       ├── Color.kt
│                       ├── Theme.kt
│                       └── Type.kt
│
└── gradle/
    └── libs.versions.toml                           # 依赖版本管理
```

### 关键目录说明

- **`common/`** - 通用基类和工具
  - `BaseViewModelOptimized` - 优化的 ViewModel 基类
  - `BaseStateFlowViewModel` - StateFlow 版本

- **`network/`** - 网络层封装
  - 完整的 Retrofit 封装
  - 统一的错误处理
  - 支持 MVI 架构

- **`intentAndState/`** - MVI 模式定义
  - Action - 用户意图
  - State - UI 状态
  - Effect - 一次性事件

---

## 💡 核心特性详解

### 1. 优化的 BaseViewModel

**特性：**
- ✅ 64 缓冲区的 Action Channel
- ✅ 自动异常捕获和处理
- ✅ 支持 Action 取消（防抖搜索）
- ✅ 自动日志记录
- ✅ 可配置的调度器

**示例：**
```kotlin
class SearchViewModel : BaseViewModelOptimized<SearchAction, SearchState>() {
    
    private fun search(query: String) {
        launchAction(key = "search") {  // 自动取消旧的搜索
            delay(300)                  // 防抖
            apiRequest { apiService.search(query) }
                .collect { result ->
                    // 处理结果
                }
        }
    }
}
```

### 2. 强大的网络层

**特性：**
- ✅ 基于 Retrofit 3.0 + OkHttp 5.3
- ✅ Kotlinx Serialization（非 Gson）
- ✅ 自动 Loading/Success/Error 状态
- ✅ 统一异常处理
- ✅ 支持并发请求
- ✅ Chucker 网络调试

**示例：**
```kotlin
// 定义 API
interface WanAndroidApiService {
    @GET("article/list/{page}/json")
    suspend fun getArticleList(@Path("page") page: Int): ApiResponse<ArticleListData>
}

// 使用
apiRequest { apiService.getArticleList(0) }
    .collect { result ->
        result.onSuccess { data -> /* 处理成功 */ }
    }
```

### 3. 纯 Compose UI

**特性：**
- ✅ 100% 声明式 UI
- ✅ Material Design 3
- ✅ 自定义主题（渐变 TopAppBar）
- ✅ 流畅的动画效果
- ✅ 响应式布局

**示例：**
```kotlin
@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val state by viewModel.state.collectAsState(initial = HomeState())
    
    LazyColumn {
        item { Banner(state.banners) }
        items(state.articles) { article ->
            ArticleCard(article)
        }
    }
}
```

---

## 📚 文档

### 核心文档

- 📖 [网络层使用指南](app/src/main/java/com/syf/wanandroidcompose/ui/network/README.md)
- 📖 [BaseViewModel 优化方案](.gemini/antigravity/brain/.../baseviewmodel_optimization_plan.md)
- 📖 [迁移指南](.gemini/antigravity/brain/.../baseviewmodel_migration_guide.md)

### 示例代码

- 💻 [网络层示例](app/src/main/java/com/syf/wanandroidcompose/ui/network/example/)
- 💻 [ViewModel 示例](app/src/main/java/com/syf/wanandroidcompose/ui/common/example/)

---

## 🔧 开发指南

### 添加新功能

#### 1. 定义 Action 和 State

```kotlin
// intentAndState/FeatureAction.kt
sealed class FeatureAction : Action {
    object LoadData : FeatureAction()
}

data class FeatureState(
    val isLoading: Boolean = false,
    val data: List<Item> = emptyList()
) : State
```

#### 2. 创建 ViewModel

```kotlin
// feature/FeatureViewModel.kt
class FeatureViewModel : BaseViewModelOptimized<FeatureAction, FeatureState>() {
    
    override fun onAction(action: FeatureAction, currentState: FeatureState?) {
        when (action) {
            is FeatureAction.LoadData -> loadData()
        }
    }
    
    private fun loadData() {
        launchAction {
            apiRequest { apiService.getData() }
                .collect { result ->
                    // 更新状态
                }
        }
    }
}
```

#### 3. 创建 Compose UI

```kotlin
// feature/FeatureScreen.kt
@Composable
fun FeatureScreen(viewModel: FeatureViewModel = viewModel()) {
    val state by viewModel.state.collectAsState(initial = FeatureState())
    
    // UI 实现
}
```

### 代码规范

- ✅ 使用 Kotlin 官方代码风格
- ✅ ViewModel 中不持有 Context
- ✅ Composable 函数首字母大写
- ✅ 使用 `remember` 和 `LaunchedEffect` 管理状态
- ✅ 网络请求统一使用 `apiRequest`

---

## 🧪 测试

```bash
# 运行单元测试
./gradlew test

# 运行 UI 测试
./gradlew connectedAndroidTest
```

---

## 📈 性能优化

- ✅ **LazyColumn** - 列表懒加载
- ✅ **remember** - 避免重组时重新计算
- ✅ **derivedStateOf** - 衍生状态
- ✅ **distinctUntilChanged** - 去重状态流
- ✅ **Action 缓冲** - 防止事件丢失

---

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

### 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

---

## 📄 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

---

## 🙏 致谢

- [玩 Android](https://www.wanandroid.com/) - 提供 API
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - 声明式 UI 框架
- [Square](https://square.github.io/) - Retrofit & OkHttp
- Android 开源社区

---

## 📞 联系方式

- 作者：sunyufeng
- 项目地址：[WanAndroidCompose](https://github.com/yourusername/WanAndroidCompose)

---

<div align="center">

**⭐ 如果这个项目对你有帮助，请给一个 Star！⭐**

Made with ❤️ and Jetpack Compose

</div>
