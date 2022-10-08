package com.tarasovvp.blacklister.database

import androidx.room.*
import com.tarasovvp.blacklister.model.CountryCode

@Dao
interface CountryCodeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllCountryCode(countryCodes: List<CountryCode>?)

    @Query("SELECT * FROM countrycode")
    fun getAllCountryCodes(): List<CountryCode>

    @Query("SELECT * FROM countrycode WHERE :filter LIKE countryCode || '%' ")
    fun getCountryCode(filter: String): CountryCode?
}