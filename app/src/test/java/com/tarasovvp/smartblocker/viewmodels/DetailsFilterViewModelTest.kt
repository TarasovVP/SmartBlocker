package com.tarasovvp.smartblocker.viewmodels

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_FILTER
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.UnitTestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.domain.entities.db_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.entities.db_entities.*
import com.tarasovvp.smartblocker.domain.entities.models.Call
import com.tarasovvp.smartblocker.domain.entities.db_views.CallWithFilter
import com.tarasovvp.smartblocker.domain.usecases.DetailsFilterUseCase
import com.tarasovvp.smartblocker.presentation.main.number.details.details_filter.DetailsFilterViewModel
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Test

@ExperimentalCoroutinesApi
class DetailsFilterViewModelTest: BaseViewModelTest<DetailsFilterViewModel>() {

    @MockK
    private lateinit var useCase: DetailsFilterUseCase

    override fun createViewModel() = DetailsFilterViewModel(application, useCase)

    @Test
    fun getQueryContactCallListTest() = runTest {
        val filter = Filter(filter = TEST_FILTER)
        val numberDataList = arrayListOf(ContactWithFilter(contact = Contact(number = TEST_NUMBER)), CallWithFilter().apply { call = Call(number = TEST_FILTER) })
        coEvery { useCase.getQueryContactCallList(filter) } returns numberDataList
        viewModel.getQueryContactCallList(filter)
        advanceUntilIdle()
        val result = viewModel.numberDataListLiveData.getOrAwaitValue()
        assertEquals(TEST_NUMBER, (result[0] as ContactWithFilter).contact?.number)
    }

    @Test
    fun filteredNumberDataListTest() = runTest {
        val filter = Filter(filter = TEST_FILTER)
        val numberDataList = arrayListOf(ContactWithFilter(contact = Contact(number = TEST_NUMBER)), CallWithFilter().apply { call = Call(number = TEST_FILTER) })
        coEvery { useCase.filteredNumberDataList(filter, numberDataList, 0) } returns numberDataList
        viewModel.filteredNumberDataList(filter, numberDataList, 0)
        advanceUntilIdle()
        val result = viewModel.filteredNumberDataListLiveData.getOrAwaitValue()
        assertEquals(TEST_NUMBER, (result[0] as ContactWithFilter).contact?.number)
    }

    @Test
    fun filteredCallsByFilterTest() = runTest {
        val filteredCallList = listOf(FilteredCallWithFilter().apply { call = FilteredCall().apply { this.number = TEST_NUMBER } })
        coEvery { useCase.filteredCallsByFilter(TEST_FILTER) } returns filteredCallList
        viewModel.filteredCallsByFilter(TEST_FILTER)
        advanceUntilIdle()
        val result = viewModel.filteredCallListLiveData.getOrAwaitValue()
        assertEquals(filteredCallList, result)
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
}