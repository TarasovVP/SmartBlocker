package com.tarasovvp.smartblocker.data.repositoryImpl

import com.tarasovvp.smartblocker.data.database.dao.CountryCodeDao
import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode
import com.tarasovvp.smartblocker.domain.repository.CountryCodeRepository
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.COUNTRY_CODE_START
import com.tarasovvp.smartblocker.utils.AppPhoneNumberUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CountryCodeRepositoryImpl @Inject constructor(
    private val appPhoneNumberUtil: AppPhoneNumberUtil,
    private val countryCodeDao: CountryCodeDao
) : CountryCodeRepository {

    override suspend fun getSystemCountryCodeList(result: (Int, Int) -> Unit): List<CountryCode> =
        withContext(Dispatchers.Default) {
            appPhoneNumberUtil.countryCodeList { size, position ->
                result.invoke(size, position)
            }
        }

    override suspend fun insertAllCountryCodes(list: List<CountryCode>) =
        countryCodeDao.insertAllCountryCodes(list)

    override suspend fun allCountryCodes(): List<CountryCode> =
        countryCodeDao.allCountryCodes()

    override suspend fun getCountryCodeByCode(code: Int): CountryCode? =
        countryCodeDao.getCountryCodeWithCode(String.format(COUNTRY_CODE_START, code.toString()))
}