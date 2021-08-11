package com.contour.flowofthought.oltp

import androidx.room.TypeConverter
import com.contour.flowofthought.oltp.model.Markdown
import com.google.gson.Gson

class Converters {
    @TypeConverter
    fun stringListToJson(value: List<String>) = Gson().toJson(value)

    @TypeConverter
    fun longArrayListToJson(value: ArrayList<Long>) = Gson().toJson(value.toList())

    @TypeConverter
    fun markdownToJson(value: Markdown) = Gson().toJson(value)

    @TypeConverter
    fun jsonToStringList(value: String) = Gson().fromJson(value, Array<String>::class.java).toList()

    @TypeConverter
    fun jsonToLongList(value: String) = Gson().fromJson(value, Array<Long>::class.java).toList() as ArrayList

    @TypeConverter
    fun jsonToMarkdown(value: String) = Gson().fromJson(value, Markdown::class.java)
}