# 体系页面 UI 优化设计文档 (Tree Page UI Improvement)

针对体系页面左侧侧边栏视觉效果不佳（未选中态白色突兀）的问题进行优化。

## 1. 目标 (Goals)
- 消除左侧侧边栏在未选中状态下的突兀白色。
- 增强侧边栏与内容区域的视觉层次感。
- 优化选中态的视觉指引，使其更符合现代 Android UI 规范。

## 2. 设计方案 (Design Details - Approach A: Soft Neutral)

### 2.1 颜色调整
- **侧边栏背景 (Sidebar Background)**:
  - 当前: `MaterialTheme.colorScheme.surfaceContainerLowest` (接近纯白)。
  - 优化: 使用比主内容区略深的暖色调。建议使用 `OrangeSecondary.copy(alpha = 0.15f)` 或 `Color(0xFFFDE6D2)`。
- **未选中项 (Unselected Item)**:
  - 背景: 透明 (`Color.Transparent`)。
  - 文字: `DeepText.copy(alpha = 0.6f)`。
- **选中项 (Selected Item)**:
  - 背景: 纯白 (`MaterialTheme.colorScheme.surface`)，与右侧内容区融为一体。
  - 文字: `MaterialTheme.colorScheme.primary` (加粗)。
  - 指示条: 左侧增加一个 4dp 宽的垂直圆角指示条，颜色为 `MaterialTheme.colorScheme.primary`。

### 2.2 布局微调
- **侧边栏宽度**: 保持 `100.dp`，但增加右侧细分割线 `Divider`，颜色为 `OrangeSecondary.copy(alpha = 0.2f)`。
- **内容区**: 右侧内容区背景保持 `MaterialTheme.colorScheme.surface` (纯白)，与选中项背景衔接。

## 3. 技术实现 (Implementation)
- 修改 `TreeView.kt` 中的 `ParentCategoryList` Composable。
- 引入一个新的 `Modifier` 或 `Box` 来实现左侧垂直指示条。
- 确保骨架屏 (`SkeletonParentCategoryItem`) 也同步更新颜色方案，保持一致性。

## 4. 验证标准 (Success Criteria)
- 侧边栏在视觉上呈现出“容器”感，不再感觉是漂浮在白色背景上的文字。
- 选中项通过左侧条和背景色变化，产生清晰的“激活”反馈。
- 整体暖色调风格保持统一。

## 5. 遗留问题/注意事项 (Notes)
- 需检查 Dark Mode 下的适配效果，确保对比度符合 WCAG AA 标准。
