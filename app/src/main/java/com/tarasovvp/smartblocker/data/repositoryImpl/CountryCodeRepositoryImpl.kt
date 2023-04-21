package com.tarasovvp.smartblocker.data.repositoryImpl

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.tarasovvp.smartblocker.data.database.dao.CountryCodeDao
import com.tarasovvp.smartblocker.domain.models.entities.CountryCode
import com.tarasovvp.smartblocker.utils.extensions.countryCodeList
import com.tarasovvp.smartblocker.domain.repository.CountryCodeRepository
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.COUNTRY_CODE_START
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

    override suspend fun getCountryCodeWithCode(code: Int): CountryCode? {
        return countryCodeDao.getCountryCodeWithCode(
            String.format(
                COUNTRY_CODE_START,
                code.toString()
            )
        )
    }
}