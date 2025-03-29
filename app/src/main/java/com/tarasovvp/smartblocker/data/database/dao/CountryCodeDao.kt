package com.tarasovvp.smartblocker.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode

@Dao
interface CountryCodeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCountryCodes(countryCodes: List<CountryCode>)

    @Query("SELECT * FROM country_codes")
    suspend fun allCountryCodes(): List<CountryCode>

    @Query("SELECT * FROM country_codes WHERE :code = countryCode")
    suspend fun getCountryCodeByCode(code: String): CountryCode?
}
