package com.tarasovvp.smartblocker.viewmodels

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.UnitTestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.domain.enums.NumberDataFiltering
import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.models.entities.*
import com.tarasovvp.smartblocker.domain.usecase.ListCallUseCase
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.presentation.main.number.list.list_call.ListCallViewModel
import com.tarasovvp.smartblocker.presentation.ui_models.CallWithFilterUIModel
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
        val callList = listOf(CallWithFilterUIModel(callUIModel = Call(number = TEST_NUMBER)))
        coEvery { useCase.getCallList() } returns callList
        viewModel.getCallList(false)
        advanceUntilIdle()
        val result = viewModel.callListLiveData.getOrAwaitValue()
        assertEquals(TEST_NUMBER, result[0].callUIModel?.number)
    }

    @Test
    fun getFilteredCallListTest() = runTest {
        val callList = listOf(CallWithFilterUIModel(callUIModel = FilteredCall(), filterWithCountryCode = FilterWithCountryCode(filter = Filter(filterType = Constants.BLOCKER))), CallWithFilterUIModel(callUIModel = LogCall().apply { number = "567" }))
        coEvery { useCase.getFilteredCallList(callList, String.EMPTY, arrayListOf(NumberDataFiltering.CALL_BLOCKED.ordinal)) } returns callList.filter { it.filterWithCountryCode?.filter?.isBlocker().isTrue() }
        viewModel.getFilteredCallList(callList, String.EMPTY, arrayListOf(NumberDataFiltering.CALL_BLOCKED.ordinal))
        advanceUntilIdle()
        val result = viewModel.filteredCallListLiveData.getOrAwaitValue()
        assertEquals(callList.filter { it.filterWithCountryCode?.filter?.isBlocker().isTrue() }, result)
    }

    @Test
    fun getHashMapFromCallListTest() = runTest {
        val callList = listOf(CallWithFilterUIModel(callUIModel = Call(number = TEST_NUMBER)), CallWithFilterUIModel(callUIModel = Call(number = "567")))
        val callMap = mapOf("1" to callList)
        coEvery { useCase.getHashMapFromCallList(callList) } returns callMap
        viewModel.getHashMapFromCallList(callList, false)
        advanceUntilIdle()
        val result = viewModel.callHashMapLiveData.getOrAwaitValue()
        assertEquals(TEST_NUMBER, result?.get("1")?.get(0)?.callUIModel?.number)
    }

    @Test
    fun deleteCallListTest() = runTest {
        val callList = listOf(CallWithFilterUIModel(callUIModel = Call(number = TEST_NUMBER, callId = 123)))
        coEvery { useCase.deleteCallList(eq(callList.map { it.callUIModel?.callId.orZero() }), any(), any()) } answers {
            val result = thirdArg<() -> Unit>()
            result.invoke()
        }
        viewModel.deleteCallList(callList.map { it.callUIModel?.callId.orZero() })
        advanceUntilIdle()
        val result = viewModel.successDeleteNumberLiveData.getOrAwaitValue()
        assertEquals(true, result)
    }
}
