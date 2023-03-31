package com.tarasovvp.smartblocker.viewmodels

import com.tarasovvp.smartblocker.TestUtils.TEST_COUNTRY_CODE
import com.tarasovvp.smartblocker.TestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.TestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.domain.models.entities.CountryCode
import com.tarasovvp.smartblocker.domain.usecase.countrycode_search.CountryCodeSearchUseCase
import com.tarasovvp.smartblocker.presentation.dialogs.country_code_search_dialog.CountryCodeSearchViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import com.tarasovvp.smartblocker.TestUtils as TestUtils1

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class CountryCodeSearchViewModelTest: BaseViewModelTest<CountryCodeSearchViewModel>() {

    @Mock
    private lateinit var useCase: CountryCodeSearchUseCase

    override fun createViewModel(): CountryCodeSearchViewModel {
        return CountryCodeSearchViewModel(application, useCase)
    }

    @Test
    fun getCountryCodeList() = runTest {
        val countryCodeList = listOf(CountryCode(countryCode = TEST_COUNTRY_CODE, country = TEST_NUMBER), CountryCode(countryCode = "+123", country = "AI"))
        Mockito.`when`(useCase.getCountryCodeList())
            .thenReturn(countryCodeList)

        viewModel.getCountryCodeList()
        advanceUntilIdle()
        val result = viewModel.countryCodeListLiveData.getOrAwaitValue()
        assertEquals(TEST_NUMBER, result[0].country)
        assertEquals(TEST_COUNTRY_CODE, result[0].countryCode)
    }
}