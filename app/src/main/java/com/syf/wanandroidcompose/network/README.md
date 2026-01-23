# Network 网络层封装

基于 Retrofit 的网络请求封装，专为 MVI 模式和 Compose UI 设计。

## 📦 核心组件

### 1. ApiResult - 请求结果封装
统一的网络请求结果类型，支持三种状态：
- `Success<T>` - 成功状态，包含数据
- `Error` - 失败状态，包含异常信息
- `Loading` - 加载中状态

### 2. ApiResponse - API 响应模型
适配玩 Android API 的标准响应格式：
```kotlin
{
  "data": T,
  "errorCode": 0,
  "errorMsg": ""
}
```

### 3. RetrofitClient - Retrofit 配置
单例模式的 Retrofit 客户端，提供：
- OkHttp 配置（超时、重试、日志）
- Kotlinx Serialization 支持
- Debug 模式下的日志拦截

### 4. NetworkException - 异常处理
统一的异常处理工具，将各种网络异常转换为友好的错误消息。

### 5. ApiExtensions - 扩展函数
简化网络请求的调用，提供多种便捷方法。

## 🚀 使用方法

### 方式一：在 ViewModel 中使用 Flow（推荐）

```kotlin
class HomeViewModel : BaseViewModel<HomeAction, HomeState>() {
    
    private val apiService = RetrofitClient.create<WanAndroidApiService>()
    
    private fun loadArticles() {
        viewModelScope.launch {
            // 自动处理 Loading 和 Error 状态
            apiRequest { apiService.getArticleList(0) }
                .collect { result ->
                    when (result) {
                        is ApiResult.Loading -> {
                            // 显示加载状态
                            emitState(HomeState(isLoading = true))
                        }
                        is ApiResult.Success -> {
                            // 处理成功数据
                            emitState(HomeState(
                                isLoading = false,
                                articles = result.data.datas
                            ))
                        }
                        is ApiResult.Error -> {
                            // 处理错误
                            emitState(HomeState(
                                isLoading = false,
                                error = result.message
                            ))
                        }
                    }
                }
        }
    }
}
```

### 方式二：使用 suspend 函数

```kotlin
private fun loadData() {
    viewModelScope.launch {
        val result = safeApiCall { apiService.getArticleList(0) }
        
        result.onSuccess { data ->
            // 处理成功
            emitState(HomeState(articles = data.datas))
        }.onError { error, message, code ->
            // 处理失败
            emitState(HomeState(error = message))
        }
    }
}
```

### 方式三：链式调用

```kotlin
private fun loadBanner() {
    viewModelScope.launch {
        apiRequest { apiService.getBanner() }
            .collect { result ->
                result
                    .onLoading { 
                        emitState(currentState?.copy(isLoading = true))
                    }
                    .onSuccess { banners ->
                        emitState(currentState?.copy(
                            isLoading = false,
                            banners = banners
                        ))
                    }
                    .onError { _, message, _ ->
                        emitState(currentState?.copy(
                            isLoading = false,
                            error = message
                        ))
                    }
            }
    }
}
```

## 🎨 在 Compose UI 中使用

```kotlin
@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val state by viewModel.state.collectAsState(initial = HomeState())
    
    when {
        state.isLoading -> {
            LoadingIndicator()
        }
        state.error != null -> {
            ErrorMessage(state.error!!)
        }
        else -> {
            ArticleList(state.articles)
        }
    }
}
```

## 📝 添加新的 API

1. 在 `WanAndroidApiService.kt` 中定义接口：
```kotlin
@GET("article/list/{page}/json")
suspend fun getArticleList(@Path("page") page: Int): ApiResponse<ArticleListData>
```

2. 定义数据模型（使用 `@Serializable` 注解）：
```kotlin
@Serializable
data class ArticleData(
    val id: Int,
    val title: String,
    val link: String
)
```

3. 在 ViewModel 中调用：
```kotlin
private val apiService = RetrofitClient.create<WanAndroidApiService>()

fun loadData() {
    viewModelScope.launch {
        apiRequest { apiService.getArticleList(0) }
            .collect { result ->
                // 处理结果
            }
    }
}
```

## ⚙️ 高级配置

### 添加 Token 拦截器
在 `RetrofitClient.kt` 中添加：
```kotlin
class TokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        return chain.proceed(request)
    }
}

// 在 OkHttpClient 构建时添加
addInterceptor(TokenInterceptor())
```

### 自定义错误处理
扩展 `NetworkException.kt`：
```kotlin
fun Throwable.toCustomMessage(): String {
    return when (this) {
        is YourCustomException -> "自定义错误消息"
        else -> toErrorMessage()
    }
}
```

## 🔍 调试技巧

1. **查看网络日志**：在 Debug 模式下，所有网络请求都会打印到 Logcat
2. **使用 Chucker**：项目已集成 Chucker，可在通知栏查看网络请求详情
3. **异常追踪**：使用 Timber 记录所有网络异常

## 📌 最佳实践

1. ✅ 使用 `apiRequest` 自动处理 Loading 状态
2. ✅ 在 ViewModel 中集中管理网络请求
3. ✅ 使用链式调用简化代码
4. ✅ 为所有数据模型添加默认值
5. ✅ 使用 `@Serializable` 注解而非 Gson
6. ⚠️ 避免在 Composable 中直接调用网络请求
7. ⚠️ 注意处理配置变更时的请求取消

## 🔗 相关文档

- [Retrofit](https://square.github.io/retrofit/)
- [OkHttp](https://square.github.io/okhttp/)
- [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization)
- [玩 Android API](https://www.wanandroid.com/blog/show/2)
