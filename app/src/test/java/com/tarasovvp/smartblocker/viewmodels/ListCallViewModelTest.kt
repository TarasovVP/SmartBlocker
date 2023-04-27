package com.tarasovvp.smartblocker.viewmodels

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.UnitTestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.domain.enums.NumberDataFiltering
import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.models.entities.*
import com.tarasovvp.smartblocker.domain.usecase.number.list.list_call.ListCallUseCase
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.presentation.main.number.list.list_call.ListCallViewModel
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import com.tarasovvp.smartblocker.utils.extensions.orZero
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
class ListCallViewModelTest: BaseViewModelTest<ListCallViewModel>() {

    @Mock
    private lateinit var useCase: ListCallUseCase

    override fun createViewModel() = ListCallViewModel(application, useCase)

    @Test
    fun getCallListTest() = runTest {
        val callList = listOf(CallWithFilter(call = Call(number = TEST_NUMBER)))
        Mockito.`when`(useCase.getCallList())
            .thenReturn(callList)
        viewModel.getCallList(false)
        advanceUntilIdle()
        val result = viewModel.callListLiveData.getOrAwaitValue()
        assertEquals(TEST_NUMBER, result[0].call?.number)
    }

    @Test
    fun getFilteredCallListTest() = runTest {
        val callList = listOf(CallWithFilter(call = FilteredCall(), filterWithCountryCode = FilterWithCountryCode(filter = Filter(filterType = Constants.BLOCKER))), CallWithFilter(call = LogCall().apply { number = "567" }))
        Mockito.`when`(useCase.getFilteredCallList(callList, String.EMPTY, arrayListOf(NumberDataFiltering.CALL_BLOCKED.ordinal)))
            .thenReturn(callList.filter { it.filterWithCountryCode?.filter?.isBlocker().isTrue() })
        viewModel.getFilteredCallList(callList, String.EMPTY, arrayListOf(NumberDataFiltering.CALL_BLOCKED.ordinal))
        advanceUntilIdle()
        val result = viewModel.filteredCallListLiveData.getOrAwaitValue()
        assertEquals(callList.filter { it.filterWithCountryCode?.filter?.isBlocker().isTrue() }, result)
    }

    @Test
    fun getHashMapFromCallListTest() = runTest {
        val callList = listOf(CallWithFilter(call = Call(number = TEST_NUMBER)), CallWithFilter(call = Call(number = "567")))
        val callMap = mapOf("1" to callList)
        Mockito.`when`(useCase.getHashMapFromCallList(callList))
            .thenReturn(callMap)
        viewModel.getHashMapFromCallList(callList, false)
        advanceUntilIdle()
        val result = viewModel.callHashMapLiveData.getOrAwaitValue()
        assertEquals(TEST_NUMBER, result?.get("1")?.get(0)?.call?.number)
    }

    @Test
    fun deleteCallListTest() = runTest {
        val callList = listOf(CallWithFilter(call = Call(number = TEST_NUMBER, callId = 123)))
        Mockito.doAnswer {
            @Suppress("UNCHECKED_CAST")
            val result = it.arguments[2] as () -> Unit
            result.invoke()
        }.`when`(useCase).deleteCallList(eq(callList.map { it.call?.callId.orZero() }), any(), any())
        viewModel.deleteCallList(callList.map { it.call?.callId.orZero() })
        advanceUntilIdle()
        val result = viewModel.successDeleteNumberLiveData.getOrAwaitValue()
        assertEquals(true, result)
    }
}
