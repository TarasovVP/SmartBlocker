package com.tarasovvp.blacklister.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.tarasovvp.blacklister.model.CountryCode

class CountryCodeConverter {

    @TypeConverter
    fun toCountryCode(json: String?): CountryCode? {
        return Gson().fromJson(json, CountryCode::class.java)
    }

    @TypeConverter
    fun toJson(countryCode: CountryCode?): String? {
        return Gson().toJson(countryCode)
    }
}