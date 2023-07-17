package com.tarasovvp.smartblocker.usecases

import com.google.firebase.auth.FirebaseAuth
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.domain.entities.db_entities.LogCall
import com.tarasovvp.smartblocker.domain.entities.db_views.CallWithFilter
import com.tarasovvp.smartblocker.domain.entities.models.Call
import com.tarasovvp.smartblocker.domain.repository.DataStoreRepository
import com.tarasovvp.smartblocker.domain.repository.FilteredCallRepository
import com.tarasovvp.smartblocker.domain.repository.LogCallRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.ListCallUseCase
import com.tarasovvp.smartblocker.presentation.main.number.list.list_call.ListCallUseCaseImpl
import com.tarasovvp.smartblocker.utils.extensions.isAuthorisedUser
import com.tarasovvp.smartblocker.utils.extensions.orZero
import io.mockk.*
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class ListCallUseCaseUnitTest {

    @MockK
    private lateinit var logCallRepository: LogCallRepository

    @MockK
    private lateinit var filteredCallRepository: FilteredCallRepository

    @MockK
    private lateinit var realDataBaseRepository: RealDataBaseRepository

    @MockK
    private lateinit var dataBaseRepository: DataStoreRepository

    @MockK
    private lateinit var firebaseAuth: FirebaseAuth

    @MockK(relaxed = true)
    private lateinit var resultMock: (Result<Unit>) -> Unit

    private lateinit var listCallUseCase: ListCallUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mockkStatic("com.tarasovvp.smartblocker.utils.extensions.DeviceExtensionsKt")
        every { firebaseAuth.isAuthorisedUser() } returns true
        listCallUseCase = ListCallUseCaseImpl(logCallRepository, filteredCallRepository, realDataBaseRepository, firebaseAuth, dataBaseRepository)
    }

    @Test
    fun allCallWithFiltersTest() = runBlocking {
        val callWithFilterList = listOf(CallWithFilter().apply { call = LogCall(callId = 1) }, CallWithFilter().apply { call = LogCall(callId = 2) })
        coEvery { logCallRepository.allCallWithFilters() } returns callWithFilterList
        val result = listCallUseCase.allCallWithFilters()
        assertEquals(callWithFilterList, result)
    }

    @Test
    fun deleteCallListTest() = runBlocking {
        val callIdList = listOf(CallWithFilter(call = Call(number = TEST_NUMBER, callId = 123))).map { it.call?.callId.orZero() }
        every { firebaseAuth.currentUser } returns mockk()
        val expectedResult = Result.Success<Unit>()
        coEvery { realDataBaseRepository.deleteFilteredCallList(eq(callIdList.map { it.toString() }), any()) } coAnswers {
            val callback = secondArg<(Result<Unit>) -> Unit>()
            callback.invoke(expectedResult)
        }
        coEvery { filteredCallRepository.deleteFilteredCalls(eq(callIdList)) } just Runs

        listCallUseCase.deleteCallList(callIdList, true, resultMock)

        verify { realDataBaseRepository.deleteFilteredCallList(eq(callIdList.map { it.toString() }), any()) }
        coVerify { filteredCallRepository.deleteFilteredCalls(eq(callIdList)) }
        verify { resultMock.invoke(expectedResult) }
    }
}
