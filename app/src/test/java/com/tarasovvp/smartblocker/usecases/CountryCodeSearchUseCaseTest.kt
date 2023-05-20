package com.tarasovvp.smartblocker.usecases

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_APP_LANGUAGE
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_COUNTRY
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_COUNTRY_CODE
import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode
import com.tarasovvp.smartblocker.domain.repository.CountryCodeRepository
import com.tarasovvp.smartblocker.domain.repository.DataStoreRepository
import com.tarasovvp.smartblocker.domain.usecases.CountryCodeSearchUseCase
import com.tarasovvp.smartblocker.presentation.dialogs.country_code_search_dialog.CountryCodeSearchUseCaseImpl
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class CountryCodeSearchUseCaseTest {

    @MockK
    private lateinit var countryCodeRepository: CountryCodeRepository

    @MockK
    private lateinit var dataStoreRepository: DataStoreRepository

    private lateinit var countryCodeSearchUseCase: CountryCodeSearchUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        countryCodeSearchUseCase = CountryCodeSearchUseCaseImpl(countryCodeRepository, dataStoreRepository)
    }

    @Test
    fun getAppLanguageTest() = runBlocking{
        val appLanguage = TEST_APP_LANGUAGE
        val flow = flowOf(appLanguage)
        coEvery { dataStoreRepository.getAppLang() } returns flow
        val result = countryCodeSearchUseCase.getAppLanguage().single()
        assertEquals(appLanguage, result)
        coVerify { dataStoreRepository.getAppLang() }
    }

    @Test
    fun getCountryCodeListTest() = runBlocking{
        val countryCodeList = listOf(CountryCode(countryCode = TEST_COUNTRY_CODE, country = TEST_COUNTRY), CountryCode(countryCode = "+123", country = "AI"))
        coEvery { countryCodeRepository.allCountryCodes() } returns countryCodeList
        val result = countryCodeSearchUseCase.getCountryCodeList()
        assertEquals(countryCodeList, result)
    }
}