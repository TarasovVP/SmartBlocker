package com.tarasovvp.smartblocker.repositoryImpl

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.tarasovvp.smartblocker.constants.Constants.COUNTRY_CODE_START
import com.tarasovvp.smartblocker.database.dao.CountryCodeDao
import com.tarasovvp.smartblocker.extensions.countryCodeList
import com.tarasovvp.smartblocker.database.entities.CountryCode
import com.tarasovvp.smartblocker.repository.CountryCodeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CountryCodeRepositoryImpl @Inject constructor(
    private val countryCodeDao: CountryCodeDao
) : CountryCodeRepository {

    override suspend fun getSystemCountryCodeList(result: (Int, Int) -> Unit): ArrayList<CountryCode> =
        withContext(
            Dispatchers.Default
        ) {
            PhoneNumberUtil.getInstance().countryCodeList { size, position ->
                result.invoke(size, position)
            }
        }

    override suspend fun insertAllCountryCodes(list: List<CountryCode>) {
        countryCodeDao.insertAllCountryCode(list)
    }

    override suspend fun getAllCountryCodes(): List<CountryCode> =
        withContext(
            Dispatchers.Default
        ) {
            countryCodeDao.getAllCountryCodes()
        }

    override suspend fun getCountryCodeWithCountry(country: String): CountryCode? {
        return countryCodeDao.getCountryCodeWithCountry(country.uppercase())
    }

    override suspend fun getCountryCodeWithCode(code: Int): CountryCode? {
        return countryCodeDao.getCountryCodeWithCode(
            String.format(
                COUNTRY_CODE_START,
                code.toString()
            )
        )
    }
}