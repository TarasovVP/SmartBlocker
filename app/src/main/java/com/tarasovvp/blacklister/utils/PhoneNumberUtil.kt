package com.tarasovvp.blacklister.utils

import android.content.Context
import android.util.Log
import androidx.collection.ArrayMap
import androidx.collection.arrayMapOf
import androidx.core.text.isDigitsOnly
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import com.tarasovvp.blacklister.constants.Constants
import com.tarasovvp.blacklister.extensions.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

object PhoneNumberUtil {

    private var phoneNumberUtil: PhoneNumberUtil? = null
    private var countryCodeMap: ArrayMap<String, Int?>? = null

    init {
        phoneNumberUtil = PhoneNumberUtil.getInstance()
    }

    suspend fun countryCodeMap(): ArrayMap<String, Int?> = withContext(
        Dispatchers.Default
    ) {
        val countryCodeMap = arrayMapOf<String, Int?>()
        Locale.getAvailableLocales().forEach { locale -> if (locale.country.isNotEmpty() && locale.country.isDigitsOnly().not()) countryCodeMap[locale.flagEmoji() + locale.country] = phoneNumberUtil?.getCountryCodeForRegion(locale.country) }
        countryCodeMap
    }

    fun initCountryCodeMap(countryCodeMap: ArrayMap<String, Int?>?) {
        Log.e("filterAddTAG",
            "PhoneNumberUtil initCountryCodeMap countryCodeMap?.size ${countryCodeMap?.size}")
        this.countryCodeMap = countryCodeMap
    }

    private fun String?.getPhoneNumber(countryCode: String): Phonenumber.PhoneNumber? = try {
        phoneNumberUtil?.parse(this.trimmed(), countryCode.uppercase())
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    fun String?.isValidPhoneNumber(context: Context?): Boolean {
        return if (getPhoneNumber(context?.getUserCountry().orEmpty()).isNotNull()) phoneNumberUtil?.isValidNumber(getPhoneNumber(context?.getUserCountry().orEmpty())).isTrue() else false
    }

    fun countryCodeKey(filter: String?): String? {
        Log.e("filterAddTAG",
            "PhoneNumberUtil countryCodeKey filter $filter countryCodeMap?.size ${countryCodeMap?.size}")
        return countryCodeMap?.keys?.firstOrNull {
            filter?.startsWith(String.format(Constants.COUNTRY_CODE_START, countryCodeMap?.get(it))).isTrue()
        }
    }

    fun Context.extractedFilter(filter: String?): String {
        Log.e("filterAddTAG",
            "PhoneNumberUtil extractedFilter filter $filter countryCodeMap?.size ${countryCodeMap?.size}")
        val countryCodeValue = countryCodeMap?.values?.firstOrNull { filter?.startsWith(String.format(Constants.COUNTRY_CODE_START, it.toString())).isTrue() }
        return when {
            filter.isValidPhoneNumber(this) -> filter?.getPhoneNumber(getUserCountry().orEmpty())?.nationalNumber.toString()
            countryCodeValue.isNotNull() -> filter?.replace(String.format(Constants.COUNTRY_CODE_START, countryCodeValue.toString()), String.EMPTY).orEmpty()
            else -> filter.orEmpty()
        }
    }
}