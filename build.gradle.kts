// Top-level build file where you can add configuration options common to all sub-projects/modules.
import org.jlleitschuh.gradle.ktlint.tasks.KtLintCheckTask

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.paparazzi) apply false
    alias(libs.plugins.ktlint)
}

// =================================================================================
// 增量化检查配置 (File-level Incremental Check)
// =================================================================================
// 支持通过命令行参数实现精准检查，例如: ./gradlew ktlintCheck -PktlintFiles=File1.kt,File2.kt
val ktlintFilesParam = providers.gradleProperty("ktlintFiles")
if (ktlintFilesParam.isPresent) {
    val fileList = ktlintFilesParam.get().split(",")
    tasks.withType<org.jlleitschuh.gradle.ktlint.tasks.KtLintCheckTask>().configureEach {
        setSource(fileList)
    }
    tasks.withType<org.jlleitschuh.gradle.ktlint.tasks.KtLintFormatTask>().configureEach {
        setSource(fileList)
    }
}

// Git Hooks 安装任务
tasks.register<Copy>("copyGitHooks") {
    description = "Copies Git Hooks from scripts/git-hooks to .git/hooks"
    group = "git hooks"
    from(file("scripts/git-hooks"))
    into(file(".git/hooks"))
}

tasks.register<Exec>("installGitHooks") {
    description = "Installs Git Hooks and sets permissions"
    group = "git hooks"
    dependsOn("copyGitHooks")

    commandLine("chmod", "-R", "+x", ".git/hooks")
}

// 确保在编译前自动安装钩子
tasks.matching { it.name == "prepareKotlinBuildScriptModel" || it.name == "assemble" }.configureEach {
    dependsOn("installGitHooks")
}
