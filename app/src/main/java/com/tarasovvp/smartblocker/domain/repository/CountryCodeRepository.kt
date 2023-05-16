package com.tarasovvp.smartblocker.domain.repository

import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode

interface CountryCodeRepository {

    suspend fun getSystemCountryCodeList(result: (Int, Int) -> Unit): List<CountryCode>

    suspend fun insertAllCountryCodes(list: List<CountryCode>)

    suspend fun allCountryCodes(): List<CountryCode>

    suspend fun getCountryCodeByCode(code: Int): CountryCode?
}