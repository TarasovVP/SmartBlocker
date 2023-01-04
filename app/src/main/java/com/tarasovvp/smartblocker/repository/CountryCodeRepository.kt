package com.tarasovvp.smartblocker.repository

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.constants.Constants.COUNTRY_CODE_START
import com.tarasovvp.smartblocker.extensions.countryCodeList
import com.tarasovvp.smartblocker.models.CountryCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CountryCodeRepository {

    private val countryCodeDao = SmartBlockerApp.instance?.database?.countryCodeDao()

    suspend fun getSystemCountryCodeList(result: (Int, Int) -> Unit): ArrayList<CountryCode> =
        withContext(
            Dispatchers.Default
        ) {
            PhoneNumberUtil.getInstance().countryCodeList { size, position ->
                result.invoke(size, position)
            }
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

    suspend fun getCountryCodeWithCountry(country: String): CountryCode? {
        return countryCodeDao?.getCountryCodeWithCountry(country.uppercase())
    }

    suspend fun getCountryCodeWithCode(code: Int): CountryCode? {
        return countryCodeDao?.getCountryCodeWithCode(String.format(COUNTRY_CODE_START,
            code.toString()))
    }
}