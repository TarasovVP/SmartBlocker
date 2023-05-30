package com.tarasovvp.smartblocker.repositories

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_COUNTRY
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_COUNTRY_CODE
import com.tarasovvp.smartblocker.data.database.dao.CountryCodeDao
import com.tarasovvp.smartblocker.data.repositoryImpl.CountryCodeRepositoryImpl
import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode
import com.tarasovvp.smartblocker.domain.repository.CountryCodeRepository
import com.tarasovvp.smartblocker.utils.AppPhoneNumberUtil
import io.mockk.*
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class CountryCodeRepositoryUnitTest {

    @MockK
    private lateinit var appPhoneNumberUtil: AppPhoneNumberUtil

    @MockK
    private lateinit var countryCodeDao: CountryCodeDao

    private lateinit var countryCodeRepository: CountryCodeRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        countryCodeRepository = CountryCodeRepositoryImpl(appPhoneNumberUtil, countryCodeDao)
    }

    @Test
    fun getSystemCountryCodeListTest() = runBlocking {
        val countryCodeList = arrayListOf(CountryCode(country = TEST_COUNTRY), CountryCode())
        val resultMock = mockk<(Int, Int) -> Unit>(relaxed = true)
        coEvery { appPhoneNumberUtil.countryCodeList(any()) } answers {
            val result: (Int, Int) -> Unit = arg(0)
            for (i in countryCodeList.indices) {
                result.invoke(countryCodeList.size, i)
            }
            countryCodeList
        }
        val result = countryCodeRepository.getSystemCountryCodeList(resultMock)
        verify(exactly = countryCodeList.size) { resultMock.invoke(any(), any()) }
        assertEquals(countryCodeList, result)
    }

    @Test
    fun insertAllCountryCodesTest() = runBlocking {
        val countryCodeList = listOf(CountryCode(country = TEST_COUNTRY), CountryCode())
        coEvery { countryCodeDao.insertAllCountryCodes(countryCodeList) } just Runs
        countryCodeRepository.insertAllCountryCodes(countryCodeList)
        coVerify(exactly = 1) { countryCodeDao.insertAllCountryCodes(countryCodeList) }
    }

    @Test
    fun getAllCountryCodesTest() = runBlocking {
        val countryCodeList = listOf(CountryCode(country = TEST_COUNTRY), CountryCode())
        coEvery { countryCodeDao.allCountryCodes() } returns countryCodeList
        val result = countryCodeRepository.allCountryCodes()
        assertEquals(countryCodeList, result)
    }

    @Test
    fun getCountryCodeByCodeTest() = runBlocking {
        val countryCode = CountryCode(country = TEST_COUNTRY, countryCode = TEST_COUNTRY)
        coEvery { countryCodeDao.getCountryCodeByCode(TEST_COUNTRY_CODE) } returns countryCode
        val result = countryCodeRepository.getCountryCodeByCode(380)
        assertEquals(countryCode, result)
    }
}