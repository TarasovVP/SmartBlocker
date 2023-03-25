package com.tarasovvp.smartblocker.viewmodels

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.tarasovvp.smartblocker.TestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.domain.models.entities.Call
import com.tarasovvp.smartblocker.domain.models.entities.CallWithFilter
import com.tarasovvp.smartblocker.domain.usecase.number.list.list_call.ListCallUseCase
import com.tarasovvp.smartblocker.presentation.main.number.list.list_call.ListCallViewModel
import com.tarasovvp.smartblocker.utils.extensions.orZero
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
class ListCallViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var application: Application

    @Mock
    private lateinit var listCallUseCase: ListCallUseCase

    private lateinit var viewModel: ListCallViewModel
    private lateinit var callNumber: String

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(StandardTestDispatcher())
        viewModel =
            ListCallViewModel(application, listCallUseCase)
        callNumber = "123"
    }

    @Test
    fun getCallListTest() = runTest {
        val callList = listOf(CallWithFilter(call = Call(number = callNumber)))
        Mockito.`when`(listCallUseCase.getCallList())
            .thenReturn(callList)
        viewModel.getCallList(false)
        advanceUntilIdle()
        val result = viewModel.callListLiveData.getOrAwaitValue()
        assertEquals(callNumber, result[0].call?.number)
    }

    @Test
    fun getHashMapFromCallListTest() = runTest {
        val callList = listOf(CallWithFilter(call = Call(number = callNumber)), CallWithFilter(call = Call(number = "567")))
        val callMap = mapOf("1" to callList)
        Mockito.`when`(listCallUseCase.getHashMapFromCallList(callList))
            .thenReturn(callMap)
        viewModel.getHashMapFromCallList(callList, false)
        advanceUntilIdle()
        val result = viewModel.callHashMapLiveData.getOrAwaitValue()
        assertEquals(callNumber, result?.get("1")?.get(0)?.call?.number)
    }

    @Test
    fun deleteCallListTest() = runTest {
        val callList = listOf(CallWithFilter(call = Call(number = callNumber, callId = 123)))
        Mockito.doAnswer {
            val result = it.arguments[1] as () -> Unit
            result.invoke()
        }.`when`(listCallUseCase).deleteCallList(eq(callList.map { it.call?.callId.orZero() }), any())
        viewModel.deleteCallList(callList.map { it.call?.callId.orZero() })
        advanceUntilIdle()
        val result = viewModel.successDeleteNumberLiveData.getOrAwaitValue()
        assertEquals(true, result)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
