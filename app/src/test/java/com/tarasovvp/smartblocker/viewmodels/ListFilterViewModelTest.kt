package com.tarasovvp.smartblocker.viewmodels

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.tarasovvp.smartblocker.TestUtils.TEST_FILTER
import com.tarasovvp.smartblocker.TestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.domain.enums.NumberDataFiltering
import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.models.entities.CallWithFilter
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.domain.models.entities.FilteredCall
import com.tarasovvp.smartblocker.domain.models.entities.LogCall
import com.tarasovvp.smartblocker.domain.usecase.number.list.list_filter.ListFilterUseCase
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.presentation.main.number.list.list_filter.ListFilterViewModel
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.utils.extensions.isTrue
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
class ListFilterViewModelTest: BaseViewModelTest<ListFilterViewModel>() {

    @Mock
    private lateinit var useCase: ListFilterUseCase
    override fun createViewModel() = ListFilterViewModel(application, useCase)

    @Test
    fun getFilterListTest() = runTest {
        val filterList = listOf(FilterWithCountryCode(filter = Filter(filter = TEST_FILTER)), FilterWithCountryCode(filter = Filter(filter = "mockFilter2")))
        Mockito.`when`(useCase.getFilterList(true))
            .thenReturn(filterList)
        viewModel.getFilterList(isBlackList = true, refreshing = false)
        advanceUntilIdle()
        val result = viewModel.filterListLiveData.getOrAwaitValue()
        assertEquals(TEST_FILTER, result?.get(0)?.filter?.filter)
    }

    @Test
    fun getFilteredFilterListTest() = runTest {
        val filterList = listOf(FilterWithCountryCode(filter = Filter(filter = TEST_FILTER)), FilterWithCountryCode(filter = Filter(filter = "mockFilter2")))
        Mockito.`when`(useCase.getFilteredFilterList(filterList, String.EMPTY, arrayListOf(
            NumberDataFiltering.FILTER_CONDITION_CONTAIN_FILTERING.ordinal)))
            .thenReturn(filterList.filter { it.filter?.filter == TEST_FILTER })
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
        Mockito.`when`(useCase.getHashMapFromFilterList(filterList))
            .thenReturn(filterMap)
        viewModel.getHashMapFromFilterList(filterList, false)
        advanceUntilIdle()
        val result = viewModel.filterHashMapLiveData.getOrAwaitValue()
        assertEquals(TEST_FILTER, result?.get(String.EMPTY)?.get(0)?.filter?.filter)
    }

    @Test
    fun deleteFilterListTest() = runTest {
        val filterList = listOf(Filter())
        Mockito.doAnswer {
            @Suppress("UNCHECKED_CAST")
            val result = it.arguments[1] as () -> Unit
            result.invoke()
        }.`when`(useCase).deleteFilterList(eq(filterList), any())
        viewModel.deleteFilterList(filterList)
        advanceUntilIdle()
        val result = viewModel.successDeleteFilterLiveData.getOrAwaitValue()
        assertEquals(result, viewModel.successDeleteFilterLiveData.value)
    }
}