package com.syf.wanandroidcompose.home

import com.syf.wanandroidcompose.home.local.ArticleEntity
import com.syf.wanandroidcompose.home.local.BannerEntity
import com.syf.wanandroidcompose.home.local.HomeDao
import com.syf.wanandroidcompose.home.local.WeChatAccountEntity
import kotlinx.coroutines.flow.Flow

class HomeLocalDataSource(private val homeDao: HomeDao) {
    fun getAllArticles(): Flow<List<ArticleEntity>> = homeDao.getAllArticles()

    suspend fun insertArticles(articles: List<ArticleEntity>) = homeDao.insertArticles(articles)

    suspend fun replaceArticles(articles: List<ArticleEntity>) = homeDao.replaceArticles(articles)

    fun getBanners(): Flow<List<BannerEntity>> = homeDao.getBanners()

    suspend fun replaceBanners(banners: List<BannerEntity>) = homeDao.replaceBanners(banners)

    fun getWeChatAccounts(): Flow<List<WeChatAccountEntity>> = homeDao.getWeChatAccounts()

    suspend fun replaceWeChatAccounts(accounts: List<WeChatAccountEntity>) =
            homeDao.replaceWeChatAccounts(accounts)
}