package com.tarasovvp.smartblocker.usecases

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.domain.enums.NumberDataFiltering
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.entities.db_views.FilteredCallWithFilter
import com.tarasovvp.smartblocker.domain.entities.db_views.LogCallWithFilter
import com.tarasovvp.smartblocker.domain.entities.db_entities.*
import com.tarasovvp.smartblocker.domain.entities.models.Call
import com.tarasovvp.smartblocker.domain.entities.models.CallWithFilter
import com.tarasovvp.smartblocker.domain.repository.FilteredCallRepository
import com.tarasovvp.smartblocker.domain.repository.LogCallRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.usecases.ListCallUseCase
import com.tarasovvp.smartblocker.presentation.main.number.list.list_call.ListCallUseCaseImpl
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.utils.extensions.isContaining
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import com.tarasovvp.smartblocker.utils.extensions.orZero
import io.mockk.*
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class ListCallUseCaseTest {

    @MockK
    private lateinit var logCallRepository: LogCallRepository

    @MockK
    private lateinit var filteredCallRepository: FilteredCallRepository

    @MockK
    private lateinit var realDataBaseRepository: RealDataBaseRepository

    @MockK(relaxed = true)
    private lateinit var resultMock: () -> Unit

    private lateinit var listCallUseCase: ListCallUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        listCallUseCase = ListCallUseCaseImpl(logCallRepository, filteredCallRepository, realDataBaseRepository)
    }

    @Test
    fun getCallListTest() = runBlocking {
        val logCallList = listOf(LogCallWithFilter().apply { call = LogCall(callId = 1) }, LogCallWithFilter().apply { call = LogCall(callId = 2) })
        val filteredCallList = listOf(FilteredCallWithFilter().apply { call=  FilteredCall(callId = 1)}, FilteredCallWithFilter().apply { call=  FilteredCall(callId = 3)})
        val commonCallList = ArrayList<CallWithFilter>().apply {
            addAll(filteredCallList)
            addAll(logCallList)
        }.distinctBy {
            it.call?.callId
        }
        coEvery { logCallRepository.getAllLogCallWithFilter() } returns logCallList
        coEvery { filteredCallRepository.allFilteredCallWithFilter() } returns filteredCallList
        val result = listCallUseCase.getCallList()
        assertEquals(commonCallList, result)
    }

    @Test
    fun getFilteredCallListTest() = runBlocking {
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
        coEvery { logCallRepository.getFilteredCallList(callList, String.EMPTY, arrayListOf(
            NumberDataFiltering.CALL_BLOCKED.ordinal)) } returns expectedCallList
        val result = listCallUseCase.getFilteredCallList(callList, String.EMPTY, arrayListOf(NumberDataFiltering.CALL_BLOCKED.ordinal))
        assertEquals(expectedCallList, result)
    }

    @Test
    fun getHashMapFromCallListTest() = runBlocking {
        val callList = listOf(CallWithFilter(call = Call(number = TEST_NUMBER)), CallWithFilter(call = Call(number = "567")))
        val callMap = mapOf("1" to callList)
        coEvery { logCallRepository.getHashMapFromCallList(callList) } returns callMap
        val result = listCallUseCase.getHashMapFromCallList(callList)
        assertEquals(TEST_NUMBER, result?.get("1")?.get(0)?.call?.number)
    }

    @Test
    fun deleteCallListTest() = runBlocking {
        val callList = listOf(CallWithFilter(call = Call(number = TEST_NUMBER, callId = 123)))
        every { realDataBaseRepository.deleteFilteredCallList(eq(callList.map { it.call?.callId.toString() }), any()) } coAnswers {
            val callback = secondArg<() -> Unit>()
            callback.invoke()
        }
        coEvery { filteredCallRepository.deleteFilteredCalls(eq(callList.map { it.call?.callId.orZero() })) } just Runs

        listCallUseCase.deleteCallList(callList.map { it.call?.callId.orZero() }, true, resultMock)

        verify { realDataBaseRepository.deleteFilteredCallList(eq(callList.map { it.call?.callId.toString() }), any()) }
        coVerify { filteredCallRepository.deleteFilteredCalls(eq(callList.map { it.call?.callId.orZero() })) }
        verify { resultMock.invoke() }

        listCallUseCase.deleteCallList(callList.map { it.call?.callId.orZero() }, false, resultMock)

        verify(exactly = 1) {  realDataBaseRepository.deleteFilteredCallList(eq(callList.map { it.call?.callId.toString() }), any()) }
        coVerify(exactly = 2) { filteredCallRepository.deleteFilteredCalls(eq(callList.map { it.call?.callId.orZero() })) }
        verify(exactly = 2) { resultMock.invoke() }
    }
}
