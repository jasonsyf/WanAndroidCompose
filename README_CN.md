# WanAndroid Compose

<div align="center">

![应用图标](/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png)

**基于 Jetpack Compose 构建的现代化 Android 学习平台**

[![Kotlin](https://img.shields.io/badge/Kotlin-2.1%2B-purple.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Compose-最新版-brightgreen.svg)](https://developer.android.com/jetpack/compose)
[![AGP](https://img.shields.io/badge/AGP-9.1.1-blue.svg)](https://developer.android.com/studio/releases/gradle-plugin)
[![Gradle](https://img.shields.io/badge/Gradle-9.4.1-blue.svg)](https://gradle.org/releases/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

[English](README.md)

</div>

## ✨ 特性

- 🎨 **现代化 UI** - 完全使用 Jetpack Compose 和 Material Design 3 构建。
- 🏗️ **MVI 架构** - 单向数据流，通过 `BaseViewModelOptimized` 实现强大的状态管理（StateFlow + Channel）。
- 💾 **离线优先** - Room 数据库配合 Paging 3 支持，实现无缝本地缓存。
- 🔄 **响应式编程** - 广泛使用 Kotlin Flow 和协程处理异步逻辑。
- 🌐 **网络层** - Retrofit + OkHttp 自定义拦截器，集成 `Result<T>` 封装。
- 🌈 **动态主题** - 支持多种主题模式（亮色/暗色/系统）、对比度等级和自定义字体样式。
- 🌍 **多语言支持** - 内置中英文切换。
- 📱 **自适应布局** - 使用 Material 3 Adaptive Navigation Suite 适配不同屏幕尺寸。

## 🛡️ 开发工作流与质量保障

项目拥有一套严苛的自动化质量控制体系，由 **Git Hooks** 和 **GitHub Actions** 驱动：

### ⚓ Git Hooks (本地质量门禁)
- **Pre-commit (增量化)**: 仅对您提交的（Staged）`.kt` 文件运行 `ktlint` 检查。修改单文件的反馈时间从 **20s+ 降低至 <2s**。同时拦截超大图片资源 (>200KB)，扫描并预警调试日志。
- **Commit-msg**: 强制执行 **Conventional Commits** 规范，并要求包含中文描述。
- **Pre-push (变动感知)**: 推送前智能识别变动范围。仅在涉及代码、资源或 UI 修改时才触发对应的单元测试、Lint 或 Paparazzi 截屏验证，显著减少无效等待。

### 🤖 CI/CD 流水线 (GitHub Actions)
- **智能增量触发**: 修改文档（`.md`）、脚本或配置时自动跳过重度编译。
- **精细化调度**: 利用 `changed-files` 插件，根据变动内容（逻辑代码、资源或测试）精准运行必要的验证任务，极速反馈并节省 CI 资源。
- **包体积监控**: 自动对比当前分支与主分支的 APK 大小，并在 PR 下方自动发表对比报告。
- **截屏测试**: 使用 **Paparazzi** 进行像素级 UI 视觉回归检测。
- **自动发布**: 推送以 `v*` 开头的 Tag 时，自动触发签名编译并发布 GitHub Release。

## 🧪 测试体系

项目建立了完善的自动化测试流程，确保业务与 UI 的双重质量：
- **单元测试**: 验证 ViewModel 业务逻辑与状态流转 (JUnit 4 + MockK + Turbine)。
- **截屏测试**: 像素级 UI 视觉回归校验 (Paparazzi)。
- **UI 测试**: 验证用户交互触发与页面渲染 (Compose Test)。

更多细节请参阅 [TESTING-GUIDE.md](TESTING-GUIDE.md)。

## 🛠️ 技术栈

### 核心
- **Jetpack Compose** - 现代化声明式 UI 框架。
- **Navigation Compose** - 类型安全导航。
- **Hilt** - 依赖注入（部分模块仍保留手动注入以示演示）。
- **Room** - 本地持久化，支持 Flow 和 Paging 3。

### 网络与数据
- **Retrofit & OkHttp** - 网络请求。
- **Kotlin Serialization** - 现代化 JSON 解析。
- **Coil** - Compose 图片加载。

### 质量与性能
- **Gradle 9.4.1** - 最新版构建系统，极致构建性能。
- **AGP 9.1.1** - 支持最新的 Android 平台特性。
- **配置缓存 (Configuration Cache)** - 已开启，增量构建秒开。
- **Strong Skipping Mode** - 极致优化的 Compose 重组性能。

## 📦 项目结构

```text
WanAndroidCompose/
├── app/                  # Android 应用核心模块
├── scripts/              # 本地质量自动化脚本 (Git Hooks)
├── design-system/        # UI/UX 设计规范
├── .github/workflows/    # CI/CD 流水线配置
└── gradle/libs.versions.toml # 统一版本管理
```

## 🚀 开始使用

### 环境要求
- **Android Studio Meerkat | 2024.3.1** 或更高版本
- JDK 17 或更高版本
- Android SDK API 24+
- Gradle 9.4+

### 构建
1. 克隆仓库。
2. 在 Android Studio 中打开。
3. 同步 Gradle。**Git Hooks** 将在首次构建时自动安装。
4. 运行应用。

## 📄 开源协议
Copyright 2024 Sun Yufeng. 基于 Apache License, Version 2.0 授权。

---
<div align="center">
用 ❤️ 和 Jetpack Compose 制作
</div>
