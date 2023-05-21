package com.tarasovvp.smartblocker.viewmodels

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_COUNTRY
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_COUNTRY_CODE
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_FILTER
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithFilteredNumbers
import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.entities.db_entities.FilteredCall
import com.tarasovvp.smartblocker.domain.entities.db_views.CallWithFilter
import com.tarasovvp.smartblocker.domain.usecases.DetailsNumberDataUseCase
import com.tarasovvp.smartblocker.presentation.main.number.details.details_number_data.DetailsNumberDataViewModel
import com.tarasovvp.smartblocker.presentation.mappers.CallWithFilterUIMapper
import com.tarasovvp.smartblocker.presentation.mappers.CountryCodeUIMapper
import com.tarasovvp.smartblocker.presentation.mappers.FilterWithFilteredNumberUIMapper
import com.tarasovvp.smartblocker.presentation.ui_models.CallWithFilterUIModel
import com.tarasovvp.smartblocker.presentation.ui_models.CountryCodeUIModel
import com.tarasovvp.smartblocker.presentation.ui_models.FilterWithCountryCodeUIModel
import com.tarasovvp.smartblocker.presentation.ui_models.FilterWithFilteredNumberUIModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
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
        val filterUIModelList = listOf(FilterWithFilteredNumberUIModel(filter = TEST_FILTER), FilterWithFilteredNumberUIModel(filter = "mockFilter2"))
        coEvery { useCase.allFilterWithFilteredNumbersByNumber(TEST_NUMBER) } returns filterList
        every { filterWithFilteredNumberUIMapper.mapToUIModelList(filterList) } returns filterUIModelList
        viewModel.filterListWithNumber(TEST_NUMBER)
        advanceUntilIdle()
        coVerify { useCase.allFilterWithFilteredNumbersByNumber(TEST_NUMBER) }
        verify { filterWithFilteredNumberUIMapper.mapToUIModelList(filterList) }
        assertEquals(filterUIModelList, viewModel.filterListLiveData.value)
    }

    @Test
    fun filteredCallsByNumberTest() = runTest {
        val filteredCallList = listOf(CallWithFilter().apply { call = FilteredCall().apply { this.number = TEST_NUMBER } })
        val filteredCallUIModelList = listOf(CallWithFilterUIModel(number = TEST_NUMBER))
        coEvery { useCase.allFilteredCallsByNumber(TEST_FILTER) } returns filteredCallList
        every { callWithFilterUIMapper.mapToUIModelList(filteredCallList) } returns filteredCallUIModelList
        viewModel.filteredCallsByNumber(TEST_FILTER)
        advanceUntilIdle()
        coVerify { useCase.allFilteredCallsByNumber(TEST_FILTER) }
        verify { callWithFilterUIMapper.mapToUIModelList(filteredCallList) }
        assertEquals(filteredCallUIModelList, viewModel.filteredCallListLiveData.value)
    }

    @Test
    fun getCountryCodeTest() = runTest {
        val code = 123
        val filterWithCountryCodeUIModel = FilterWithCountryCodeUIModel(filterWithFilteredNumberUIModel = FilterWithFilteredNumberUIModel(filter = TEST_FILTER))
        val countryCode = CountryCode(countryCode = TEST_COUNTRY_CODE, country = TEST_COUNTRY)
        val countryCodeUIModel = CountryCodeUIModel(countryCode = TEST_COUNTRY_CODE, country = TEST_COUNTRY)
        coEvery { useCase.getCountryCodeByCode(code) } returns countryCode
        every { countryCodeUIMapper.mapToUIModel(countryCode) } returns countryCodeUIModel
        viewModel.getCountryCode(code, filterWithCountryCodeUIModel)
        advanceUntilIdle()
        coVerify { useCase.getCountryCodeByCode(code) }
        verify { countryCodeUIMapper.mapToUIModel(countryCode) }
        assertEquals(countryCodeUIModel, viewModel.countryCodeLiveData.value)
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