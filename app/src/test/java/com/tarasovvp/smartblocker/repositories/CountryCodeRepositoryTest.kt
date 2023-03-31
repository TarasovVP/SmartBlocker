package com.tarasovvp.smartblocker.repositories

import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.tarasovvp.smartblocker.TestUtils.TEST_COUNTRY
import com.tarasovvp.smartblocker.TestUtils.TEST_COUNTRY_CODE
import com.tarasovvp.smartblocker.data.database.dao.CountryCodeDao
import com.tarasovvp.smartblocker.data.repositoryImpl.CountryCodeRepositoryImpl
import com.tarasovvp.smartblocker.domain.models.entities.CountryCode
import com.tarasovvp.smartblocker.domain.repository.CountryCodeRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class CountryCodeRepositoryTest {

    @Mock
    private lateinit var countryCodeDao: CountryCodeDao

    private lateinit var countryCodeRepository: CountryCodeRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        countryCodeRepository = CountryCodeRepositoryImpl(countryCodeDao)
    }

    @Test
    fun getSystemCountryCodeListTest() = runTest {

    }

    @Test
    fun insertAllCountryCodesTest() = runTest {
        val countryCodeList = listOf(CountryCode(country = TEST_COUNTRY), CountryCode())
        countryCodeRepository.insertAllCountryCodes(countryCodeList)
        verify(countryCodeDao, times(1)).insertAllCountryCode(countryCodeList)
    }

    @Test
    fun getAllCountryCodesTest() = runTest {
        val countryCodeList = listOf(CountryCode(country = TEST_COUNTRY), CountryCode())
        Mockito.`when`(countryCodeDao.getAllCountryCodes()).thenReturn(countryCodeList)
        val result = countryCodeRepository.getAllCountryCodes()
        assertEquals(countryCodeList, result)
    }

    @Test
    fun getCountryCodeWithCountryTest() = runTest {
        val countryCode = CountryCode(country = TEST_COUNTRY, countryCode = TEST_COUNTRY)
        Mockito.`when`(countryCodeDao.getCountryCodeWithCountry(TEST_COUNTRY)).thenReturn(countryCode)
        val result = countryCodeRepository.getCountryCodeWithCountry(TEST_COUNTRY)
        assertEquals(countryCode, result)
    }

    @Test
    fun getCountryCodeWithCodeTest() = runTest {
        val countryCode = CountryCode(country = TEST_COUNTRY, countryCode = TEST_COUNTRY)
        Mockito.`when`(countryCodeDao.getCountryCodeWithCode(TEST_COUNTRY_CODE)).thenReturn(countryCode)
        val result = countryCodeRepository.getCountryCodeWithCode(380)
        assertEquals(countryCode, result)
    }
}