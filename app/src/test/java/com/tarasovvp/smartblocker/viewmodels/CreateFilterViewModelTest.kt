package com.tarasovvp.smartblocker.viewmodels

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_COUNTRY
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_COUNTRY_CODE
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_FILTER
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.UnitTestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.models.database_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.models.entities.*
import com.tarasovvp.smartblocker.domain.usecase.number.create.CreateFilterUseCase
import com.tarasovvp.smartblocker.presentation.main.number.create.CreateFilterViewModel
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Test

@ExperimentalCoroutinesApi
class CreateFilterViewModelTest: BaseViewModelTest<CreateFilterViewModel>() {


    @MockK
    private lateinit var useCase: CreateFilterUseCase

    override fun createViewModel() = CreateFilterViewModel(application, useCase)

    @Test
    fun getCountryCodeWithCodeTest() = runTest {
        val countryCode = 123
        val expectedCountryCode = CountryCode(countryCode = TEST_COUNTRY_CODE, country = TEST_COUNTRY)
        coEvery { useCase.getCountryCodeWithCode(countryCode) } returns expectedCountryCode
        viewModel.getCountryCodeWithCode(countryCode)
        advanceUntilIdle()
        val result = viewModel.countryCodeLiveData.getOrAwaitValue()
        assertEquals(result.country, TEST_COUNTRY)
    }

    @Test
    fun getNumberDataListTest() = runTest {
        val numberDataList = arrayListOf(ContactWithFilter(contact = Contact(number = TEST_NUMBER)), CallWithFilter().apply { call = Call(number = TEST_NUMBER) })
        coEvery { useCase.getNumberDataList() } returns numberDataList
        viewModel.getNumberDataList()
        advanceUntilIdle()
        val result = viewModel.numberDataListLiveData.getOrAwaitValue()
        assertEquals(TEST_NUMBER, (result[0] as ContactWithFilter).contact?.number)
    }

    @Test
    fun checkFilterExistTest() = runTest {
        val filterWithCountryCode = FilterWithCountryCode(filter = Filter(filter = TEST_FILTER))
        coEvery { useCase.checkFilterExist(filterWithCountryCode) } returns filterWithCountryCode
        viewModel.checkFilterExist(filterWithCountryCode)
        advanceUntilIdle()
        val result = viewModel.existingFilterLiveData.getOrAwaitValue()
        assertEquals(TEST_FILTER, result.filter?.filter)
    }

    @Test
    fun filterNumberDataListTest() = runTest {
        //TODO
        /*val filterWithCountryCode = FilterWithCountryCode(filter = Filter(filter = TEST_FILTER))
        val numberDataList = arrayListOf(ContactWithFilter(contact = Contact(number = TEST_NUMBER)), CallWithFilter().apply { call = Call(number = TEST_NUMBER) })
        Mockito.`when`(useCase.filterNumberDataList(filterWithCountryCode, numberDataList, 0))
            .thenReturn(numberDataList)
        viewModel.filterNumberDataList(filterWithCountryCode, numberDataList, 0)
        advanceUntilIdle()
        val result = viewModel.filteredNumberDataListLiveData.getOrAwaitValue()
        assertEquals(numberDataList, result)*/
    }

    @Test
    fun createFilterTest() = runTest {
        val filter = Filter(filter = TEST_FILTER)
        coEvery { useCase.createFilter(eq(filter), any(), any()) } answers {
            val result = thirdArg<() -> Unit>()
            result.invoke()
        }
        viewModel.createFilter(filter)
        advanceUntilIdle()
        val result = viewModel.filterActionLiveData.getOrAwaitValue()
        assertEquals(TEST_FILTER, result.filter)
    }

    @Test
    fun updateFilterTest() = runTest {
        val filter = Filter(filter = TEST_FILTER)
        coEvery { useCase.updateFilter(eq(filter), any(), any()) } answers {
            val result = thirdArg<() -> Unit>()
            result.invoke()
        }
        viewModel.updateFilter(filter)
        advanceUntilIdle()
        val result = viewModel.filterActionLiveData.getOrAwaitValue()
        assertEquals(TEST_FILTER, result.filter)
    }

    @Test
    fun deleteFilterTest() = runTest {
        val filter = Filter(filter = TEST_FILTER)
        coEvery { useCase.deleteFilter(eq(filter), any(), any()) } answers {
            val result = thirdArg<() -> Unit>()
            result.invoke()
        }
        viewModel.deleteFilter(filter)
        advanceUntilIdle()
        val result = viewModel.filterActionLiveData.getOrAwaitValue()
        assertEquals(TEST_FILTER, result.filter)
    }
}