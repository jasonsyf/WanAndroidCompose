package com.syf.wanandroidcompose

import android.app.Application
import androidx.room.Room
import com.syf.wanandroidcompose.home.local.HomeDatabase

class WanAndroidApplication : Application() {
    lateinit var database: HomeDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        database =
            Room.databaseBuilder(applicationContext, HomeDatabase::class.java, "wanandroid_db")
                .fallbackToDestructiveMigration().build()
    }

    companion object {
        lateinit var instance: WanAndroidApplication
            private set
    }
}
