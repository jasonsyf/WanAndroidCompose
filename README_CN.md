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

- 🎨 **现代化 UI** - 完全使用 Jetpack Compose 和 Material Design 3 构建。
- 🏗️ **MVI 架构** - 单向数据流，通过 `BaseViewModelOptimized` 实现强大的状态管理（StateFlow + Channel）。
- 💾 **离线优先** - Room 数据库配合 Paging 3 支持，实现无缝本地缓存。
- 🔄 **响应式编程** - 广泛使用 Kotlin Flow 和协程处理异步逻辑。
- 🌐 **网络层** - Retrofit + OkHttp 自定义拦截器，集成 `Result<T>` 封装。
- 🌈 **动态主题** - 支持多种主题模式（亮色/暗色/系统）、对比度等级和自定义字体样式。
- 🌍 **多语言支持** - 内置中英文切换。
- 🚀 **自动化工作流**:
    - **CI (Build & Test)**: 每次推送均会自动进行 Lint 检查与编译验证。
    - **CD (Auto Release)**: 通过 Git Tag (`v*`) 触发自动签名与 GitHub Release 发布。
- 🤖 **AI 协作**: 专为 Gemini CLI 优化，内置 `AGENTS.md` 智能助手指南。

## 📂 功能模块

- 🏠 **首页** - 文章列表、轮播图及每日更新。
- 📂 **项目** - 玩 Android 社区分类项目展示。
- 🌿 **体系** - 知识体系与权威导航。
- 👤 **我的** - 用户管理、设置与个性化配置。
- 🔑 **登录/注册** - 完整的身份验证流程。

## 🛠️ 技术栈

### 核心
- **Jetpack Compose** - 现代化声明式 UI 框架。
- **Navigation Compose** - 类型安全导航。
- **Manual DI** - 使用 `WanAndroidApplication` 结合 `ViewModelProvider.Factory` 实现简洁的依赖注入，无 Hilt/Koin 负担。
- **Room** - 本地持久化，支持 Flow 和 Paging 3。

### 网络与数据
- **Retrofit & OkHttp** - 网络请求。
- **Kotlin Serialization** - 现代化 JSON 解析。
- **Coil** - Compose 图片加载。

### 质量与性能
- **KSP** - 快速注解处理。
- **Timber** - 结构化日志。
- **GitHub Actions** - 自动化编译与签名发布。
- **生命周期感知状态** - 使用 `collectAsStateWithLifecycle` 提升资源效率。

## 📦 项目结构

```text
WanAndroidCompose/
├── app/                  # Android 应用核心模块
│   └── src/main/java/    # 采用功能分包 (Feature-based packaging)
├── design-system/        # UI/UX 设计规范 (Single Source of Truth)
├── docs/                 # 项目相关文档与素材
├── .github/workflows/    # CI/CD 流水线 (GitHub Actions)
└── AGENTS.md             # AI 助手专属开发规范与指令
```


## 🚀 开始使用

### 环境要求
- Android Studio Ladybug | 2024.2.1 或更高版本
- JDK 17 或更高版本
- Android SDK API 24+
- Gradle 8.10+

### 构建
1. 克隆仓库。
2. 在 Android Studio 中打开。
3. 同步 Gradle 并运行。

## 🎯 核心亮点

### 优化的 MVI ViewModel
项目使用自定义的 `BaseViewModelOptimized` 处理：
- 状态持久化与重放。
- 协程异常捕获。
- 基于 Action 的副作用处理。

### 动态着色系统 (Tint System)
允许用户切换：
- **主题模式**: 亮色、暗色、系统随动。
- **对比度**: 标准、中等、高对比。
- **字体样式**: 系统、楷体、宋体、衬线、等宽。

### 性能优化
遵循 Jetpack Compose 最佳实践：
- 使用 `collectAsStateWithLifecycle` 进行高效状态采集。
- 在 LazyLayout 中使用 `key` 优化列表性能（进行中）。
- 稳定领域模型，减少不必要的重组。

## 📄 开源协议
Copyright 2024 Sun Yufeng. 基于 Apache License, Version 2.0 授权。

---
<div align="center">
用 ❤️ 和 Jetpack Compose 制作
</div>
