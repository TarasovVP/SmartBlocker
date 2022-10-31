package com.tarasovvp.blacklister.repository

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.constants.Constants.COUNTRY_CODE_START
import com.tarasovvp.blacklister.extensions.countryCodeList
import com.tarasovvp.blacklister.model.CountryCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CountryCodeRepository {

    private val countryCodeDao = BlackListerApp.instance?.database?.countryCodeDao()

    suspend fun getSystemCountryCodeList(): ArrayList<CountryCode> =
        withContext(
            Dispatchers.Default
        ) {
            PhoneNumberUtil.getInstance().countryCodeList()
        }

    suspend fun insertAllCountryCodes(list: List<CountryCode>) {
        countryCodeDao?.insertAllCountryCode(list)
    }

    suspend fun getAllCountryCodes(): List<CountryCode>? =
        withContext(
            Dispatchers.Default
        ) {
            countryCodeDao?.getAllCountryCodes()
        }

    suspend fun getCountryCode(code: Int): CountryCode? {
        return countryCodeDao?.getCountryCode(String.format(COUNTRY_CODE_START, code.toString()))
    }
}