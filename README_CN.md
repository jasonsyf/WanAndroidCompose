# 玩 Android Compose - 简明指南

## 📱 项目简介

这是一个使用 **Jetpack Compose** 和 **MVI 架构**构建的现代化 Android 应用，展示了最新的 Android 开发最佳实践。

## ✨ 核心特性

### 🏗️ MVI 架构
- **单向数据流**：Action → State → UI
- **状态管理清晰**：所有 UI 状态集中管理
- **易于测试**：逻辑与 UI 分离

### 🎨 纯 Compose UI
- 100% 声明式 UI，无 XML
- Material Design 3
- 流畅动画效果

### 🚀 优化的 BaseViewModel
```kotlin
class HomeViewModel : BaseViewModelOptimized<HomeAction, HomeState>() {
    // ✅ Action 缓冲区 64
    // ✅ 自动异常处理
    // ✅ 支持取消机制
    // ✅ 自动日志记录
}
```

### 🌐 强大的网络层
```kotlin
// 简单易用
apiRequest { apiService.getArticleList(0) }
    .collect { result ->
        result
            .onLoading { /* 加载中 */ }
            .onSuccess { data -> /* 成功 */ }
            .onError { _, msg, _ -> /* 失败 */ }
    }
```

## 🛠 技术栈

| 技术 | 版本 |
|------|------|
| Kotlin | 2.2.21 |
| Jetpack Compose | 2024.09.00 |
| Retrofit | 3.0.0 |
| OkHttp | 5.3.2 |
| Navigation | 2.9.6 |

## 🚀 快速开始

```bash
# 克隆项目
git clone https://github.com/yourusername/WanAndroidCompose.git

# 打开 Android Studio 并运行
./gradlew assembleDebug
```

## 📂 核心目录

```
ui/
├── common/                    # 基础组件
│   ├── BaseViewModelOptimized.kt     ⭐ 优化的 ViewModel
│   └── BaseStateFlowViewModel.kt     StateFlow 版本
│
├── network/                   # 网络层 ⭐
│   ├── ApiResult.kt          结果封装
│   ├── RetrofitClient.kt     Retrofit 配置
│   ├── ApiExtensions.kt      扩展函数
│   └── WanAndroidApiService.kt  API 定义
│
├── intentAndState/            # MVI 定义
│   └── HomeAction.kt         Action/State
│
└── home/                      # 功能模块
    └── HomeViewModel.kt
```

## 💡 代码示例

### 定义 Action 和 State

```kotlin
sealed class HomeAction : Action {
    object LoadData : HomeAction()
    object Refresh : HomeAction()
}

data class HomeState(
    val isLoading: Boolean = false,
    val articles: List<Article> = emptyList(),
    val error: String? = null
) : State
```

### 创建 ViewModel

```kotlin
class HomeViewModel : BaseViewModelOptimized<HomeAction, HomeState>() {
    
    override fun onAction(action: HomeAction, currentState: HomeState?) {
        when (action) {
            is HomeAction.LoadData -> loadData()
            is HomeAction.Refresh -> refresh()
        }
    }
    
    private fun loadData() {
        launchAction(key = "load") {  // 支持取消
            apiRequest { apiService.getArticles() }
                .collect { result ->
                    result.onSuccess { data ->
                        emitState(HomeState(articles = data.datas))
                    }
                }
        }
    }
}
```

### Compose UI

```kotlin
@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val state by viewModel.state.collectAsState(initial = HomeState())
    
    LaunchedEffect(Unit) {
        viewModel.sendAction(HomeAction.LoadData)
    }
    
    when {
        state.isLoading -> LoadingView()
        state.error != null -> ErrorView(state.error!!)
        else -> ArticleList(state.articles)
    }
}
```

## 🎯 架构图

```
┌──────────────┐
│  Compose UI  │  ← 观察 State
└──────┬───────┘
       │ 发送 Action
       ▼
┌──────────────┐
│  ViewModel   │  ← 处理逻辑
│  (MVI)       │
└──────┬───────┘
       │ 网络请求
       ▼
┌──────────────┐
│ Network API  │  ← Retrofit
└──────────────┘
```

## 📚 详细文档

- 📖 [完整 README](README.md)
- 📖 [网络层文档](app/src/main/java/com/syf/wanandroidcompose/ui/network/README.md)
- 💻 [使用示例](app/src/main/java/com/syf/wanandroidcompose/ui/network/example/)

## 🔥 核心优势

### BaseViewModel 优化

| 特性 | 旧版 | 优化版 |
|-----|------|--------|
| Action 缓冲 | ❌ 0 | ✅ 64 |
| 异常处理 | ❌ 会中断 | ✅ 自动捕获 |
| 取消机制 | ❌ 无 | ✅ 按 key 取消 |
| 日志支持 | ❌ 无 | ✅ 自动记录 |

### 网络层特性

- ✅ 自动 Loading/Success/Error 状态
- ✅ 统一异常处理
- ✅ 支持并发请求
- ✅ 类型安全（Kotlinx Serialization）
- ✅ Chucker 网络调试

## 📝 开发计划

- [x] MVI 架构搭建
- [x] 网络层封装
- [x] BaseViewModel 优化
- [x] 首页界面
- [ ] 用户登录
- [ ] 文章收藏
- [ ] 搜索功能
- [ ] 完整的单元测试

## ⭐ Star History

如果这个项目对你有帮助，请给一个 Star！

---

**Made with ❤️ and Jetpack Compose**
