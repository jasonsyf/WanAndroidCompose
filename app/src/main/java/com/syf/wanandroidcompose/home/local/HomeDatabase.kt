package com.syf.wanandroidcompose.home.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.syf.wanandroidcompose.common.local.CategoryEntity

@Database(
    entities = [ArticleEntity::class, BannerEntity::class, WeChatAccountEntity::class, CategoryEntity::class],
    version = 4,
    exportSchema = true,
)
@TypeConverters(ArticleTypeConverters::class)
abstract class HomeDatabase : RoomDatabase() {
    abstract fun homeDao(): HomeDao
}
