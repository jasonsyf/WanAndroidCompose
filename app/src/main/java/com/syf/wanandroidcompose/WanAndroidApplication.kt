package com.syf.wanandroidcompose

import android.app.Application
import androidx.room.Room
import com.syf.wanandroidcompose.home.local.HomeDatabase

/**
 * 自定义 Application 类
 *
 * 用于进行应用的全局初始化操作。
 */
class WanAndroidApplication : Application() {

    // 数据库实例
    lateinit var database: HomeDatabase
        private set // 只允许在类内部修改

    override fun onCreate() {
        super.onCreate()
        // 初始化 Application 单例
        instance = this
        // 构建 Room 数据库
        database =
            Room.databaseBuilder(applicationContext, HomeDatabase::class.java, "wanandroid_db")
                .fallbackToDestructiveMigration() // 在数据库升级时，如果找不到迁移策略，则会销毁并重新创建数据库
                .build()
    }

    companion object {
        // Application 的单例，方便全局访问
        lateinit var instance: WanAndroidApplication
            private set // 只允许在类内部修改
    }
}
