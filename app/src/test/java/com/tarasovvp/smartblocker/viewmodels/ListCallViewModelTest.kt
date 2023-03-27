package com.tarasovvp.smartblocker.viewmodels

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.tarasovvp.smartblocker.TestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.TestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.domain.models.entities.Call
import com.tarasovvp.smartblocker.domain.models.entities.CallWithFilter
import com.tarasovvp.smartblocker.domain.usecase.number.list.list_call.ListCallUseCase
import com.tarasovvp.smartblocker.presentation.main.number.list.list_call.ListCallViewModel
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
            val result = it.arguments[1] as () -> Unit
            result.invoke()
        }.`when`(useCase).deleteCallList(eq(callList.map { it.call?.callId.orZero() }), any())
        viewModel.deleteCallList(callList.map { it.call?.callId.orZero() })
        advanceUntilIdle()
        val result = viewModel.successDeleteNumberLiveData.getOrAwaitValue()
        assertEquals(true, result)
    }
}
