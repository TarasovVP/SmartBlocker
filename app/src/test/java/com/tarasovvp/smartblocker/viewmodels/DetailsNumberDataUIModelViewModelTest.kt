package com.tarasovvp.smartblocker.viewmodels

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_COUNTRY
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_COUNTRY_CODE
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_FILTER
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.UnitTestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithFilteredNumbers
import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.entities.db_entities.FilteredCall
import com.tarasovvp.smartblocker.domain.usecases.DetailsNumberDataUseCase
import com.tarasovvp.smartblocker.presentation.main.number.details.details_number_data.DetailsNumberDataViewModel
import com.tarasovvp.smartblocker.presentation.mappers.CallWithFilterUIMapper
import com.tarasovvp.smartblocker.presentation.mappers.ContactWithFilterUIMapper
import com.tarasovvp.smartblocker.presentation.mappers.CountryCodeUIMapper
import com.tarasovvp.smartblocker.presentation.mappers.FilterWithFilteredNumberUIMapper
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.Test

@ExperimentalCoroutinesApi
class DetailsNumberDataUIModelViewModelTest: BaseViewModelTest<DetailsNumberDataViewModel>() {

    @MockK
    private lateinit var useCase: DetailsNumberDataUseCase

    @MockK
    private lateinit var filterWithFilteredNumberUIMapper: FilterWithFilteredNumberUIMapper

    @MockK
    private lateinit var callWithFilterUIMapper: CallWithFilterUIMapper

    @MockK
    private lateinit var countryCodeUIMapper: CountryCodeUIMapper

    override fun createViewModel() = DetailsNumberDataViewModel(application, useCase, filterWithFilteredNumberUIMapper, callWithFilterUIMapper, countryCodeUIMapper)

    @Test
    fun filterListWithNumberTest() = runTest {
        val filterList = listOf(FilterWithFilteredNumbers(filter = Filter(filter = TEST_FILTER)), FilterWithFilteredNumbers(filter = Filter(filter = "mockFilter2")))
        coEvery { useCase.allFilterWithFilteredNumbersByNumber(TEST_NUMBER) } returns filterList
        viewModel.filterListWithNumber(TEST_NUMBER)
        advanceUntilIdle()
        val result = viewModel.filterListLiveData.getOrAwaitValue()
        assertEquals(TEST_FILTER, (result[0] as FilterWithFilteredNumbers).filter?.filter)
    }

    @Test
    fun filteredCallsByNumberTest() = runTest {
        val filteredCallList = listOf(FilteredCallWithFilter().apply { call = FilteredCall().apply { this.number = TEST_NUMBER } })
        coEvery { useCase.allFilteredCallsByNumber(TEST_NUMBER) } returns filteredCallList
        viewModel.filteredCallsByNumber(TEST_NUMBER)
        advanceUntilIdle()
        val result = viewModel.filteredCallListLiveData.getOrAwaitValue()
        assertEquals(TEST_NUMBER, (result[0] as FilteredCallWithFilter).call?.number)
    }

    @Test
    fun getCountryCodeTest() = runTest {
        val countryCode = 123
        val expectedCountryCode = CountryCode(countryCode = TEST_COUNTRY_CODE, country = TEST_COUNTRY)
        coEvery { useCase.getCountryCodeByCode(countryCode) } returns expectedCountryCode
        viewModel.getCountryCode(phoneNumber.countryCode, countryCode)
        advanceUntilIdle()
        val result = viewModel.countryCodeLiveData.getOrAwaitValue()
        assertEquals(TEST_COUNTRY, result.country)
    }

    @Test
    fun getBlockHiddenTest() = runTest {
        val blockHidden = true
        coEvery { useCase.getBlockHidden() } returns flowOf(blockHidden)
        viewModel.getBlockHidden()
        advanceUntilIdle()
        coVerify { useCase.getBlockHidden() }
        assertEquals(blockHidden, viewModel.blockHiddenLiveData.value)
    }
}