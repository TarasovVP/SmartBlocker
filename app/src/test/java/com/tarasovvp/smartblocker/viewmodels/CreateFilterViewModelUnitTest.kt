package com.tarasovvp.smartblocker.viewmodels

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_COUNTRY
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_COUNTRY_CODE
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_FILTER
import com.tarasovvp.smartblocker.UnitTestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithFilteredNumber
import com.tarasovvp.smartblocker.domain.entities.db_entities.*
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.CreateFilterUseCase
import com.tarasovvp.smartblocker.presentation.main.number.create.CreateFilterViewModel
import com.tarasovvp.smartblocker.presentation.mappers.CallWithFilterUIMapper
import com.tarasovvp.smartblocker.presentation.mappers.ContactWithFilterUIMapper
import com.tarasovvp.smartblocker.presentation.mappers.CountryCodeUIMapper
import com.tarasovvp.smartblocker.presentation.mappers.FilterWithFilteredNumberUIMapper
import com.tarasovvp.smartblocker.presentation.ui_models.CountryCodeUIModel
import com.tarasovvp.smartblocker.presentation.ui_models.FilterWithFilteredNumberUIModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Test

@ExperimentalCoroutinesApi
class CreateFilterViewModelUnitTest: BaseViewModelUnitTest<CreateFilterViewModel>() {


    @MockK
    private lateinit var useCase: CreateFilterUseCase

    @MockK
    private lateinit var countryCodeUIMapper: CountryCodeUIMapper

    @MockK
    private lateinit var filterWithFilteredNumberUIMapper: FilterWithFilteredNumberUIMapper

    @MockK
    private lateinit var callWithFilterUIMapper: CallWithFilterUIMapper

    @MockK
    private lateinit var contactWithFilterUIMapper: ContactWithFilterUIMapper

    override fun createViewModel() = CreateFilterViewModel(application, useCase, countryCodeUIMapper, filterWithFilteredNumberUIMapper, callWithFilterUIMapper, contactWithFilterUIMapper)

    @Test
    fun getCountryCodeByCodeTest() = runTest {
        val code = 123
        val countryCode = CountryCode(countryCode = TEST_COUNTRY_CODE, country = TEST_COUNTRY)
        val countryCodeUIModel = CountryCodeUIModel(countryCode = TEST_COUNTRY_CODE, country = TEST_COUNTRY)
        coEvery { useCase.getCountryCodeWithCode(code) } returns countryCode
        every { countryCodeUIMapper.mapToUIModel(countryCode) } returns countryCodeUIModel
        viewModel.getCountryCodeWithCode(code)
        advanceUntilIdle()
        coVerify { useCase.getCountryCodeWithCode(code) }
        verify { countryCodeUIMapper.mapToUIModel(countryCode) }
        assertEquals(countryCodeUIModel, viewModel.countryCodeLiveData.getOrAwaitValue())
    }

    @Test
    fun getNumberDataListTest() = runTest {
        /*val numberDataList = arrayListOf(ContactWithFilter(contact = Contact(number = TEST_NUMBER)), CallWithFilter().apply { call = Call(number = TEST_NUMBER) })
        coEvery { useCase.getNumberDataList() } returns numberDataList
        viewModel.getNumberDataList()
        advanceUntilIdle()
        val result = viewModel.numberDataListLiveDataUIModel.getOrAwaitValue()
        assertEquals(TEST_NUMBER, (result[0] as ContactWithFilter).contact?.number)*/
    }

    @Test
    fun checkFilterExistTest() = runTest {
        val filterValue = TEST_FILTER
        val filter = FilterWithFilteredNumber(filter = Filter(filter = TEST_FILTER))
        val filterUIModel = FilterWithFilteredNumberUIModel(filter = TEST_FILTER)
        coEvery { useCase.getFilter(filterValue) } returns filter
        every { filterWithFilteredNumberUIMapper.mapToUIModel(filter) } returns filterUIModel
        viewModel.checkFilterExist(filterValue)
        advanceUntilIdle()
        coVerify { useCase.getFilter(filterValue) }
        verify { filterWithFilteredNumberUIMapper.mapToUIModel(filter) }
        assertEquals(filterUIModel, viewModel.existingFilterLiveData.getOrAwaitValue())
    }

    @Test
    fun createFilterTest() = runTest {
        val expectedResult = Result.Success<Unit>()
        val filterWithFilteredNumber = FilterWithFilteredNumber(filter = Filter(filter = TEST_FILTER))
        val filterWithFilteredNumberUIModel = FilterWithFilteredNumberUIModel(filter = TEST_FILTER)
        every { application.isNetworkAvailable } returns true
        coEvery { useCase.createFilter(eq(filterWithFilteredNumber.filter ?: Filter()), eq(true), any()) } answers {
            val result = thirdArg<(Result<Unit>) -> Unit>()
            result.invoke(expectedResult)
        }
        every { filterWithFilteredNumberUIMapper.mapFromUIModel(filterWithFilteredNumberUIModel) } returns filterWithFilteredNumber
        viewModel.createFilter(filterWithFilteredNumberUIModel)
        advanceUntilIdle()
        coVerify { useCase.createFilter(eq(filterWithFilteredNumber.filter ?: Filter()), eq(true), any()) }
        verify { filterWithFilteredNumberUIMapper.mapFromUIModel(filterWithFilteredNumberUIModel) }
        assertEquals(filterWithFilteredNumberUIModel, viewModel.filterActionLiveData.getOrAwaitValue())
    }

    @Test
    fun updateFilterTest() = runTest {
        val expectedResult = Result.Success<Unit>()
        val filterWithFilteredNumber = FilterWithFilteredNumber(filter = Filter(filter = TEST_FILTER))
        val filterWithFilteredNumberUIModel = FilterWithFilteredNumberUIModel(filter = TEST_FILTER)
        every { application.isNetworkAvailable } returns true
        coEvery { useCase.updateFilter(eq(filterWithFilteredNumber.filter ?: Filter()), eq(true), any()) } answers {
            val result = thirdArg<(Result<Unit>) -> Unit>()
            result.invoke(expectedResult)
        }
        every { filterWithFilteredNumberUIMapper.mapFromUIModel(filterWithFilteredNumberUIModel) } returns filterWithFilteredNumber
        viewModel.updateFilter(filterWithFilteredNumberUIModel)
        advanceUntilIdle()
        coVerify { useCase.updateFilter(eq(filterWithFilteredNumber.filter ?: Filter()), eq(true), any()) }
        verify { filterWithFilteredNumberUIMapper.mapFromUIModel(filterWithFilteredNumberUIModel) }
        assertEquals(filterWithFilteredNumberUIModel, viewModel.filterActionLiveData.getOrAwaitValue())
    }

    @Test
    fun deleteFilterTest() = runTest {
        val expectedResult = Result.Success<Unit>()
        val filterWithFilteredNumber = FilterWithFilteredNumber(filter = Filter(filter = TEST_FILTER))
        val filterWithFilteredNumberUIModel = FilterWithFilteredNumberUIModel(filter = TEST_FILTER)
        every { application.isNetworkAvailable } returns true
        coEvery { useCase.deleteFilter(eq(filterWithFilteredNumber.filter ?: Filter()), eq(true), any()) } answers {
            val result = thirdArg<(Result<Unit>) -> Unit>()
            result.invoke(expectedResult)
        }
        every { filterWithFilteredNumberUIMapper.mapFromUIModel(filterWithFilteredNumberUIModel) } returns filterWithFilteredNumber
        viewModel.deleteFilter(filterWithFilteredNumberUIModel)
        advanceUntilIdle()
        coVerify { useCase.deleteFilter(eq(filterWithFilteredNumber.filter ?: Filter()), eq(true), any()) }
        verify { filterWithFilteredNumberUIMapper.mapFromUIModel(filterWithFilteredNumberUIModel) }
        assertEquals(filterWithFilteredNumberUIModel, viewModel.filterActionLiveData.getOrAwaitValue())
    }
}