package com.tarasovvp.smartblocker.domain.repository

import com.tarasovvp.smartblocker.domain.models.entities.CountryCode

interface CountryCodeRepository {

    suspend fun getSystemCountryCodeList(result: (Int, Int) -> Unit): List<CountryCode>

    suspend fun insertAllCountryCodes(list: List<CountryCode>)

    suspend fun getAllCountryCodes(): List<CountryCode>

    suspend fun getCountryCodeWithCountry(country: String): CountryCode?

    suspend fun getCountryCodeWithCode(code: Int): CountryCode?
}