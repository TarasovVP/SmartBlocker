package com.tarasovvp.smartblocker.usecases

import com.tarasovvp.smartblocker.domain.models.entities.CountryCode
import com.tarasovvp.smartblocker.domain.repository.CountryCodeRepository
import com.tarasovvp.smartblocker.domain.usecase.countrycode_search.CountryCodeSearchUseCaseImpl
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
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

    lateinit var countryCodeSearchUseCaseImpl: CountryCodeSearchUseCaseImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        countryCodeSearchUseCaseImpl = CountryCodeSearchUseCaseImpl(countryCodeRepository)
    }

    @Test
    fun getCountryCodeList() = runTest{
        val country = "UA"
        val countryCode = "+380"
        val countryCodeList = listOf(CountryCode(countryCode = countryCode, country = country), CountryCode(countryCode = "+123", country = "AI"))
        Mockito.`when`(countryCodeRepository.getAllCountryCodes())
            .thenReturn(countryCodeList)
        val result = countryCodeSearchUseCaseImpl.getCountryCodeList()
        assertEquals(country, result[0].country)
    }
}