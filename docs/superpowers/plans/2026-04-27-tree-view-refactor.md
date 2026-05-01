# TreeView Refinement Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Refine `TreeView.kt` by fixing skeleton padding mismatch, optimizing lambdas for stability, and smoothing out transitions.

**Architecture:** UI refinement within the existing MVI structure.

**Tech Stack:** Jetpack Compose, Material 3.

---

### Task 1: Lambda Optimization

**Files:**
- Modify: `app/src/main/java/com/syf/wanandroidcompose/tree/TreeView.kt`

- [ ] **Step 1: Wrap callback lambdas in `remember`**

Update `TreeView` to wrap lambdas passed to `ParentCategoryList` and `SubCategoryContent` in `remember` blocks.

```kotlin
    // 在 TreeView 中
    val onCategoryClick = remember(viewModel) {
        { parentId: Int -> viewModel.sendAction(TreeAction.SelectParentCategory(parentId)) }
    }
    
    val onSubCategoryClick = remember(viewModel) {
        { cid: Int -> viewModel.sendAction(TreeAction.SelectCategory(cid)) }
    }
    
    val onRefresh = remember(viewModel) {
        { viewModel.sendAction(TreeAction.Refresh) }
    }
    
    val onLoadMore = remember(viewModel) {
        { viewModel.sendAction(TreeAction.LoadMore) }
    }
    
    val onArticleClick = remember(rootNavController) {
        { article: ArticleData ->
            val encodedUrl = URLEncoder.encode(article.link, StandardCharsets.UTF_8.toString())
            rootNavController.navigate("detail/$encodedUrl")
        }
    }
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/syf/wanandroidcompose/tree/TreeView.kt
git commit -m "perf(tree): optimize lambdas with remember for better recomposition skipping"
```

### Task 2: Fix Skeleton Padding

**Files:**
- Modify: `app/src/main/java/com/syf/wanandroidcompose/tree/TreeView.kt`

- [ ] **Step 1: Update `SkeletonArticleItem` padding**

Match the padding of `ArticleItem` from the `home` module (which is `.padding(horizontal = 16.dp, vertical = 8.dp)`).

```kotlin
@Composable
private fun SkeletonArticleItem() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp) // 添加外层间距以匹配 ArticleItem
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        // ... 内容保持不变
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/syf/wanandroidcompose/tree/TreeView.kt
git commit -m "fix(tree): match SkeletonArticleItem padding with actual ArticleItem"
```

### Task 3: Smooth AnimatedContent Transition

**Files:**
- Modify: `app/src/main/java/com/syf/wanandroidcompose/tree/TreeView.kt`

- [ ] **Step 1: Enhance `AnimatedContent` transition**

Add `scaleIn` and `scaleOut` to the `fadeIn` and `fadeOut` for a smoother, more modern feel.

```kotlin
            AnimatedContent(
                targetState = isLoading && articles.isEmpty(),
                transitionSpec = {
                    (fadeIn(animationSpec = tween(220, delayMillis = 90)) + 
                     scaleIn(initialScale = 0.92f, animationSpec = tween(220, delayMillis = 90)))
                    .togetherWith(fadeOut(animationSpec = tween(90)) + 
                                  scaleOut(targetScale = 0.92f, animationSpec = tween(90)))
                },
                label = "TreeContentTransition"
            )
```

- [ ] **Step 2: Final Verification**

Run the app (or use layout inspector/previews) to verify:
1. Skeletons and ArticleItems are horizontally aligned.
2. No horizontal jump during loading transition.
3. Transition feels smooth.

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/syf/wanandroidcompose/tree/TreeView.kt
git commit -m "style(tree): polish AnimatedContent transition with scale and fade"
```
