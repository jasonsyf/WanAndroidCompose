package com.syf.wanandroidcompose.home.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.syf.wanandroidcompose.home.ArticleData
import com.syf.wanandroidcompose.home.ArticleTag
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * 文章数据在 Room 数据库中的实体类。
 * @property tags 使用了 TypeConverter 将 List<ArticleTag> 转换为 String 进行存储。
 */
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

/**
 * 将网络数据模型 `ArticleData` 转换为数据库实体模型 `ArticleEntity`。
 */
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

/**
 * 将数据库实体模型 `ArticleEntity` 转换为网络数据模型 `ArticleData`。
 */
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

/**
 * Room 的类型转换器，用于在 `List<ArticleTag>` 和 `String` 之间进行转换。
 * Room 本身不支持直接存储复杂对象列表，需要通过这种方式序列化为 JSON 字符串进行存储。
 */
class ArticleTypeConverters {
    private val json = Json { ignoreUnknownKeys = true } // 配置 Json 解析器

    @TypeConverter
    fun fromTags(tags: List<ArticleTag>): String {
        return json.encodeToString(tags)
    }

    @TypeConverter
    fun toTags(data: String): List<ArticleTag> {
        return if (data.isEmpty()) emptyList() else json.decodeFromString(data)
    }
}
