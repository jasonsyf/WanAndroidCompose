# Jetpack Compose Audit Report - WanAndroidCompose

**Date:** 2026-04-20
**Overall Score:** 7/10 (Needs Work)
**Compiler Diagnostics Used:** Yes (Strong Skipping Mode: ON)

## Category Scores

| Category | Score | Status |
| :--- | :--- | :--- |
| **Performance** | 4/10 | Capped by 69.6% skippability |
| **State Management** | 8/10 | Solid |
| **Side Effects** | 9/10 | Excellent |
| **Composable API Quality** | 7/10 | Solid |

---

## Top Critical Findings

1.  **Missing Lazy List Keys:** Systemic absence of `key` in `LazyColumn` and `LazyRow` across `HomeView.kt`, `ProjectView.kt`, and `TreeView.kt`. This causes unnecessary recompositions and impacts scroll performance.
    *   **References:** [Optimizing performance in Jetpack Compose](https://developer.android.com/develop/ui/compose/performance/best-practices#use-keys)
2.  **Unstable Domain Models:** Core data classes like `ArticleData` and `BannerData` are inferred as `unstable` by the compiler. While Strong Skipping mitigates this, it still leads to extra memoization overhead and potential issues if Strong Skipping is disabled.
    *   **References:** [Composable metrics](https://developer.android.com/develop/ui/compose/performance/stability)
3.  **Inconsistent Lifecycle-Aware State Collection:** `HomeView.kt` uses `collectAsState`, which continues to collect updates even when the app is in the background, wasting resources. Other views correctly use `collectAsStateWithLifecycle`.
    *   **References:** [Consuming flows in a lifecycle-aware manner](https://developer.android.com/develop/ui/compose/state#lifecycle-aware)

---

## Performance (4/10)

**Reasoning:**
The qualitative performance is decent due to the modern MVI architecture, but the compiler report reveals a skippability of **69.6% (94/135 named composables)**. According to the rubric, this falls into the 50-70% band, triggering a **mandatory ceiling of 4**.

**Evidence:**
- `app/build/compose_audit/debug/app_debug-module.json`: `skippableComposables: 94, restartableComposables: 135`.
- `app/src/main/java/com/syf/wanandroidcompose/home/HomeView.kt:116`: `LazyColumn` without `key`.
- `app/src/main/java/com/syf/wanandroidcompose/home/HomeView.kt:93-95`: `bgColor` list created in every recomposition without `remember`.

**Performance ceiling check:**
- skippable% = 94/135 = 69.6% → falls in 50-70% band → cap at 4
- qualitative score: 7
- **applied score: 4**

---

## State Management (8/10)

**Reasoning:**
The project follows a clean MVI pattern using `BaseViewModelOptimized`. State hoisting is generally well-handled, and screens are separated from logic.

**Evidence:**
- `app/src/main/java/com/syf/wanandroidcompose/common/BaseViewModelOptimized.kt`: Robust state and action handling.
- `app/src/main/java/com/syf/wanandroidcompose/profile/ProfileView.kt:37`: Correct use of `collectAsStateWithLifecycle`.
- **Gap:** `app/src/main/java/com/syf/wanandroidcompose/home/HomeView.kt:97` uses `collectAsState`.

---

## Side Effects (9/10)

**Reasoning:**
`LaunchedEffect` is used correctly for one-time events like navigation and showing SnackBar. Auto-scrolling logic in the Carousel is handled safely with `LaunchedEffect`.

**Evidence:**
- `app/src/main/java/com/syf/wanandroidcompose/home/HomeView.kt:101`: `LaunchedEffect(state.navigateToDetail)` correctly handles navigation.
- `app/src/main/java/com/syf/wanandroidcompose/home/HomeView.kt:348`: `LaunchedEffect(isDragged)` for carousel auto-scroll.

---

## Composable API Quality (7/10)

**Reasoning:**
Reusable components like `ArticleItem` are well-structured but fail the "Modifier parameter" rule. Most internal components do not accept a `Modifier` for external positioning/sizing.

**Evidence:**
- `app/src/main/java/com/syf/wanandroidcompose/home/HomeView.kt:246`: `ArticleItem` missing `Modifier` parameter.
- `app/src/main/java/com/syf/wanandroidcompose/home/HomeView.kt:364`: `ChipTabRow` missing `Modifier` parameter.

---

## Prioritized Remediation List

1.  **Add Keys to Lazy Lists:** Add `key = { ... }` to all `items` calls in `HomeView.kt`, `ProjectView.kt`, and `TreeView.kt` using a unique ID (e.g., `item.id` or `item.link`).
    *   **Impact:** Significantly improves scroll performance and prevents unnecessary item recompositions.
    *   **Ref:** <https://developer.android.com/develop/ui/compose/performance/best-practices#use-keys>
2.  **Optimize Domain Model Stability:** Annotate `ArticleData`, `BannerData`, and `CategoryUiModel` with `@Stable` (if they meet the criteria) or use `kotlinx.collections.immutable` for `List` parameters.
    *   **Impact:** Moves skippability towards 90%+, reducing recomposition overhead.
    *   **Ref:** <https://developer.android.com/develop/ui/compose/performance/stability>
3.  **Fix Lifecycle State Collection:** Update `HomeView.kt` to use `collectAsStateWithLifecycle` from `androidx.lifecycle.compose`.
    *   **Impact:** Reduces background CPU/battery usage.
    *   **Ref:** <https://developer.android.com/develop/ui/compose/state#lifecycle-aware>
4.  **Adopt Modifier Parameter Pattern:** Ensure all reusable Composables (`ArticleItem`, `ChipTabRow`, `Carouse`, etc.) accept a `modifier: Modifier = Modifier` as their first optional parameter and apply it to the root layout.
    *   **Impact:** Improves component reusability and UI consistency.
    *   **Ref:** <https://developer.android.com/develop/ui/compose/components/layouts#modifiers-as-parameters>
