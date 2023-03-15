package com.tarasovvp.smartblocker.repository

import com.tarasovvp.smartblocker.database.entities.CountryCode

interface CountryCodeRepository {

    suspend fun getSystemCountryCodeList(result: (Int, Int) -> Unit): ArrayList<CountryCode>

    suspend fun insertAllCountryCodes(list: List<CountryCode>)

    suspend fun getAllCountryCodes(): List<CountryCode>

    suspend fun getCountryCodeWithCountry(country: String): CountryCode?

    suspend fun getCountryCodeWithCode(code: Int): CountryCode?
}