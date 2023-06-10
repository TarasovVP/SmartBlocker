package com.tarasovvp.smartblocker.viewmodels

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.UnitTestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.domain.entities.db_views.CallWithFilter
import com.tarasovvp.smartblocker.domain.entities.models.Call
import com.tarasovvp.smartblocker.domain.enums.NumberDataFiltering
import com.tarasovvp.smartblocker.domain.usecases.ListCallUseCase
import com.tarasovvp.smartblocker.presentation.main.number.list.list_call.ListCallViewModel
import com.tarasovvp.smartblocker.presentation.mappers.CallWithFilterUIMapper
import com.tarasovvp.smartblocker.presentation.ui_models.CallWithFilterUIModel
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test

@ExperimentalCoroutinesApi
class ListCallViewModelUnitTest: BaseViewModelUnitTest<ListCallViewModel>() {

    @MockK
    private lateinit var useCase: ListCallUseCase

    @MockK
    private lateinit var callWithFilterUIMapper: CallWithFilterUIMapper

    override fun createViewModel() = ListCallViewModel(application, useCase, callWithFilterUIMapper)

    @Test
    fun getCallListTest() = runTest {
        val callList = listOf(CallWithFilter(call = Call(number = TEST_NUMBER)), CallWithFilter(call = Call(number = "1234")))
        val callUIModelList = listOf(CallWithFilterUIModel(number = TEST_NUMBER), CallWithFilterUIModel(number = "1234"))
        coEvery { useCase.allCallWithFilters() } returns callList
        every { callWithFilterUIMapper.mapToUIModelList(callList) } returns callUIModelList
        viewModel.getCallList(false)
        advanceUntilIdle()
        coVerify { useCase.allCallWithFilters() }
        verify { callWithFilterUIMapper.mapToUIModelList(callList) }
        assertEquals(callUIModelList, viewModel.callListLiveData.getOrAwaitValue())
    }

    @Test
    fun getFilteredCallListTest() = runTest {
        val numberDataFilters = arrayListOf(NumberDataFiltering.CALL_BLOCKED.ordinal)
        val callList = listOf(CallWithFilter(call = Call(number = TEST_NUMBER)), CallWithFilter(call = Call(number = "1234")))
        val callUIModelList = listOf(CallWithFilterUIModel(number = TEST_NUMBER), CallWithFilterUIModel(number = "1234"))
        coEvery { useCase.getFilteredCallList(callList, String.EMPTY, arrayListOf(NumberDataFiltering.CALL_BLOCKED.ordinal)) } returns callList
        every { callWithFilterUIMapper.mapToUIModelList(callList) } returns callUIModelList
        every { callWithFilterUIMapper.mapFromUIModelList(callUIModelList) } returns callList
        viewModel.getFilteredCallList(callUIModelList, String.EMPTY, numberDataFilters)
        advanceUntilIdle()
        coVerify { useCase.getFilteredCallList(callList, String.EMPTY, numberDataFilters) }
        verify { callWithFilterUIMapper.mapToUIModelList(callList) }
        verify { callWithFilterUIMapper.mapFromUIModelList(callUIModelList) }
        assertEquals(callUIModelList, viewModel.filteredCallListLiveData.getOrAwaitValue())
    }

    @Test
    fun deleteCallListTest() = runTest {
        /*val expectedResult = Result.Success<Unit>()
        val callList = listOf(CallWithFilter(call = Call(number = TEST_NUMBER)), CallWithFilter(call = Call(number = "1234")))
        val callUIModelList = listOf(CallWithFilterUIModel(number = TEST_NUMBER), CallWithFilterUIModel(number = "1234"))
        every { application.isNetworkAvailable } returns true
        coEvery { useCase.deleteCallList(eq(callUIModelList.map { it.callId.orZero() }), any(), any()) } answers {
            val result = thirdArg<(Result<Unit>) -> Unit>()
            result.invoke(expectedResult)
        }
        every { callWithFilterUIMapper.mapFromUIModelList(callUIModelList) } returns callList
        viewModel.deleteCallList(callUIModelList.map { it.callId.orZero() })
        advanceUntilIdle()
        coVerify { useCase.deleteCallList(eq(callUIModelList.map { it.callId.orZero() }), any(), any()) }
        verify { callWithFilterUIMapper.mapFromUIModelList(callUIModelList) }
        assertEquals(true, viewModel.successDeleteNumberLiveData.value)*/
    }
}
