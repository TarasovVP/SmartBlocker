package com.example.blacklister.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object StringTypeConverters {
    private val gson = Gson()

    @TypeConverter
    @JvmStatic
    fun stringToStringList(data: String?): List<String> {
        if (data == null) {
            return ArrayList()
        }
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    @JvmStatic
    fun stringListToString(someObjects: List<String>): String {
        return gson.toJson(someObjects)
    }
}