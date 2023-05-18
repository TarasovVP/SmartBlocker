package com.tarasovvp.smartblocker.utils

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.utils.extensions.*

class AppPhoneNumberUtil {

    private var phoneNumberUtil: PhoneNumberUtil? = null

    init {
        phoneNumberUtil = PhoneNumberUtil.getInstance()
    }

    fun countryCodeList(result: (Int, Int) -> Unit): ArrayList<CountryCode> {
        val countryCodeMap = arrayListOf<CountryCode>()
        phoneNumberUtil?.supportedRegions?.sorted()?.forEachIndexed { index, region ->
            val countryCode =
                String.format(Constants.COUNTRY_CODE_START, phoneNumberUtil?.getCountryCodeForRegion(region).toString())
            val numberFormat = try {
                phoneNumberUtil?.format(phoneNumberUtil?.getExampleNumberForType(region, PhoneNumberUtil.PhoneNumberType.MOBILE),
                    PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)?.replace("$countryCode ", String.EMPTY).orEmpty()
            } catch (e: Exception) {
                String.EMPTY
            }
            countryCodeMap.add(CountryCode(region, countryCode, numberFormat))
            result.invoke(phoneNumberUtil?.supportedRegions.orEmpty().size, index)
        }
        return countryCodeMap
    }

    fun getPhoneNumber(phoneNumber: String?, country: String): Phonenumber.PhoneNumber? = try {
        if (phoneNumber.isNullOrEmpty()) null
        else if (phoneNumber.startsWith(Constants.PLUS_CHAR)) phoneNumberUtil?.parse(phoneNumber.digitsTrimmed(), String.EMPTY)
        else phoneNumberUtil?.parse(phoneNumber.digitsTrimmed(), country)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    fun phoneNumberValue(number: String?, country: String): String {
        val phoneNumber = getPhoneNumber(number, country)
        return if (isValidPhoneNumber(phoneNumber)) String.format("+%s%s", phoneNumber?.countryCode, phoneNumber?.nationalNumber) else number.orEmpty()
    }

    fun isPhoneNumberValid(number: String?, country: String): Boolean {
        val phoneNumber = getPhoneNumber(number, country)
        return try {
            if (phoneNumber.isNull()) false else phoneNumberUtil?.isValidNumber(phoneNumber).isTrue()
        } catch (e: Exception) {
            return false
        }
    }

    private fun isValidPhoneNumber(phoneNumber: Phonenumber.PhoneNumber?): Boolean {
        return try {
            if (this.isNull()) false else phoneNumberUtil?.isValidNumber(phoneNumber).isTrue()
        } catch (e: Exception) {
            return false
        }
    }
}