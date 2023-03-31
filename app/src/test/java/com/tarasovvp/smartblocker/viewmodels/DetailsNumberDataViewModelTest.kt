package com.tarasovvp.smartblocker.viewmodels

import com.tarasovvp.smartblocker.TestUtils
import com.tarasovvp.smartblocker.TestUtils.TEST_COUNTRY
import com.tarasovvp.smartblocker.TestUtils.TEST_COUNTRY_CODE
import com.tarasovvp.smartblocker.TestUtils.TEST_FILTER
import com.tarasovvp.smartblocker.TestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.TestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.models.database_views.FilteredCallWithFilter
import com.tarasovvp.smartblocker.domain.models.entities.CountryCode
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.domain.models.entities.FilteredCall
import com.tarasovvp.smartblocker.domain.usecase.number.details.details_number_data.DetailsNumberDataUseCase
import com.tarasovvp.smartblocker.presentation.main.number.details.details_number_data.DetailsNumberDataViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class DetailsNumberDataViewModelTest: BaseViewModelTest<DetailsNumberDataViewModel>() {

    @Mock
    private lateinit var useCase: DetailsNumberDataUseCase

    override fun createViewModel() = DetailsNumberDataViewModel(application, useCase)

    @Test
    fun filterListWithNumberTest() = runTest {
        val filterList = listOf(FilterWithCountryCode(filter = Filter(filter = TEST_FILTER)), FilterWithCountryCode(filter = Filter(filter = "mockFilter2")))
        Mockito.`when`(useCase.filterListWithNumber(TEST_NUMBER))
            .thenReturn(filterList)
        viewModel.filterListWithNumber(TEST_NUMBER)
        advanceUntilIdle()
        val result = viewModel.filterListLiveData.getOrAwaitValue()
        assertEquals(TEST_FILTER, (result[0] as FilterWithCountryCode).filter?.filter)
    }

    @Test
    fun filteredCallsByNumberTest() = runTest {
        val filteredCallList = listOf(FilteredCallWithFilter().apply { call = FilteredCall().apply { this.number = TEST_NUMBER } })
        Mockito.`when`(useCase.filteredCallsByNumber(TEST_NUMBER))
            .thenReturn(filteredCallList)
        viewModel.filteredCallsByNumber(TEST_NUMBER)
        advanceUntilIdle()
        val result = viewModel.filteredCallListLiveData.getOrAwaitValue()
        assertEquals(TEST_NUMBER, (result[0] as FilteredCallWithFilter).call?.number)
    }

    @Test
    fun getCountryCodeTest() = runTest {
        val countryCode = 123
        val expectedCountryCode = CountryCode(countryCode = TEST_COUNTRY_CODE, country = TEST_COUNTRY)
        Mockito.`when`(useCase.getCountryCode(countryCode))
            .thenReturn(expectedCountryCode)

        viewModel.getCountryCode(countryCode)
        advanceUntilIdle()
        val result = viewModel.countryCodeLiveData.getOrAwaitValue()
        assertEquals(TEST_COUNTRY, result.country)
    }
}