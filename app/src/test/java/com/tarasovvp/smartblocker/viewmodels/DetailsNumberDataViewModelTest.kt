package com.tarasovvp.smartblocker.viewmodels

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.tarasovvp.smartblocker.TestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.models.database_views.FilteredCallWithFilter
import com.tarasovvp.smartblocker.domain.models.entities.CountryCode
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.domain.models.entities.FilteredCall
import com.tarasovvp.smartblocker.domain.usecase.number.details.details_number_data.DetailsNumberDataUseCase
import com.tarasovvp.smartblocker.presentation.main.number.details.details_number_data.DetailsNumberDataViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class DetailsNumberDataViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var application: Application

    @Mock
    private lateinit var detailsNumberDataUseCase: DetailsNumberDataUseCase

    private lateinit var viewModel: DetailsNumberDataViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(StandardTestDispatcher())
        viewModel =
            DetailsNumberDataViewModel(application, detailsNumberDataUseCase)
    }

    @Test
    fun filterListWithNumberTest() = runTest {
        val number = "123"
        val mockFilter = "mockFilter"
        val filterList = listOf(FilterWithCountryCode(filter = Filter(filter = mockFilter)), FilterWithCountryCode(filter = Filter(filter = "mockFilter2")))
        Mockito.`when`(detailsNumberDataUseCase.filterListWithNumber(number))
            .thenReturn(filterList)
        viewModel.filterListWithNumber(number)
        advanceUntilIdle()
        val result = viewModel.filterListLiveData.getOrAwaitValue()
        assertEquals(mockFilter, (result[0] as FilterWithCountryCode).filter?.filter)
    }

    @Test
    fun filteredCallsByNumberTest() = runTest {
        val number = "123"
        val mockNumber = "123"
        val filteredCallList = listOf(FilteredCallWithFilter().apply { call = FilteredCall().apply { this.number = mockNumber } })
        Mockito.`when`(detailsNumberDataUseCase.filteredCallsByNumber(number))
            .thenReturn(filteredCallList)
        viewModel.filteredCallsByNumber(number)
        advanceUntilIdle()
        val result = viewModel.filteredCallListLiveData.getOrAwaitValue()
        assertEquals(mockNumber, (result[0] as FilteredCallWithFilter).call?.number)
    }

    @Test
    fun getCountryCodeTest() = runTest {
        val country = "UA"
        val countryCode = 123
        val expectedCountryCode = CountryCode(countryCode = "+380", country = country)
        Mockito.`when`(detailsNumberDataUseCase.getCountryCode(countryCode))
            .thenReturn(expectedCountryCode)

        viewModel.getCountryCode(countryCode)
        advanceUntilIdle()
        val result = viewModel.countryCodeLiveData.getOrAwaitValue()
        assertEquals(country, result.country)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}