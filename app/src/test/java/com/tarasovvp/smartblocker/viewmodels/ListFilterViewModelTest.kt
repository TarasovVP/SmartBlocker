package com.tarasovvp.smartblocker.viewmodels

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_FILTER
import com.tarasovvp.smartblocker.UnitTestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.domain.enums.NumberDataFiltering
import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.domain.usecase.number.list.list_filter.ListFilterUseCase
import com.tarasovvp.smartblocker.presentation.main.number.list.list_filter.ListFilterViewModel
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Test

@ExperimentalCoroutinesApi
class ListFilterViewModelTest: BaseViewModelTest<ListFilterViewModel>() {

    @MockK
    private lateinit var useCase: ListFilterUseCase
    override fun createViewModel() = ListFilterViewModel(application, useCase)

    @Test
    fun getFilterListTest() = runTest {
        val filterList = listOf(FilterWithCountryCode(filter = Filter(filter = TEST_FILTER)), FilterWithCountryCode(filter = Filter(filter = "mockFilter2")))
        coEvery { useCase.getFilterList(true) } returns filterList
        viewModel.getFilterList(isBlackList = true, refreshing = false)
        advanceUntilIdle()
        val result = viewModel.filterListLiveData.getOrAwaitValue()
        assertEquals(TEST_FILTER, result?.get(0)?.filter?.filter)
    }

    @Test
    fun getFilteredFilterListTest() = runTest {
        val filterList = listOf(FilterWithCountryCode(filter = Filter(filter = TEST_FILTER)), FilterWithCountryCode(filter = Filter(filter = "mockFilter2")))
        coEvery { useCase.getFilteredFilterList(filterList, String.EMPTY, arrayListOf(
            NumberDataFiltering.FILTER_CONDITION_CONTAIN_FILTERING.ordinal)) } returns filterList.filter { it.filter?.filter == TEST_FILTER }
        viewModel.getFilteredFilterList(filterList, String.EMPTY, arrayListOf(
            NumberDataFiltering.FILTER_CONDITION_CONTAIN_FILTERING.ordinal))
        advanceUntilIdle()
        val result = viewModel.filteredFilterListLiveData.getOrAwaitValue()
        assertEquals(filterList.filter { it.filter?.filter == TEST_FILTER }, result)
    }

    @Test
    fun getHashMapFromFilterListTest() = runTest {
        val filterList = listOf(FilterWithCountryCode(filter = Filter(filter = TEST_FILTER)), FilterWithCountryCode(filter = Filter(filter = "mockFilter2")))
        val filterMap = mapOf(String.EMPTY to filterList)
        coEvery { useCase.getHashMapFromFilterList(filterList) } returns filterMap
        viewModel.getHashMapFromFilterList(filterList, false)
        advanceUntilIdle()
        val result = viewModel.filterHashMapLiveData.getOrAwaitValue()
        assertEquals(TEST_FILTER, result?.get(String.EMPTY)?.get(0)?.filter?.filter)
    }

    @Test
    fun deleteFilterListTest() = runTest {
        val filterList = listOf(Filter())
        coEvery { useCase.deleteFilterList(eq(filterList), any(), any()) } answers {
            val result = thirdArg<() -> Unit>()
            result.invoke()
        }
        viewModel.deleteFilterList(filterList)
        advanceUntilIdle()
        val result = viewModel.successDeleteFilterLiveData.getOrAwaitValue()
        assertEquals(result, viewModel.successDeleteFilterLiveData.value)
    }
}