package com.tarasovvp.smartblocker.viewmodels

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.tarasovvp.smartblocker.TestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.domain.models.database_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.models.database_views.FilteredCallWithFilter
import com.tarasovvp.smartblocker.domain.models.entities.*
import com.tarasovvp.smartblocker.domain.usecase.number.details.details_filter.DetailsFilterUseCase
import com.tarasovvp.smartblocker.presentation.main.number.details.details_filter.DetailsFilterViewModel
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
class DetailsFilterViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var application: Application

    @Mock
    private lateinit var detailsFilterUseCase: DetailsFilterUseCase

    private lateinit var viewModel: DetailsFilterViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(StandardTestDispatcher())
        viewModel =
            DetailsFilterViewModel(application, detailsFilterUseCase)
    }

    @Test
    fun getQueryContactCallListTest() = runTest {
        val mockFilter = "mockFilter"
        val mockNumber = "123"
        val filter = Filter(filter = mockFilter)
        val numberDataList = arrayListOf(ContactWithFilter(contact = Contact(number = mockNumber)), CallWithFilter().apply { call = Call(number = mockNumber) })
        Mockito.`when`(detailsFilterUseCase.getQueryContactCallList(filter))
            .thenReturn(numberDataList)
        viewModel.getQueryContactCallList(filter)
        advanceUntilIdle()
        val result = viewModel.numberDataListLiveData.getOrAwaitValue()
        assertEquals(mockNumber, (result[0] as ContactWithFilter).contact?.number)
    }

    @Test
    fun filteredNumberDataListTest() = runTest {
        val mockFilter = "mockFilter"
        val mockNumber = "123"
        val filter = Filter(filter = mockFilter)
        val numberDataList = arrayListOf(ContactWithFilter(contact = Contact(number = mockNumber)), CallWithFilter().apply { call = Call(number = mockNumber) })
        Mockito.`when`(detailsFilterUseCase.filteredNumberDataList(filter, numberDataList, 0))
            .thenReturn(numberDataList)
        viewModel.filteredNumberDataList(filter, numberDataList, 0)
        advanceUntilIdle()
        val result = viewModel.filteredNumberDataListLiveData.getOrAwaitValue()
        assertEquals(mockNumber, (result[0] as ContactWithFilter).contact?.number)
    }

    @Test
    fun filteredCallsByFilterTest() = runTest {
        val mockFilter = "mockFilter"
        val mockNumber = "123"
        val filteredCallList = listOf(FilteredCallWithFilter().apply { call = FilteredCall().apply { this.number = mockNumber } })
        Mockito.`when`(detailsFilterUseCase.filteredCallsByFilter(mockFilter))
            .thenReturn(filteredCallList)
        viewModel.filteredCallsByFilter(mockFilter)
        advanceUntilIdle()
        val result = viewModel.filteredCallListLiveData.getOrAwaitValue()
        assertEquals(mockNumber, (result[0] as FilteredCallWithFilter).call?.number)
    }

    @Test
    fun deleteFilterTest() = runTest {
        val mockFilter = "mockFilter"
        val filter = Filter(filter = mockFilter)
        Mockito.doAnswer {
            val result = it.arguments[1] as () -> Unit
            result.invoke()
        }.`when`(detailsFilterUseCase).deleteFilter(eq(filter), any())
        viewModel.deleteFilter(filter)
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
        }.`when`(detailsFilterUseCase).updateFilter(eq(filter), any())
        viewModel.updateFilter(filter)
        advanceUntilIdle()
        val result = viewModel.filterActionLiveData.getOrAwaitValue()
        assertEquals(mockFilter, result.filter)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}