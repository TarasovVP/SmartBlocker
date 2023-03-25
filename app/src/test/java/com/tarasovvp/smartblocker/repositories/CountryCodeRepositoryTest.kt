package com.tarasovvp.smartblocker.repositories

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.tarasovvp.smartblocker.data.database.dao.CountryCodeDao
import com.tarasovvp.smartblocker.domain.models.entities.CountryCode
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.COUNTRY_CODE_START
import com.tarasovvp.smartblocker.utils.extensions.countryCodeList
import com.tarasovvp.smartblocker.domain.repository.CountryCodeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import javax.inject.Inject

@RunWith(MockitoJUnitRunner::class)
class CountryCodeRepositoryTest @Inject constructor(
    private val countryCodeDao: CountryCodeDao
) {

    suspend fun getSystemCountryCodeList(result: (Int, Int) -> Unit): ArrayList<CountryCode> =
        withContext(
            Dispatchers.Default
        ) {
            PhoneNumberUtil.getInstance().countryCodeList { size, position ->
                result.invoke(size, position)
            }
        }

    suspend fun insertAllCountryCodes(list: List<CountryCode>) {
        countryCodeDao.insertAllCountryCode(list)
    }

    suspend fun getAllCountryCodes(): List<CountryCode> =
        withContext(
            Dispatchers.Default
        ) {
            countryCodeDao.getAllCountryCodes()
        }

    suspend fun getCountryCodeWithCountry(country: String): CountryCode? {
        return countryCodeDao.getCountryCodeWithCountry(country.uppercase())
    }

    suspend fun getCountryCodeWithCode(code: Int): CountryCode? {
        return countryCodeDao.getCountryCodeWithCode(
            String.format(
                COUNTRY_CODE_START,
                code.toString()
            )
        )
    }
}