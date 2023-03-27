package com.tarasovvp.smartblocker.viewmodels

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.tarasovvp.smartblocker.TestUtils.TEST_COUNTRY
import com.tarasovvp.smartblocker.TestUtils.TEST_FILTER
import com.tarasovvp.smartblocker.TestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.TestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.models.database_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.models.entities.*
import com.tarasovvp.smartblocker.domain.usecase.number.create.CreateFilterUseCase
import com.tarasovvp.smartblocker.presentation.main.number.create.CreateFilterViewModel
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
class CreateFilterViewModelTest: BaseViewModelTest<CreateFilterViewModel>() {


    @Mock
    private lateinit var useCase: CreateFilterUseCase

    override fun createViewModel() = CreateFilterViewModel(application, useCase)

    @Test
    fun getCountryCodeWithCountryTest() = runTest {
        val expectedCountryCode = CountryCode(countryCode = "+380", country = "UA")
        Mockito.`when`(useCase.getCountryCodeWithCountry(TEST_COUNTRY))
            .thenReturn(expectedCountryCode)

        viewModel.getCountryCodeWithCountry(TEST_COUNTRY)
        advanceUntilIdle()
        val resultCountry = viewModel.countryCodeLiveData.getOrAwaitValue()
        assertEquals(expectedCountryCode.country, resultCountry?.country)
        assertEquals(expectedCountryCode.countryCode, resultCountry?.countryCode)
    }

    @Test
    fun getCountryCodeWithCodeTest() = runTest {
        val countryCode = 123
        val expectedCountryCode = CountryCode(countryCode = "+380", country = TEST_COUNTRY)
        Mockito.`when`(useCase.getCountryCodeWithCode(countryCode))
            .thenReturn(expectedCountryCode)

        viewModel.getCountryCodeWithCode(countryCode)
        advanceUntilIdle()
        val result = viewModel.countryCodeLiveData.getOrAwaitValue()
        assertEquals(result.country, TEST_COUNTRY)
    }

    @Test
    fun getNumberDataListTest() = runTest {
        val numberDataList = arrayListOf(ContactWithFilter(contact = Contact(number = TEST_NUMBER)), CallWithFilter().apply { call = Call(number = TEST_NUMBER) })
        Mockito.`when`(useCase.getNumberDataList())
            .thenReturn(numberDataList)
        viewModel.getNumberDataList()
        advanceUntilIdle()
        val result = viewModel.numberDataListLiveData.getOrAwaitValue()
        assertEquals(TEST_NUMBER, (result[0] as ContactWithFilter).contact?.number)
    }

    @Test
    fun checkFilterExistTest() = runTest {
        val filterWithCountryCode = FilterWithCountryCode(filter = Filter(filter = TEST_FILTER))
        Mockito.`when`(useCase.checkFilterExist(filterWithCountryCode))
            .thenReturn(filterWithCountryCode)
        viewModel.checkFilterExist(filterWithCountryCode)
        advanceUntilIdle()
        val result = viewModel.existingFilterLiveData.getOrAwaitValue()
        assertEquals(TEST_FILTER, result.filter?.filter)
    }

    @Test
    fun filterNumberDataListTest() = runTest {
        val filterWithCountryCode = FilterWithCountryCode(filter = Filter(filter = TEST_FILTER))
        val numberDataList = arrayListOf(ContactWithFilter(contact = Contact(number = TEST_NUMBER)), CallWithFilter().apply { call = Call(number = TEST_NUMBER) })
        Mockito.`when`(useCase.filterNumberDataList(filterWithCountryCode, numberDataList, 0))
            .thenReturn(numberDataList)
        viewModel.filterNumberDataList(filterWithCountryCode, numberDataList, 0)
        advanceUntilIdle()
        val result = viewModel.filteredNumberDataListLiveData.getOrAwaitValue()
        assertEquals(TEST_NUMBER, (result[0] as ContactWithFilter).contact?.number)
    }

    @Test
    fun createFilterTest() = runTest {
        val filter = Filter(filter = TEST_FILTER)
        Mockito.doAnswer {
            @Suppress("UNCHECKED_CAST")
            val result = it.arguments[1] as () -> Unit
            result.invoke()
        }.`when`(useCase).createFilter(eq(filter), any())
        viewModel.createFilter(filter)
        advanceUntilIdle()
        val result = viewModel.filterActionLiveData.getOrAwaitValue()
        assertEquals(TEST_FILTER, result.filter)
    }

    @Test
    fun updateFilterTest() = runTest {
        val filter = Filter(filter = TEST_FILTER)
        Mockito.doAnswer {
            @Suppress("UNCHECKED_CAST")
            val result = it.arguments[1] as () -> Unit
            result.invoke()
        }.`when`(useCase).updateFilter(eq(filter), any())
        viewModel.updateFilter(filter)
        advanceUntilIdle()
        val result = viewModel.filterActionLiveData.getOrAwaitValue()
        assertEquals(TEST_FILTER, result.filter)
    }

    @Test
    fun deleteFilterTest() = runTest {
        val filter = Filter(filter = TEST_FILTER)
        Mockito.doAnswer {
            @Suppress("UNCHECKED_CAST")
            val result = it.arguments[1] as () -> Unit
            result.invoke()
        }.`when`(useCase).deleteFilter(eq(filter), any())
        viewModel.deleteFilter(filter)
        advanceUntilIdle()
        val result = viewModel.filterActionLiveData.getOrAwaitValue()
        assertEquals(TEST_FILTER, result.filter)
    }
}