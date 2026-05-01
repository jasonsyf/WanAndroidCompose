# [ViewModel] 实现双级分类联动逻辑 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement the dual-level classification linkage logic in `TreeViewModel` to handle parent-child category selection and article list updates.

**Architecture:** MVI (Model-View-Intent). The ViewModel processes actions to update state. `TreeAction.SelectParentCategory` will trigger a state update for selected parent and sub-categories, resetting the article list and loading the first sub-category's articles.

**Tech Stack:** Kotlin, Jetpack Compose, Coroutines, Flow.

---

### Task 1: Update `onAction` to handle `SelectParentCategory`

**Files:**
- Modify: `app/src/main/java/com/syf/wanandroidcompose/tree/TreeViewModel.kt`

- [ ] **Step 1: Add case for `TreeAction.SelectParentCategory`**

In the `onAction` method, add the following case:

```kotlin
            is TreeAction.SelectParentCategory -> selectParentCategory(action.parentId)
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/syf/wanandroidcompose/tree/TreeViewModel.kt
git commit -m "feat(tree): add SelectParentCategory case to onAction"
```

---

### Task 2: Implement `selectParentCategory` method

**Files:**
- Modify: `app/src/main/java/com/syf/wanandroidcompose/tree/TreeViewModel.kt`

- [ ] **Step 1: Implement the private `selectParentCategory` method**

Add the `selectParentCategory` method to `TreeViewModel`. This method should update the state with the selected parent ID, its children as sub-categories, and reset the article list.

```kotlin
    /**
     * 选择主分类
     */
    private fun selectParentCategory(parentId: Int) {
        val currentState = replayState ?: return
        if (currentState.selectedParentId == parentId) return

        val parent = currentState.categories.find { it.id == parentId }
        val subCategories = parent?.children ?: emptyList()
        val firstSubId = subCategories.firstOrNull()?.id ?: 0

        emitState {
            replayState?.copy(
                selectedParentId = parentId,
                subCategories = subCategories,
                selectedCid = firstSubId,
                articles = emptyList(),
                hasMore = true,
                errorMsg = null
            )
        }

        if (firstSubId != 0) {
            // 重置当前分类 ID 和页码，加载新主分类下第一个子分类的文章
            currentCid = firstSubId
            currentPage = 0
            sendAction(TreeAction.LoadArticles(firstSubId, 0))
        }
    }
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/syf/wanandroidcompose/tree/TreeViewModel.kt
git commit -m "feat(tree): implement selectParentCategory logic"
```

---

### Task 3: Update `loadSystemTree` success logic

**Files:**
- Modify: `app/src/main/java/com/syf/wanandroidcompose/tree/TreeViewModel.kt`

- [ ] **Step 1: Update `loadSystemTree` to initialize dual-level state**

Modify the success branch of `loadSystemTree` to initialize `selectedParentId` and call `selectParentCategory` if it's the first load.

Find the `Result.Success` block in `loadSystemTree`:

```kotlin
                        is Result.Success -> {
                            // 转换原始数据为 UI 模型
                            val categories = result.data.map { mapTreeDataToTreeCategory(it) }
                            
                            emitState {
                                replayState?.copy(
                                    isLoading = false,
                                    categories = categories,
                                    errorMsg = null,
                                ) ?: TreeState(
                                    categories = categories,
                                )
                            }
                            
                            // 默认选择第一个主分类
                            val currentParentId = replayState?.selectedParentId ?: 0
                            if (currentParentId == 0 && categories.isNotEmpty()) {
                                selectParentCategory(categories.first().id)
                            }
                        }
```

*Note: Remove the old `allSubCategories` and `currentCid` initialization logic that was based on the flat structure.*

- [ ] **Step 2: Verify the logic**

Ensure that `allSubCategories` is no longer used in `loadSystemTree` as it's being replaced by `subCategories` managed via `selectParentCategory`.

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/syf/wanandroidcompose/tree/TreeViewModel.kt
git commit -m "feat(tree): update loadSystemTree to initialize dual-level state"
```

---

### Task 4: Final Validation

- [ ] **Step 1: Build the project**

Run: `./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 2: Verify article loading**

Check if `sendAction(TreeAction.LoadArticles(firstSubId, 0))` is correctly triggered on first load and when parent category changes.
