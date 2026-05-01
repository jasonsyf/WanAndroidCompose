# [WanAndroidCompose] 数据库缓存 TDD 实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [x]`) syntax for tracking.

**Goal:** 通过测试驱动开发 (TDD) 模式为“项目”和“体系”页面添加数据库缓存，实现“本地优先 + 网络刷新”策略，提升响应速度。

**Architecture:** 
- **Entity 层**: 扩展 `ArticleEntity` 支持模块区分，新增 `CategoryEntity`。
- **DAO 层**: 增强 `HomeDao` 处理多模块过滤和分类存储。
- **LocalDataSource 层**: 封装数据库访问。
- **Repository 层**: 重构 `getProjectTree`, `getProjectList`, `getSystemTree`, `getSystemArticles` 以支持流式缓存。

**Tech Stack:** Room, Flow, JUnit 4, Robolectric, Mockk.

---

### Task 1: [DAO] 扩展 ArticleEntity 以支持多模块 (RED)

**Files:**
- Modify: `app/src/main/java/com/syf/wanandroidcompose/home/local/ArticleEntity.kt`
- Create: `app/src/test/java/com/syf/wanandroidcompose/home/local/HomeDaoTest.kt`

- [x] **Step 1: 在 ArticleEntity 中增加 moduleType 字段并定义常量**

```kotlin
// 修改 ArticleEntity.kt
@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey val id: Int,
    // ...
    val moduleType: Int = MODULE_HOME 
) {
    companion object {
        const val MODULE_HOME = 0
        const val MODULE_PROJECT = 1
        const val MODULE_TREE = 2
    }
}
```

- [x] **Step 2: 编写失败的测试用例验证按模块过滤**

```kotlin
// HomeDaoTest.kt (Robolectric)
@Test
fun testGetArticlesByModule() = runBlocking {
    val projectArticle = createFakeArticle(id = 1, moduleType = ArticleEntity.MODULE_PROJECT)
    val treeArticle = createFakeArticle(id = 2, moduleType = ArticleEntity.MODULE_TREE)
    dao.insertArticles(listOf(projectArticle, treeArticle))
    
    // 假设 getArticlesByModule 接口已定义但逻辑未实现（或未增加过滤）
    val result = dao.getArticlesByModule(ArticleEntity.MODULE_PROJECT).first()
    assertEquals(1, result.size)
    assertEquals(1, result[0].id)
}
```

- [x] **Step 3: 运行测试并验证失败**

Run: `./gradlew :app:testDebugUnitTest --tests "com.syf.wanandroidcompose.home.local.HomeDaoTest"`

- [x] **Step 4: 实现逻辑使测试通过 (GREEN)**

- [x] **Step 5: 提交更改**

---

### Task 2: [DAO] 实现分类缓存支持 (TDD)

**Files:**
- Create: `app/src/main/java/com/syf/wanandroidcompose/common/local/CategoryEntity.kt`
- Modify: `app/src/main/java/com/syf/wanandroidcompose/home/local/HomeDao.kt`

- [x] **Step 1: 编写分类存取测试用例 (RED)**
  验证 `replaceCategoriesByType` 能够正确覆盖指定类型的旧数据。
- [x] **Step 2: 运行测试并验证失败**
- [x] **Step 3: 实现 CategoryEntity 和 DAO 逻辑 (GREEN)**
- [x] **Step 4: 运行测试并验证通过**
- [x] **Step 5: 提交更改**

---

### Task 3: [LocalDataSource] 封装本地数据访问 (TDD)

**Files:**
- Create: `app/src/main/java/com/syf/wanandroidcompose/project/ProjectLocalDataSource.kt`
- Create: `app/src/main/java/com/syf/wanandroidcompose/tree/TreeLocalDataSource.kt`

- [x] **Step 1: 编写单元测试验证数据源映射逻辑 (RED)**
- [x] **Step 2: 运行测试并验证失败**
- [x] **Step 3: 实现 LocalDataSource (GREEN)**
- [x] **Step 4: 运行测试并验证通过**
- [x] **Step 5: 提交更改**

---

### Task 4: [Repository] 重构 ProjectRepository 逻辑 (TDD)

**Files:**
- Modify: `app/src/main/java/com/syf/wanandroidcompose/project/ProjectRepository.kt`
- Create: `app/src/test/java/com/syf/wanandroidcompose/project/ProjectRepositoryTest.kt`

- [x] **Step 1: 编写测试用例验证“先发本地后发网络” (RED)**

```kotlin
@Test
fun testGetProjectTree_EmitsLocalThenRemote() = runTest {
    coEvery { localDataSource.getCategories() } returns listOf(localCategory)
    coEvery { apiService.getProjectTree() } returns ApiResponse(remoteData)
    
    val results = repository.getProjectTree().toList()
    
    assertTrue(results[0] is Result.Success) // Local
    assertTrue(results[1] is Result.Success) // Remote
}
```

- [x] **Step 2: 运行测试并验证失败**
- [x] **Step 3: 重构 Repository 实现 (GREEN)**
- [x] **Step 4: 运行测试并验证通过**
- [x] **Step 5: 提交更改**

---

### Task 5: [Repository] 重构 TreeRepository 逻辑 (TDD)

**Files:**
- Modify: `app/src/main/java/com/syf/wanandroidcompose/tree/TreeRepository.kt`

- [x] **Step 1: 编写体系页面相关缓存测试 (RED)**
- [x] **Step 2: 运行测试并验证失败**
- [x] **Step 3: 实现逻辑 (GREEN)**
- [x] **Step 4: 运行测试并验证通过**
- [x] **Step 5: 提交更改**
