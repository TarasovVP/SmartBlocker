package com.tarasovvp.smartblocker.usecases

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.domain.enums.NumberDataFiltering
import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.models.database_views.FilteredCallWithFilter
import com.tarasovvp.smartblocker.domain.models.database_views.LogCallWithFilter
import com.tarasovvp.smartblocker.domain.models.entities.*
import com.tarasovvp.smartblocker.domain.repository.FilteredCallRepository
import com.tarasovvp.smartblocker.domain.repository.LogCallRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.usecase.number.list.list_call.ListCallUseCase
import com.tarasovvp.smartblocker.domain.usecase.number.list.list_call.ListCallUseCaseImpl
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.utils.extensions.isContaining
import com.tarasovvp.smartblocker.utils.extensions.isTrue
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

    @Mock
    private lateinit var realDataBaseRepository: RealDataBaseRepository

    @Mock
    private lateinit var resultMock: () -> Unit

    private lateinit var listCallUseCase: ListCallUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        listCallUseCase = ListCallUseCaseImpl(logCallRepository, filteredCallRepository, realDataBaseRepository)
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
    fun getFilteredCallListTest() = runTest {
        val callList = listOf(CallWithFilter(call = FilteredCall(), filterWithCountryCode = FilterWithCountryCode(filter = Filter(filterType = Constants.BLOCKER))), CallWithFilter(call = LogCall().apply { number = "567" }))
        val searchQuery = String.EMPTY
        val filterIndexes = arrayListOf(
            NumberDataFiltering.CALL_BLOCKED.ordinal)
        val expectedCallList = if (searchQuery.isBlank() && filterIndexes.isEmpty()) callList else callList.filter { callWithFilter ->
            (callWithFilter.call?.callName isContaining searchQuery || callWithFilter.call?.number isContaining searchQuery)
                    && (callWithFilter.call?.isBlockedCall().isTrue() && filterIndexes.contains(NumberDataFiltering.CALL_BLOCKED.ordinal).isTrue()
                    || callWithFilter.call?.isPermittedCall().isTrue() && filterIndexes.contains(NumberDataFiltering.CALL_PERMITTED.ordinal).isTrue()
                    || filterIndexes.isEmpty())
        }
        Mockito.`when`(logCallRepository.getFilteredCallList(callList, String.EMPTY, arrayListOf(
            NumberDataFiltering.CALL_BLOCKED.ordinal)))
            .thenReturn(expectedCallList)
        val result = listCallUseCase.getFilteredCallList(callList, String.EMPTY, arrayListOf(NumberDataFiltering.CALL_BLOCKED.ordinal))
        assertEquals(expectedCallList, result)
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
        }.`when`(realDataBaseRepository).deleteFilteredCallList(eq(callList.map { it.call?.callId.toString() }), any())
        Mockito.`when`(filteredCallRepository.deleteFilteredCalls(eq(callList.map { it.call?.callId.orZero() }))).thenReturn(Unit)
        listCallUseCase.deleteCallList(callList.map { it.call?.callId.orZero() }, true, resultMock)
        verify(realDataBaseRepository).deleteFilteredCallList(eq(callList.map { it.call?.callId.toString() }), any())
        verify(filteredCallRepository).deleteFilteredCalls(eq(callList.map { it.call?.callId.orZero() }))
        verify(resultMock).invoke()
        listCallUseCase.deleteCallList(callList.map { it.call?.callId.orZero() }, false, resultMock)
        verify(realDataBaseRepository, times(1)).deleteFilteredCallList(eq(callList.map { it.call?.callId.toString() }), any())
        verify(filteredCallRepository, times(2)).deleteFilteredCalls(eq(callList.map { it.call?.callId.orZero() }))
        verify(resultMock, times(2)).invoke()
    }
}
