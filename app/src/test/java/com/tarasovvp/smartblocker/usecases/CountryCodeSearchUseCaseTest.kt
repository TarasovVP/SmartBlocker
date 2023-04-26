package com.tarasovvp.smartblocker.usecases

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_COUNTRY
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_COUNTRY_CODE
import com.tarasovvp.smartblocker.domain.models.entities.CountryCode
import com.tarasovvp.smartblocker.domain.repository.CountryCodeRepository
import com.tarasovvp.smartblocker.domain.usecase.countrycode_search.CountryCodeSearchUseCase
import com.tarasovvp.smartblocker.domain.usecase.countrycode_search.CountryCodeSearchUseCaseImpl
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
class CountryCodeSearchUseCaseTest {

    @Mock
    private lateinit var countryCodeRepository: CountryCodeRepository

    private lateinit var countryCodeSearchUseCase: CountryCodeSearchUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        countryCodeSearchUseCase = CountryCodeSearchUseCaseImpl(countryCodeRepository)
    }

    @Test
    fun getCountryCodeList() = runTest{
        val countryCodeList = listOf(CountryCode(countryCode = TEST_COUNTRY_CODE, country = TEST_COUNTRY), CountryCode(countryCode = "+123", country = "AI"))
        Mockito.`when`(countryCodeRepository.getAllCountryCodes())
            .thenReturn(countryCodeList)
        val result = countryCodeSearchUseCase.getCountryCodeList()
        assertEquals(TEST_COUNTRY, result[0].country)
    }
}