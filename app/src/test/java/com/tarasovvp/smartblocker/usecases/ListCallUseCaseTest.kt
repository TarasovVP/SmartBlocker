package com.tarasovvp.smartblocker.usecases

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.tarasovvp.smartblocker.TestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.domain.models.database_views.FilteredCallWithFilter
import com.tarasovvp.smartblocker.domain.models.database_views.LogCallWithFilter
import com.tarasovvp.smartblocker.domain.models.entities.Call
import com.tarasovvp.smartblocker.domain.models.entities.CallWithFilter
import com.tarasovvp.smartblocker.domain.models.entities.FilteredCall
import com.tarasovvp.smartblocker.domain.models.entities.LogCall
import com.tarasovvp.smartblocker.domain.repository.FilteredCallRepository
import com.tarasovvp.smartblocker.domain.repository.LogCallRepository
import com.tarasovvp.smartblocker.domain.usecase.number.list.list_call.ListCallUseCase
import com.tarasovvp.smartblocker.domain.usecase.number.list.list_call.ListCallUseCaseImpl
import com.tarasovvp.smartblocker.utils.extensions.orZero
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ListCallUseCaseTest {

    @Mock
    private lateinit var logCallRepository: LogCallRepository

    @Mock
    private lateinit var filteredCallRepository: FilteredCallRepository

    private lateinit var listCallUseCase: ListCallUseCase

    private val resultMock = mock<() -> Unit>()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        listCallUseCase = ListCallUseCaseImpl(logCallRepository, filteredCallRepository)
    }

    @Test
    fun getCallListTest() = runTest {
        val logCallList = listOf(LogCallWithFilter().apply { call = LogCall(callId = 1) }, LogCallWithFilter().apply { call = LogCall(callId = 2) })
        val filteredCallList = listOf(FilteredCallWithFilter().apply { call=  FilteredCall(callId = 1)}, FilteredCallWithFilter().apply { call=  FilteredCall(callId = 3)})
        val commonCallList = ArrayList<CallWithFilter>().apply {
            addAll(filteredCallList)
            addAll(logCallList)
        }.distinctBy {
            it.call?.callId
        }
        Mockito.`when`(logCallRepository.getAllLogCallWithFilter())
            .thenReturn(logCallList)
        Mockito.`when`(filteredCallRepository.allFilteredCallWithFilter())
            .thenReturn(filteredCallList)
        val result = listCallUseCase.getCallList()
        assertEquals(commonCallList, result)
    }

    @Test
    fun getHashMapFromCallListTest() = runTest {
        val callList = listOf(CallWithFilter(call = Call(number = TEST_NUMBER)), CallWithFilter(call = Call(number = "567")))
        val callMap = mapOf("1" to callList)
        Mockito.`when`(logCallRepository.getHashMapFromCallList(callList))
            .thenReturn(callMap)
        val result = listCallUseCase.getHashMapFromCallList(callList)
        assertEquals(TEST_NUMBER, result?.get("1")?.get(0)?.call?.number)
    }

    @Test
    fun deleteCallListTest() = runTest {
        val callList = listOf(CallWithFilter(call = Call(number = TEST_NUMBER, callId = 123)))
        Mockito.doAnswer {
            @Suppress("UNCHECKED_CAST")
            val result = it.arguments[1] as () -> Unit
            result.invoke()
        }.`when`(filteredCallRepository).deleteFilteredCalls(eq(callList.map { it.call?.callId.orZero() }), any())
        listCallUseCase.deleteCallList(callList.map { it.call?.callId.orZero() }, resultMock)
        verify(resultMock).invoke()
    }
}
