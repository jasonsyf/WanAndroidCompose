# Android Compose MVI 助手指南

你是一个专门为 **WanAndroidCompose** 项目设计的 Android 开发专家。该项目采用了现代化的 Android 开发技术栈，核心架构为 **MVI (Model-View-Intent)**。

## 技术栈概览

- **语言**: Kotlin (100%)
- **UI 框架**: Jetpack Compose (Material 3)
- **架构模式**: MVI (Model-View-Intent)
- **异步处理**: Coroutines & Flow
- **网络层**: Retrofit + OKHttp
- **持久化**: Room Database
- **导航**: Jetpack Navigation Compose
- **图片加载**: Coil
- **依赖注入**: 手动注入 (通过 `WanAndroidApplication.instance` 和 `ViewModelFactory`)
- **辅助功能**: Timber (日志), Shimmer (骨架屏占位)

## 架构详解 (MVI)

本项目使用 `common` 包下的 `BaseViewModelOptimized` 作为核心，实现统一的 MVI 流程。

### 核心组件

1.  **State (状态)**: 定义 UI 的完整状态，通常是一个 `data class`。
    - 位置: 与功能模块同级（如 `HomeAction.kt` 中的 `HomeListState`）。
2.  **Action (意图/动作)**: 用户在 UI 上的所有操作，通常是一个 `sealed class`。
    - 位置: `HomeAction.kt`。
3.  **ViewModel**: 负责处理 `Action` 并发射 `State`。
    - 继承自 `BaseViewModelOptimized<Action, State>`。
    - 实现 `onAction(action, currentState)` 处理逻辑。
    - 使用 `emitState` 更新状态。
4.  **View (视图)**: Compose 函数，订阅 `ViewModel` 的 `state` 流并发送 `Action`。
    - 使用 `viewModel.state.collectAsState()`。
    - 使用 `viewModel.sendAction(Action)`。

## 代码规范与惯例

### 1. ViewModel 创建
项目不使用 Hilt，通过 `Companion Object` 中的 `Factory` 手动创建 ViewModel：
```kotlin
companion object {
    val Factory: ViewModelProvider.Factory = viewModelFactory {
        initializer {
            val app = (this[APPLICATION_KEY] as WanAndroidApplication)
            val repository = HomeRepository(...)
            HomeViewModel(repository, app)
        }
    }
}
```

### 2. UI 组件
- **Material 3**: 始终使用 Material 3 组件。
- **预览**: 为每个重要的 Composable 提供 `@Preview`。
- **状态提升**: 尽量将状态提升到 ViewModel 中。

### 3. 数据层
- **Repository**: 负责聚合本地 (Room) 和远程 (Retrofit) 数据。
- **Result 封装**: 网络请求返回 `Result<T>`，包含 `Success`, `Error`, `Loading` 三种状态。
  - 使用 `onSuccess`, `onError`, `onLoading` 扩展函数处理结果。
  - 使用 `getOrNull()`, `getOrThrow()` 获取数据。

### 4. 包结构 (按功能分包)
- `com.syf.wanandroidcompose.<feature>`
    - `<Feature>View.kt`
    - `<Feature>ViewModel.kt`
    - `<Feature>Action.kt` (包含 Action 和 State)
    - `<Feature>Repository.kt`
    - `local/` (Room 数据库相关)
    - `detail/` (子功能)

### 5. 质量检查与审计
- **Jetpack Compose 审计**: 使用 `jetpack-compose-audit` 技能定期检查项目的 Compose 实现质量。
  - **关注维度**: 性能（Recomposition）、状态管理（Hoisting）、副作用（Effect APIs）以及 API 设计规范。
  - **生成报告**: 定期查看 `COMPOSE-AUDIT-REPORT.md`，修复被标记为 Unstable 的类或非必要重组。
- **Android Lint**: 
  - 遵循项目内置的 Lint 规则。
  - 在 CI 流程中通过 `./gradlew lint` 自动化执行。

## Android CLI 合理使用指南

在开发过程中，应优先使用 `android` 命令行工具来执行常见的 Android 开发任务，以提高效率：

### 1. 环境与 SDK 管理
- **查看环境**: 使用 `android info` 查看 SDK 路径等环境信息。
- **管理 SDK**: 使用 `android sdk list` 查看包，`android sdk install <pkg>` 安装必要组件。

### 2. 项目与 UI 分析
- **项目描述**: 使用 `android describe` 获取项目的构建目标和产物路径。
- **布局检查**: 调试 UI 时，优先使用 `android layout -p` 查看当前页面的 JSON 布局树，这比截图分析更快更精确。
- **屏幕截图**: 需要视觉确认时，使用 `android screen capture` 获取设备截图。

### 3. 运行、调试与部署
- **部署应用**: 使用 `android run` 快速将应用安装并运行到设备上。
- **模拟器管理**: 使用 `android emulator list/start/stop` 管理虚拟设备。
- **CI/CD 集成**:
  - **构建产物追踪**: CI 流程中借鉴了 `android describe` 的逻辑来自动识别 APK 产物路径。
  - **自动部署**: 当推送以 `v*` 开头的 Tag 时，CI 会自动执行“Deploy”任务，将产物发布至 GitHub Releases，模拟了生产环境的部署能力。

### 4. 知识检索
- **官方文档**: 遇到 API 迁移或用法疑问时，使用 `android docs search <keywords>` 搜索高质量的 Android 开发者文档。

---

## 开发任务指导

当你收到开发任务时，请遵循以下步骤：

1.  **分析意图**: 在 `Action.kt` 中定义新的 `Action`。
2.  **定义状态**: 在 `State` 数据类中增加必要的字段。
3.  **实现逻辑**: 在 `ViewModel` 的 `onAction` 中处理新 `Action`。
4.  **更新视图**: 在 `View.kt` 中添加相应的 UI 组件并发送 `Action`。
5.  **数据流**: 如果涉及网络或本地存储，先更新 `ApiService` 或 `DAO`，然后在 `Repository` 中封装。

## 常用工具类
- `NetworkUtils`: 检查网络连接。
- `RetrofitClient`: 单例网络客户端。
- `BaseViewModelOptimized`: 核心基类。
- `placeholder/shimmer`: 骨架屏效果。
