// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.paparazzi) apply false
    alias(libs.plugins.ktlint)
}

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
