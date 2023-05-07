package com.tarasovvp.smartblocker.viewmodels

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.UnitTestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.domain.enums.NumberDataFiltering
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.entities.db_entities.*
import com.tarasovvp.smartblocker.domain.entities.models.Call
import com.tarasovvp.smartblocker.domain.entities.db_views.CallWithFilter
import com.tarasovvp.smartblocker.domain.usecases.ListCallUseCase
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.presentation.main.number.list.list_call.ListCallViewModel
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import com.tarasovvp.smartblocker.utils.extensions.orZero
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Test

@ExperimentalCoroutinesApi
class ListCallViewModelTest: BaseViewModelTest<ListCallViewModel>() {

    @MockK
    private lateinit var useCase: ListCallUseCase

    override fun createViewModel() = ListCallViewModel(application, useCase)

    @Test
    fun getCallListTest() = runTest {
        val callList = listOf(CallWithFilter(call = Call(number = TEST_NUMBER)))
        coEvery { useCase.allCallWithFilters() } returns callList
        viewModel.getCallList(false)
        advanceUntilIdle()
        val result = viewModel.callListLiveData.getOrAwaitValue()
        assertEquals(TEST_NUMBER, result[0].call?.number)
    }

    @Test
    fun getFilteredCallListTest() = runTest {
        val callList = listOf(CallWithFilter(call = FilteredCall(), filterWithCountryCode = FilterWithCountryCode(filter = Filter(filterType = Constants.BLOCKER))), CallWithFilter(call = LogCall().apply { number = "567" }))
        coEvery { useCase.getFilteredCallList(callList, String.EMPTY, arrayListOf(NumberDataFiltering.CALL_BLOCKED.ordinal)) } returns callList.filter { it.filterWithCountryCode?.filter?.isBlocker().isTrue() }
        viewModel.getFilteredCallList(callList, String.EMPTY, arrayListOf(NumberDataFiltering.CALL_BLOCKED.ordinal))
        advanceUntilIdle()
        val result = viewModel.filteredCallListLiveData.getOrAwaitValue()
        assertEquals(callList.filter { it.filterWithCountryCode?.filter?.isBlocker().isTrue() }, result)
    }

    @Test
    fun getHashMapFromCallListTest() = runTest {
        val callList = listOf(CallWithFilter(call = Call(number = TEST_NUMBER)), CallWithFilter(call = Call(number = "567")))
        val callMap = mapOf("1" to callList)
        coEvery { useCase.getHashMapFromCallList(callList) } returns callMap
        viewModel.getHashMapFromCallList(callList, false)
        advanceUntilIdle()
        val result = viewModel.callHashMapLiveData.getOrAwaitValue()
        assertEquals(TEST_NUMBER, result?.get("1")?.get(0)?.call?.number)
    }

    @Test
    fun deleteCallListTest() = runTest {
        val callList = listOf(CallWithFilter(call = Call(number = TEST_NUMBER, callId = 123)))
        coEvery { useCase.deleteCallList(eq(callList.map { it.call?.callId.orZero() }), any(), any()) } answers {
            val result = thirdArg<() -> Unit>()
            result.invoke()
        }
        viewModel.deleteCallList(callList.map { it.call?.callId.orZero() })
        advanceUntilIdle()
        val result = viewModel.successDeleteNumberLiveData.getOrAwaitValue()
        assertEquals(true, result)
    }
}
