package com.tarasovvp.smartblocker.viewmodels

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.tarasovvp.smartblocker.TestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.domain.usecase.number.list.list_filter.ListFilterUseCase
import com.tarasovvp.smartblocker.presentation.main.number.list.list_filter.ListFilterViewModel
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
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
class ListFilterViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var application: Application

    @Mock
    private lateinit var listFilterUseCase: ListFilterUseCase

    private lateinit var viewModel: ListFilterViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(StandardTestDispatcher())
        viewModel =
            ListFilterViewModel(application, listFilterUseCase)
    }

    @Test
    fun getFilterListTest() = runTest {
        val mockFilter = "mockFilter"
        val filterList = listOf(FilterWithCountryCode(filter = Filter(filter = mockFilter)), FilterWithCountryCode(filter = Filter(filter = "mockFilter2")))
        Mockito.`when`(listFilterUseCase.getFilterList(true))
            .thenReturn(filterList)
        viewModel.getFilterList(isBlackList = true, refreshing = false)
        advanceUntilIdle()
        val result = viewModel.filterListLiveData.getOrAwaitValue()
        assertEquals(mockFilter, result?.get(0)?.filter?.filter)
    }

    @Test
    fun getHashMapFromFilterListTest() = runTest {
        val mockFilter = "mockFilter"
        val filterList = listOf(FilterWithCountryCode(filter = Filter(filter = mockFilter)), FilterWithCountryCode(filter = Filter(filter = "mockFilter2")))
        val filterMap = mapOf(String.EMPTY to filterList)
        Mockito.`when`(listFilterUseCase.getHashMapFromFilterList(filterList))
            .thenReturn(filterMap)
        viewModel.getHashMapFromFilterList(filterList, false)
        advanceUntilIdle()
        val result = viewModel.filterHashMapLiveData.getOrAwaitValue()
        assertEquals(mockFilter, result?.get(String.EMPTY)?.get(0)?.filter?.filter)
    }

    @Test
    fun deleteFilterListTest() = runTest {
        val filterList = listOf(Filter())
        Mockito.doAnswer {
            val result = it.arguments[1] as () -> Unit
            result.invoke()
        }.`when`(listFilterUseCase).deleteFilterList(eq(filterList), any())
        viewModel.deleteFilterList(filterList)
        advanceUntilIdle()
        val result = viewModel.successDeleteFilterLiveData.getOrAwaitValue()
        assertEquals(result, viewModel.successDeleteFilterLiveData.value)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}