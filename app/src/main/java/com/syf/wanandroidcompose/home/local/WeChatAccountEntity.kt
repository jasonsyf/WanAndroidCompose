package com.syf.wanandroidcompose.home.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.syf.wanandroidcompose.home.WeChatAccountData

@Entity(tableName = "wechat_accounts")
data class WeChatAccountEntity(
        @PrimaryKey val id: Int,
        val name: String,
        val order: Int,
        val courseId: Int,
        val parentChapterId: Int,
        val userControlSetTop: Boolean,
        val visible: Int
)

fun WeChatAccountData.toEntity() =
        WeChatAccountEntity(
                id = id,
                name = name,
                order = order,
                courseId = courseId,
                parentChapterId = parentChapterId,
                userControlSetTop = userControlSetTop,
                visible = visible
        )

fun WeChatAccountEntity.toData() =
        WeChatAccountData(
                id = id,
                name = name,
                order = order,
                courseId = courseId,
                parentChapterId = parentChapterId,
                userControlSetTop = userControlSetTop,
                visible = visible
        )
