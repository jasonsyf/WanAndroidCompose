package com.syf.wanandroidcompose.home.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.syf.wanandroidcompose.home.ArticleData
import com.syf.wanandroidcompose.home.ArticleTag
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Entity(tableName = "articles")
@TypeConverters(ArticleTypeConverters::class)
data class ArticleEntity(
        @PrimaryKey val id: Int,
        val title: String,
        val link: String,
        val author: String,
        val shareUser: String,
        val chapterName: String,
        val superChapterName: String,
        val chapterId: Int,
        val superChapterId: Int,
        val niceDate: String,
        val desc: String,
        val collect: Boolean,
        val fresh: Boolean,
        val top: String,
        val tags: List<ArticleTag>
)

fun ArticleData.toEntity(): ArticleEntity {
    return ArticleEntity(
            id = id,
            title = title,
            link = link,
            author = author,
            shareUser = shareUser,
            chapterName = chapterName,
            superChapterName = superChapterName,
            chapterId = chapterId,
            superChapterId = superChapterId,
            niceDate = niceDate,
            desc = desc,
            collect = collect,
            fresh = fresh,
            top = top,
            tags = tags
    )
}

fun ArticleEntity.toData(): ArticleData {
    return ArticleData(
            id = id,
            title = title,
            link = link,
            author = author,
            shareUser = shareUser,
            chapterName = chapterName,
            superChapterName = superChapterName,
            chapterId = chapterId,
            superChapterId = superChapterId,
            niceDate = niceDate,
            desc = desc,
            collect = collect,
            fresh = fresh,
            top = top,
            tags = tags
    )
}

class ArticleTypeConverters {
    @TypeConverter
    fun fromTags(tags: List<ArticleTag>): String {
        return Json.encodeToString(tags)
    }

    @TypeConverter
    fun toTags(data: String): List<ArticleTag> {
        return if (data.isEmpty()) emptyList() else Json.decodeFromString(data)
    }
}
