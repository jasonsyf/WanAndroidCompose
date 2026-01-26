package com.syf.wanandroidcompose.home.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.syf.wanandroidcompose.home.BannerData

@Entity(tableName = "banners")
data class BannerEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val desc: String,
    val imagePath: String,
    val url: String,
    val type: Int,
    val order: Int
)

fun BannerData.toEntity() = BannerEntity(
    id = id,
    title = title,
    desc = desc,
    imagePath = imagePath,
    url = url,
    type = type,
    order = order
)

fun BannerEntity.toData() = BannerData(
    id = id,
    title = title,
    desc = desc,
    imagePath = imagePath,
    url = url,
    type = type,
    order = order
)
