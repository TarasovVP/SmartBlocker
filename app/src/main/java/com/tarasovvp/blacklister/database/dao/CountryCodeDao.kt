package com.tarasovvp.blacklister.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tarasovvp.blacklister.model.CountryCode

@Dao
interface CountryCodeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCountryCode(countryCodes: List<CountryCode>?)

    @Query("SELECT * FROM countrycode")
    suspend fun getAllCountryCodes(): List<CountryCode>

    @Query("SELECT * FROM countrycode WHERE :code = countryCode")
    suspend fun getCountryCode(code: Int): CountryCode?
}