package com.syf.wanandroidcompose.common.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val type: Int,
) {
    companion object {
        const val TYPE_PROJECT = 1
        const val TYPE_TREE = 2
    }
}
