package com.tarasovvp.smartblocker.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.tarasovvp.smartblocker.model.Filter

class FilterConverter {

    @TypeConverter
    fun toCountryCode(json: String?): Filter? {
        return Gson().fromJson(json, Filter::class.java)
    }

    @TypeConverter
    fun toJson(filter: Filter?): String? {
        return Gson().toJson(filter)
    }
}