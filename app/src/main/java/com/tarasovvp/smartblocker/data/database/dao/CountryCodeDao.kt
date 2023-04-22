package com.tarasovvp.smartblocker.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tarasovvp.smartblocker.domain.models.entities.CountryCode

@Dao
interface CountryCodeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCountryCode(countryCodes: List<CountryCode>?)

    @Query("SELECT * FROM country_codes")
    suspend fun getAllCountryCodes(): List<CountryCode>

    @Query("SELECT * FROM country_codes WHERE :code = countryCode")
    suspend fun getCountryCodeWithCode(code: String): CountryCode?
}