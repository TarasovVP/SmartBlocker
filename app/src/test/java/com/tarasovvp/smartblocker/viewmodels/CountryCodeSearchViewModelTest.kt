package com.tarasovvp.smartblocker.viewmodels

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_COUNTRY_CODE
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.UnitTestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.domain.models.entities.CountryCode
import com.tarasovvp.smartblocker.domain.usecase.countrycode_search.CountryCodeSearchUseCase
import com.tarasovvp.smartblocker.presentation.dialogs.country_code_search_dialog.CountryCodeSearchViewModel
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Test

@ExperimentalCoroutinesApi
class CountryCodeSearchViewModelTest: BaseViewModelTest<CountryCodeSearchViewModel>() {

    @MockK
    private lateinit var useCase: CountryCodeSearchUseCase

    override fun createViewModel(): CountryCodeSearchViewModel {
        return CountryCodeSearchViewModel(application, useCase)
    }

    @Test
    fun getCountryCodeList() = runTest {
        val countryCodeList = listOf(CountryCode(countryCode = TEST_COUNTRY_CODE, country = TEST_NUMBER), CountryCode(countryCode = "+123", country = "AI"))
        coEvery { useCase.getCountryCodeList() } returns countryCodeList
        viewModel.getCountryCodeList()
        advanceUntilIdle()
        val result = viewModel.countryCodeListLiveData.getOrAwaitValue()
        assertEquals(TEST_NUMBER, result[0].country)
        assertEquals(TEST_COUNTRY_CODE, result[0].countryCode)
    }
}