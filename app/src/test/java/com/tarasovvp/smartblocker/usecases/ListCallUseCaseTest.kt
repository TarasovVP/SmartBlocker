package com.tarasovvp.smartblocker.usecases

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.domain.enums.NumberDataFiltering
import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.models.database_views.FilteredCallWithFilter
import com.tarasovvp.smartblocker.domain.models.database_views.LogCallWithFilter
import com.tarasovvp.smartblocker.domain.models.entities.*
import com.tarasovvp.smartblocker.domain.repository.FilteredCallRepository
import com.tarasovvp.smartblocker.domain.repository.LogCallRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.usecase.ListCallUseCase
import com.tarasovvp.smartblocker.presentation.main.number.list.list_call.ListCallUseCaseImpl
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.presentation.ui_models.CallWithFilterUIModel
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
        val logCallList = listOf(LogCallWithFilter().apply { callUIModel = LogCall(callId = 1) }, LogCallWithFilter().apply { callUIModel = LogCall(callId = 2) })
        val filteredCallList = listOf(FilteredCallWithFilter().apply { callUIModel=  FilteredCall(callId = 1)}, FilteredCallWithFilter().apply { callUIModel=  FilteredCall(callId = 3)})
        val commonCallList = ArrayList<CallWithFilterUIModel>().apply {
            addAll(filteredCallList)
            addAll(logCallList)
        }.distinctBy {
            it.callUIModel?.callId
        }
        coEvery { logCallRepository.getAllLogCallWithFilter() } returns logCallList
        coEvery { filteredCallRepository.allFilteredCallWithFilter() } returns filteredCallList
        val result = listCallUseCase.getCallList()
        assertEquals(commonCallList, result)
    }

    @Test
    fun getFilteredCallListTest() = runBlocking {
        val callList = listOf(CallWithFilterUIModel(callUIModel = FilteredCall(), filterWithCountryCode = FilterWithCountryCode(filter = Filter(filterType = Constants.BLOCKER))), CallWithFilterUIModel(callUIModel = LogCall().apply { number = "567" }))
        val searchQuery = String.EMPTY
        val filterIndexes = arrayListOf(
            NumberDataFiltering.CALL_BLOCKED.ordinal)
        val expectedCallList = if (searchQuery.isBlank() && filterIndexes.isEmpty()) callList else callList.filter { callWithFilter ->
            (callWithFilter.callUIModel?.callName isContaining searchQuery || callWithFilter.callUIModel?.number isContaining searchQuery)
                    && (callWithFilter.callUIModel?.isBlockedCall().isTrue() && filterIndexes.contains(NumberDataFiltering.CALL_BLOCKED.ordinal).isTrue()
                    || callWithFilter.callUIModel?.isPermittedCall().isTrue() && filterIndexes.contains(NumberDataFiltering.CALL_PERMITTED.ordinal).isTrue()
                    || filterIndexes.isEmpty())
        }
        coEvery { logCallRepository.getFilteredCallList(callList, String.EMPTY, arrayListOf(
            NumberDataFiltering.CALL_BLOCKED.ordinal)) } returns expectedCallList
        val result = listCallUseCase.getFilteredCallList(callList, String.EMPTY, arrayListOf(NumberDataFiltering.CALL_BLOCKED.ordinal))
        assertEquals(expectedCallList, result)
    }

    @Test
    fun getHashMapFromCallListTest() = runBlocking {
        val callList = listOf(CallWithFilterUIModel(callUIModel = Call(number = TEST_NUMBER)), CallWithFilterUIModel(callUIModel = Call(number = "567")))
        val callMap = mapOf("1" to callList)
        coEvery { logCallRepository.getHashMapFromCallList(callList) } returns callMap
        val result = listCallUseCase.getHashMapFromCallList(callList)
        assertEquals(TEST_NUMBER, result?.get("1")?.get(0)?.callUIModel?.number)
    }

    @Test
    fun deleteCallListTest() = runBlocking {
        val callList = listOf(CallWithFilterUIModel(callUIModel = Call(number = TEST_NUMBER, callId = 123)))
        every { realDataBaseRepository.deleteFilteredCallList(eq(callList.map { it.callUIModel?.callId.toString() }), any()) } coAnswers {
            val callback = secondArg<() -> Unit>()
            callback.invoke()
        }
        coEvery { filteredCallRepository.deleteFilteredCalls(eq(callList.map { it.callUIModel?.callId.orZero() })) } just Runs

        listCallUseCase.deleteCallList(callList.map { it.callUIModel?.callId.orZero() }, true, resultMock)

        verify { realDataBaseRepository.deleteFilteredCallList(eq(callList.map { it.callUIModel?.callId.toString() }), any()) }
        coVerify { filteredCallRepository.deleteFilteredCalls(eq(callList.map { it.callUIModel?.callId.orZero() })) }
        verify { resultMock.invoke() }

        listCallUseCase.deleteCallList(callList.map { it.callUIModel?.callId.orZero() }, false, resultMock)

        verify(exactly = 1) {  realDataBaseRepository.deleteFilteredCallList(eq(callList.map { it.callUIModel?.callId.toString() }), any()) }
        coVerify(exactly = 2) { filteredCallRepository.deleteFilteredCalls(eq(callList.map { it.callUIModel?.callId.orZero() })) }
        verify(exactly = 2) { resultMock.invoke() }
    }
}
