package com.tarasovvp.smartblocker.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tarasovvp.smartblocker.models.CountryCode

@Dao
interface CountryCodeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCountryCode(countryCodes: List<CountryCode>?)

    @Query("SELECT * FROM country_codes")
    suspend fun getAllCountryCodes(): List<CountryCode>

    @Query("SELECT * FROM country_codes WHERE :country = country")
    suspend fun getCountryCodeWithCountry(country: String): CountryCode?

    @Query("SELECT * FROM country_codes WHERE :code = countryCode")
    suspend fun getCountryCodeWithCode(code: String): CountryCode?
}