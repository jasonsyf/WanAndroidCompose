package com.syf.wanandroidcompose.home

import com.syf.wanandroidcompose.home.local.ArticleEntity
import com.syf.wanandroidcompose.home.local.BannerEntity
import com.syf.wanandroidcompose.home.local.HomeDao
import com.syf.wanandroidcompose.home.local.WeChatAccountEntity
import kotlinx.coroutines.flow.Flow

/**
 * 首页本地数据源
 *
 * 负责与本地数据库（Room）进行交互，提供数据的读取和写入功能。
 * 它是 HomeRepository 的一部分，实现了数据缓存策略。
 *
 * @param homeDao Room 数据库的 Data Access Object (DAO)。
 */
class HomeLocalDataSource(private val homeDao: HomeDao) {

    /**
     * 从数据库获取所有文章，并以 Flow 的形式返回。
     * 当数据库中的文章数据发生变化时，Flow 会自动发射新的数据。
     */
    fun getAllArticles(): Flow<List<ArticleEntity>> = homeDao.getAllArticles()

    /**
     * 向数据库中插入一批文章。
     * 如果文章已存在，则忽略。
     */
    suspend fun insertArticles(articles: List<ArticleEntity>) = homeDao.insertArticles(articles)

    /**
     * 替换数据库中的所有文章。
     * 这个操作会先清空文章表，然后插入新的文章列表。
     */
    suspend fun replaceArticles(articles: List<ArticleEntity>) = homeDao.replaceArticles(articles)

    /**
     * 从数据库获取所有 Banner，并以 Flow 的形式返回。
     */
    fun getBanners(): Flow<List<BannerEntity>> = homeDao.getBanners()

    /**
     * 替换数据库中的所有 Banner。
     */
    suspend fun replaceBanners(banners: List<BannerEntity>) = homeDao.replaceBanners(banners)

    /**
     * 从数据库获取所有公众号账号，并以 Flow 的形式返回。
     */
    fun getWeChatAccounts(): Flow<List<WeChatAccountEntity>> = homeDao.getWeChatAccounts()

    /**
     * 替换数据库中的所有公众号账号。
     */
    suspend fun replaceWeChatAccounts(accounts: List<WeChatAccountEntity>) =
            homeDao.replaceWeChatAccounts(accounts)
}
