# Tree Page UI Improvement Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Improve the Tree page sidebar UI by adopting a "Soft Neutral" design, adding a selected indicator, and refining layout contrast.

**Architecture:** MVI architecture using Jetpack Compose. Refactor to extract a stateless `TreeContent` for snapshot testing.

**Tech Stack:** Kotlin, Jetpack Compose (Material 3), Paparazzi (Snapshot Testing).

---

### Task 1: Refactor TreeView.kt to extract TreeContent

**Files:**
- Modify: `app/src/main/java/com/syf/wanandroidcompose/tree/TreeView.kt`

- [ ] **Step 1: Extract TreeContent from TreeView**
Move the UI logic from `TreeView` to a new `@Composable fun TreeContent`. `TreeView` will handle ViewModel interaction and navigation, then pass state and callbacks to `TreeContent`.

- [ ] **Step 2: Commit refactoring**
```bash
git add app/src/main/java/com/syf/wanandroidcompose/tree/TreeView.kt
git commit -m "refactor(tree): extract stateless TreeContent for testing [Task 1]"
```

### Task 2: Add TreeSnapshotTest.kt

**Files:**
- Create: `app/src/test/java/com/syf/wanandroidcompose/tree/TreeSnapshotTest.kt`

- [ ] **Step 1: Create snapshot test class**
Use Paparazzi to capture `TreeContent` in different states.

```kotlin
package com.syf.wanandroidcompose.tree

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.syf.wanandroidcompose.theme.WanAndroidComposeTheme
import org.junit.Rule
import org.junit.Test

class TreeSnapshotTest {
    @get:Rule
    val paparazzi = Paparazzi(deviceConfig = DeviceConfig.PIXEL_5)

    @Test
    fun snapshot_tree_initial_state() {
        paparazzi.snapshot {
            WanAndroidComposeTheme {
                TreeContent(
                    state = TreeState(
                        categories = listOf(
                            TreeCategory(id = 1, name = "体系"),
                            TreeCategory(id = 2, name = "公众号")
                        ),
                        selectedParentId = 1
                    ),
                    onCategoryClick = {},
                    onSubCategoryClick = {},
                    onRefresh = {},
                    onLoadMore = {},
                    onArticleClick = {}
                )
            }
        }
    }
}
```

- [ ] **Step 2: Record baseline snapshots**
Run: `./gradlew recordPaparazziDebug`
Expected: Baseline images generated in `app/src/test/snapshots/images/`.

- [ ] **Step 3: Commit baseline**
```bash
git add app/src/test/java/com/syf/wanandroidcompose/tree/TreeSnapshotTest.kt
git commit -m "test(tree): add baseline snapshot test [Task 2]"
```

### Task 3: Implement Sidebar UI Refinement

**Files:**
- Modify: `app/src/main/java/com/syf/wanandroidcompose/tree/TreeView.kt`

- [ ] **Step 1: Update ParentCategoryList styling**
Change sidebar background to a warmer tone. Add vertical indicator for selected item. Add right border/divider.

```kotlin
// In ParentCategoryList
LazyColumn(
    modifier = Modifier
        .width(100.dp)
        .fillMaxHeight()
        .background(MaterialTheme.colorScheme.orangeSecondary.copy(alpha = 0.1f)) // Warmer background
        .drawBehind { // Add right border
             drawLine(
                 color = Color(0xFFFED7AA),
                 start = Offset(size.width, 0f),
                 end = Offset(size.width, size.height),
                 strokeWidth = 1.dp.toPx()
             )
        }
)
```

- [ ] **Step 2: Update item selection visual**
Selected item gets white background and left indicator bar.

```kotlin
// In items block of ParentCategoryList
Box(
    modifier = Modifier
        .fillMaxWidth()
        .background(if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent)
        // ...
) {
    if (isSelected) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .width(4.dp)
                .height(24.dp)
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(0.dp, 4.dp, 4.dp, 0.dp))
        )
    }
    // ... Text
}
```

- [ ] **Step 3: Update skeletons to match colors**

- [ ] **Step 4: Verify with Paparazzi**
Run: `./gradlew verifyPaparazziDebug` (Should fail if changed)
Run: `./gradlew recordPaparazziDebug` (To update images)

- [ ] **Step 5: Commit changes**
```bash
git add app/src/main/java/com/syf/wanandroidcompose/tree/TreeView.kt
git commit -m "feat(tree): improve sidebar UI with soft neutral design [Task 3]"
```
