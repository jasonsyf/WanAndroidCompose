package com.syf.wanandroidcompose.home.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.syf.wanandroidcompose.common.local.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeDao {
    // Articles

    // Categories
    @Query("SELECT * FROM categories WHERE type = :type")
    fun getCategoriesByType(type: Int): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Query("DELETE FROM categories WHERE type = :type")
    suspend fun clearCategoriesByType(type: Int)

    @Transaction
    suspend fun replaceCategoriesByType(
        type: Int,
        categories: List<CategoryEntity>,
    ) {
        clearCategoriesByType(type)
        insertCategories(categories)
    }

    @Query("SELECT * FROM articles WHERE moduleType = :moduleType AND chapterId = :chapterId")
    fun getArticlesByChapter(
        moduleType: Int,
        chapterId: Int,
    ): Flow<List<ArticleEntity>>

    @Query("DELETE FROM articles WHERE moduleType = :moduleType AND chapterId = :chapterId")
    suspend fun clearArticlesByChapter(
        moduleType: Int,
        chapterId: Int,
    )

    @Transaction
    suspend fun replaceArticlesByChapter(
        moduleType: Int,
        chapterId: Int,
        articles: List<ArticleEntity>,
    ) {
        clearArticlesByChapter(moduleType, chapterId)
        insertArticles(articles)
    }

    @Query("SELECT * FROM articles")
    fun getAllArticles(): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM articles WHERE moduleType = :moduleType")
    fun getArticlesByModule(moduleType: Int): Flow<List<ArticleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<ArticleEntity>)

    @Transaction
    suspend fun replaceArticles(articles: List<ArticleEntity>) {
        clearArticles()
        insertArticles(articles)
    }

    @Query("DELETE FROM articles")
    suspend fun clearArticles()

    // Banners
    @Query("SELECT * FROM banners")
    fun getBanners(): Flow<List<BannerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBanners(banners: List<BannerEntity>)

    @Query("DELETE FROM banners")
    suspend fun clearBanners()

    @Transaction
    suspend fun replaceBanners(banners: List<BannerEntity>) {
        clearBanners()
        insertBanners(banners)
    }

    // WeChat Accounts
    @Query("SELECT * FROM wechat_accounts")
    fun getWeChatAccounts(): Flow<List<WeChatAccountEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeChatAccounts(accounts: List<WeChatAccountEntity>)

    @Query("DELETE FROM wechat_accounts")
    suspend fun clearWeChatAccounts()

    @Transaction
    suspend fun replaceWeChatAccounts(accounts: List<WeChatAccountEntity>) {
        clearWeChatAccounts()
        insertWeChatAccounts(accounts)
    }
}
