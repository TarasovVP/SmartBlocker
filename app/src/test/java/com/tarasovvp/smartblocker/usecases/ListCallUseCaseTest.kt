package com.tarasovvp.smartblocker.usecases

import com.google.firebase.auth.FirebaseAuth
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.domain.enums.NumberDataFiltering
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithFilteredNumbers
import com.tarasovvp.smartblocker.domain.entities.db_entities.*
import com.tarasovvp.smartblocker.domain.entities.models.Call
import com.tarasovvp.smartblocker.domain.entities.db_views.CallWithFilter
import com.tarasovvp.smartblocker.domain.repository.FilteredCallRepository
import com.tarasovvp.smartblocker.domain.repository.LogCallRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.usecases.ListCallUseCase
import com.tarasovvp.smartblocker.presentation.main.number.list.list_call.ListCallUseCaseImpl
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
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

    @MockK
    private lateinit var firebaseAuth: FirebaseAuth

    @MockK(relaxed = true)
    private lateinit var resultMock: (Result<Unit>) -> Unit

    private lateinit var listCallUseCase: ListCallUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        listCallUseCase = ListCallUseCaseImpl(logCallRepository, filteredCallRepository, realDataBaseRepository, firebaseAuth)
    }

    @Test
    fun allCallWithFiltersTest() = runBlocking {
        val callWithFilterList = listOf(CallWithFilter().apply { call = LogCall(callId = 1) }, CallWithFilter().apply { call = LogCall(callId = 2) })
        coEvery { logCallRepository.allCallWithFilters() } returns callWithFilterList
        val result = listCallUseCase.allCallWithFilters()
        assertEquals(callWithFilterList, result)
    }

    @Test
    fun getFilteredCallListTest() = runBlocking {
        val callList = listOf(CallWithFilter(call = FilteredCall(), filterWithFilteredNumbers = FilterWithFilteredNumbers(filter = Filter(filterType = Constants.BLOCKER))), CallWithFilter(call = LogCall().apply { number = "567" }))
        val searchQuery = String.EMPTY
        val filterIndexes = arrayListOf(
            NumberDataFiltering.CALL_BLOCKED.ordinal)
        val expectedCallList = listOf(CallWithFilter(call = FilteredCall(), filterWithFilteredNumbers = FilterWithFilteredNumbers(filter = Filter(filterType = Constants.BLOCKER))))
        val result = listCallUseCase.getFilteredCallList(callList, searchQuery, filterIndexes)
        assertEquals(expectedCallList, result)
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
        verify { resultMock.invoke(Result.Success()) }

        listCallUseCase.deleteCallList(callList.map { it.call?.callId.orZero() }, false, resultMock)

        verify(exactly = 1) {  realDataBaseRepository.deleteFilteredCallList(eq(callList.map { it.call?.callId.toString() }), any()) }
        coVerify(exactly = 2) { filteredCallRepository.deleteFilteredCalls(eq(callList.map { it.call?.callId.orZero() })) }
        verify(exactly = 2) { resultMock.invoke(Result.Success()) }
    }
}
