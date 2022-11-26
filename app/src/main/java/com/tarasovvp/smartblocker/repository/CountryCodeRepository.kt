package com.tarasovvp.smartblocker.repository

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.tarasovvp.smartblocker.BlackListerApp
import com.tarasovvp.smartblocker.constants.Constants.COUNTRY_CODE_START
import com.tarasovvp.smartblocker.extensions.countryCodeList
import com.tarasovvp.smartblocker.models.CountryCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CountryCodeRepository {

    private val countryCodeDao = BlackListerApp.instance?.database?.countryCodeDao()

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

    suspend fun getCountryCode(code: Int): CountryCode? {
        return countryCodeDao?.getCountryCode(String.format(COUNTRY_CODE_START, code.toString()))
    }
}