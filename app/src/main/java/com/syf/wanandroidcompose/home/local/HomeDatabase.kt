package com.syf.wanandroidcompose.home.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [ArticleEntity::class, BannerEntity::class, WeChatAccountEntity::class],
    version = 2,
    exportSchema = true
)
@TypeConverters(ArticleTypeConverters::class)
abstract class HomeDatabase : RoomDatabase() {
    abstract fun homeDao(): HomeDao
}
