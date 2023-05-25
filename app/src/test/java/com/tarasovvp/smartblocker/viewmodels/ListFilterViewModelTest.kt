package com.tarasovvp.smartblocker.viewmodels

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_FILTER
import com.tarasovvp.smartblocker.domain.enums.NumberDataFiltering
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithFilteredNumbers
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.ListFilterUseCase
import com.tarasovvp.smartblocker.presentation.main.number.list.list_filter.ListFilterViewModel
import com.tarasovvp.smartblocker.presentation.mappers.FilterWithFilteredNumberUIMapper
import com.tarasovvp.smartblocker.presentation.ui_models.FilterWithFilteredNumberUIModel
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
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
class ListFilterViewModelTest: BaseViewModelTest<ListFilterViewModel>() {

    @MockK
    private lateinit var useCase: ListFilterUseCase

    @MockK
    private lateinit var filterWithFilteredNumberUIMapper: FilterWithFilteredNumberUIMapper

    override fun createViewModel() = ListFilterViewModel(application, useCase, filterWithFilteredNumberUIMapper)

    @Test
    fun getFilterListTest() = runTest {
        val filterList = listOf(FilterWithFilteredNumbers(filter = Filter(filter = TEST_FILTER)), FilterWithFilteredNumbers(filter = Filter(filter = "mockFilter2")))
        val filterUIModelList = listOf(FilterWithFilteredNumberUIModel(filter = TEST_FILTER), FilterWithFilteredNumberUIModel(filter = "mockFilter2"))
        coEvery { useCase.allFilterWithFilteredNumbersByType(true) } returns filterList
        every { filterWithFilteredNumberUIMapper.mapToUIModelList(filterList) } returns filterUIModelList
        viewModel.getFilterList(isBlackList = true, refreshing = false)
        advanceUntilIdle()
        coVerify { useCase.allFilterWithFilteredNumbersByType(true) }
        verify { filterWithFilteredNumberUIMapper.mapToUIModelList(filterList) }
        assertEquals(filterUIModelList, viewModel.filterListLiveData.value)
    }

    @Test
    fun getFilteredFilterListTest() = runTest {
        val numberDataFilters = arrayListOf(NumberDataFiltering.FILTER_CONDITION_CONTAIN_FILTERING.ordinal)
        val filterList = listOf(FilterWithFilteredNumbers(filter = Filter(filter = TEST_FILTER)), FilterWithFilteredNumbers(filter = Filter(filter = "mockFilter2")))
        val filterUIModelList = listOf(FilterWithFilteredNumberUIModel(filter = TEST_FILTER), FilterWithFilteredNumberUIModel(filter = "mockFilter2"))
        coEvery { useCase.getFilteredFilterList(filterList, String.EMPTY, numberDataFilters) } returns filterList
        every { filterWithFilteredNumberUIMapper.mapToUIModelList(filterList) } returns filterUIModelList
        every { filterWithFilteredNumberUIMapper.mapFromUIModelList(filterUIModelList) } returns filterList
        viewModel.getFilteredFilterList(filterUIModelList, String.EMPTY, numberDataFilters)
        advanceUntilIdle()
        coVerify { useCase.getFilteredFilterList(filterList, String.EMPTY, numberDataFilters) }
        verify { filterWithFilteredNumberUIMapper.mapToUIModelList(filterList) }
        verify { filterWithFilteredNumberUIMapper.mapFromUIModelList(filterUIModelList) }
        assertEquals(filterUIModelList, viewModel.filteredFilterListLiveData.value)
    }

    @Test
    fun getHashMapFromFilterListTest() {
        val filterUIModelList = listOf(FilterWithFilteredNumberUIModel(filter = TEST_FILTER), FilterWithFilteredNumberUIModel(filter = "mockFilter2"))
        val filterMap = mapOf(String.EMPTY to filterUIModelList)
        viewModel.getHashMapFromFilterList(filterUIModelList, true)
        assertEquals(filterMap, viewModel.filterHashMapLiveData.value)
    }

    @Test
    fun deleteFilterListTest() = runTest {
        val expectedResult = Result.Success<Unit>()
        val filterList = listOf(FilterWithFilteredNumbers(filter = Filter(filter = TEST_FILTER)), FilterWithFilteredNumbers(filter = Filter(filter = "mockFilter2")))
        val filterUIModelList = listOf(FilterWithFilteredNumberUIModel(filter = TEST_FILTER), FilterWithFilteredNumberUIModel(filter = "mockFilter2"))
        val filterIdList = filterList.mapNotNull { it.filter }
        every { application.isNetworkAvailable } returns true
        every { filterWithFilteredNumberUIMapper.mapFromUIModelList(filterUIModelList) } returns filterList
        coEvery { useCase.deleteFilterList(eq(filterIdList), any(), any()) } answers {
            val result = thirdArg<(Result<Unit>) -> Unit>()
            result.invoke(expectedResult)
        }
        viewModel.deleteFilterList(filterUIModelList)
        advanceUntilIdle()
        coVerify { useCase.deleteFilterList(eq(filterIdList), any(), any()) }
        verify { filterWithFilteredNumberUIMapper.mapFromUIModelList(filterUIModelList) }
        assertEquals(true, viewModel.successDeleteFilterLiveData.value)
    }
}