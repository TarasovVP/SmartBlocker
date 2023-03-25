package com.tarasovvp.smartblocker.viewmodels

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.tarasovvp.smartblocker.TestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.models.database_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.models.entities.*
import com.tarasovvp.smartblocker.domain.usecase.number.create.CreateFilterUseCase
import com.tarasovvp.smartblocker.presentation.main.number.create.CreateFilterViewModel
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
class CreateFilterViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var application: Application

    @Mock
    private lateinit var createFilterUseCase: CreateFilterUseCase

    private lateinit var viewModel: CreateFilterViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(StandardTestDispatcher())
        viewModel =
            CreateFilterViewModel(application, createFilterUseCase)
    }

    @Test
    fun getCountryCodeWithCountryTest() = runTest {
        val queryCountry = "ua"
        val expectedCountryCode = CountryCode(countryCode = "+380", country = "UA")
        Mockito.`when`(createFilterUseCase.getCountryCodeWithCountry(queryCountry))
            .thenReturn(expectedCountryCode)

        viewModel.getCountryCodeWithCountry(queryCountry)
        advanceUntilIdle()
        val resultCountry = viewModel.countryCodeLiveData.getOrAwaitValue()
        assertEquals(expectedCountryCode.country, resultCountry?.country)
        assertEquals(expectedCountryCode.countryCode, resultCountry?.countryCode)
    }

    @Test
    fun getCountryCodeWithCodeTest() = runTest {
        val country = "UA"
        val countryCode = 123
        val expectedCountryCode = CountryCode(countryCode = "+380", country = country)
        Mockito.`when`(createFilterUseCase.getCountryCodeWithCode(countryCode))
            .thenReturn(expectedCountryCode)

        viewModel.getCountryCodeWithCode(countryCode)
        advanceUntilIdle()
        val result = viewModel.countryCodeLiveData.getOrAwaitValue()
        assertEquals(result.country, country)
    }

    @Test
    fun getNumberDataListTest() = runTest {
        val mockNumber = "123"
        val numberDataList = arrayListOf(ContactWithFilter(contact = Contact(number = mockNumber)), CallWithFilter().apply { call = Call(number = mockNumber) })
        Mockito.`when`(createFilterUseCase.getNumberDataList())
            .thenReturn(numberDataList)
        viewModel.getNumberDataList()
        advanceUntilIdle()
        val result = viewModel.numberDataListLiveData.getOrAwaitValue()
        assertEquals(mockNumber, (result[0] as ContactWithFilter).contact?.number)
    }

    @Test
    fun checkFilterExistTest() = runTest {
        val mockFilter = "mockFilter"
        val filterWithCountryCode = FilterWithCountryCode(filter = Filter(filter = mockFilter))
        Mockito.`when`(createFilterUseCase.checkFilterExist(filterWithCountryCode))
            .thenReturn(filterWithCountryCode)
        viewModel.checkFilterExist(filterWithCountryCode)
        advanceUntilIdle()
        val result = viewModel.existingFilterLiveData.getOrAwaitValue()
        assertEquals(mockFilter, result.filter?.filter)
    }

    @Test
    fun filterNumberDataListTest() = runTest {
        val mockFilter = "mockFilter"
        val mockNumber = "123"
        val filterWithCountryCode = FilterWithCountryCode(filter = Filter(filter = mockFilter))
        val numberDataList = arrayListOf(ContactWithFilter(contact = Contact(number = mockNumber)), CallWithFilter().apply { call = Call(number = mockNumber) })
        Mockito.`when`(createFilterUseCase.filterNumberDataList(filterWithCountryCode, numberDataList, 0))
            .thenReturn(numberDataList)
        viewModel.filterNumberDataList(filterWithCountryCode, numberDataList, 0)
        advanceUntilIdle()
        val result = viewModel.filteredNumberDataListLiveData.getOrAwaitValue()
        assertEquals(mockNumber, (result[0] as ContactWithFilter).contact?.number)
    }

    @Test
    fun createFilterTest() = runTest {
        val mockFilter = "mockFilter"
        val filter = Filter(filter = mockFilter)
        Mockito.doAnswer {
            val result = it.arguments[1] as () -> Unit
            result.invoke()
        }.`when`(createFilterUseCase).createFilter(eq(filter), any())
        viewModel.createFilter(filter)
        advanceUntilIdle()
        val result = viewModel.filterActionLiveData.getOrAwaitValue()
        assertEquals(mockFilter, result.filter)
    }

    @Test
    fun updateFilterTest() = runTest {
        val mockFilter = "mockFilter"
        val filter = Filter(filter = mockFilter)
        Mockito.doAnswer {
            val result = it.arguments[1] as () -> Unit
            result.invoke()
        }.`when`(createFilterUseCase).updateFilter(eq(filter), any())
        viewModel.updateFilter(filter)
        advanceUntilIdle()
        val result = viewModel.filterActionLiveData.getOrAwaitValue()
        assertEquals(mockFilter, result.filter)
    }

    @Test
    fun deleteFilterTest() = runTest {
        val mockFilter = "mockFilter"
        val filter = Filter(filter = mockFilter)
        Mockito.doAnswer {
            val result = it.arguments[1] as () -> Unit
            result.invoke()
        }.`when`(createFilterUseCase).deleteFilter(eq(filter), any())
        viewModel.deleteFilter(filter)
        advanceUntilIdle()
        val result = viewModel.filterActionLiveData.getOrAwaitValue()
        assertEquals(mockFilter, result.filter)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

}