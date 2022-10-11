package com.tarasovvp.blacklister.repository

import android.content.Context
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.constants.Constants
import com.tarasovvp.blacklister.constants.Constants.PLUS_CHAR
import com.tarasovvp.blacklister.extensions.*
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

    fun insertAllCountryCodes(list: List<CountryCode>) {
        countryCodeDao?.insertAllCountryCode(list)
    }

    suspend fun getAllCountryCodes(): List<CountryCode>? =
        withContext(
            Dispatchers.Default
        ) {
            countryCodeDao?.getAllCountryCodes()
        }

    fun Context.extractedFilter(filter: String?): String {
        val countryCodeValue = countryCodeKey(filter)
        return when {filter.isValidPhoneNumber() -> filter?.getPhoneNumber()?.nationalNumber.toString()
            countryCodeValue.isNotNull() -> filter?.replace(String.format(Constants.COUNTRY_CODE_START,
                countryCodeValue), String.EMPTY).orEmpty()
            else -> filter.orEmpty()
        }
    }

    private fun countryCodeKey(filter: String?): String {
        return String.format(Constants.COUNTRY_CODE_START,
            countryCodeDao?.getCountryCode(filter?.replace(PLUS_CHAR.toString(), String.EMPTY)
                .orEmpty())?.countryCode)
    }
}