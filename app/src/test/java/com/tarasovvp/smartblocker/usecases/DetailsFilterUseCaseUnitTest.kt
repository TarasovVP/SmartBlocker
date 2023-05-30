package com.tarasovvp.smartblocker.usecases

import com.google.firebase.auth.FirebaseAuth
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_FILTER
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.domain.entities.db_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.entities.db_entities.*
import com.tarasovvp.smartblocker.domain.entities.db_views.CallWithFilter
import com.tarasovvp.smartblocker.domain.repository.*
import com.tarasovvp.smartblocker.domain.usecases.DetailsFilterUseCase
import com.tarasovvp.smartblocker.presentation.main.number.details.details_filter.DetailsFilterUseCaseImpl
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import io.mockk.*
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class DetailsFilterUseCaseUnitTest {

    @MockK
    private lateinit var contactRepository: ContactRepository

    @MockK
    private lateinit var filterRepository: FilterRepository

    @MockK
    private lateinit var realDataBaseRepository: RealDataBaseRepository

    @MockK
    private lateinit var logCallRepository: LogCallRepository

    @MockK
    private lateinit var filteredCallRepository: FilteredCallRepository

    @MockK
    private lateinit var firebaseAuth: FirebaseAuth

    @MockK(relaxed = true)
    private lateinit var resultMock: (Result<Unit>) -> Unit

    private lateinit var detailsFilterUseCase: DetailsFilterUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        detailsFilterUseCase = DetailsFilterUseCaseImpl(contactRepository, filterRepository, realDataBaseRepository, logCallRepository, filteredCallRepository, firebaseAuth)
    }

    @Test
    fun allCallWithFiltersByFilterTest() = runBlocking {
        val filter = TEST_FILTER
        val callList = listOf(CallWithFilter().apply { call = LogCall().apply { number = "1" } }, CallWithFilter().apply { call = LogCall().apply { number = "2"} })
        coEvery { logCallRepository.allCallWithFiltersByFilter(filter) } returns callList
        val result = detailsFilterUseCase.allCallWithFiltersByFilter(filter)
        assertEquals(callList, result)
    }

    @Test
    fun allContactsWithFiltersByFilterTest() = runBlocking {
        val filter = TEST_FILTER
        val contactList = listOf(ContactWithFilter(contact =  Contact(number = "1")), ContactWithFilter(contact =  Contact(number = "1")))
        coEvery { contactRepository.allContactsWithFiltersByFilter(filter) } returns contactList
        val result = detailsFilterUseCase.allContactsWithFiltersByFilter(filter)
        assertEquals(contactList, result)
    }

    @Test
    fun allFilteredCallsByFilterTest() = runBlocking {
        val filteredCallList = listOf(CallWithFilter().apply { call = FilteredCall().apply { this.number = TEST_NUMBER } })
        coEvery { filteredCallRepository.allFilteredCallsByFilter(TEST_FILTER) } returns filteredCallList
        val result = detailsFilterUseCase.allFilteredCallsByFilter(TEST_FILTER)
        assertEquals(filteredCallList, result)
    }

    @Test
    fun deleteFilterTest() = runBlocking {
        val filter = Filter(filter = TEST_FILTER)
        every { firebaseAuth.currentUser } returns mockk()
        val expectedResult = Result.Success<Unit>()
        coEvery { realDataBaseRepository.deleteFilterList(eq(listOf(filter)), any()) } coAnswers {
            val callback = secondArg<(Result<Unit>) -> Unit>()
            callback.invoke(expectedResult)
        }
        coEvery { filterRepository.deleteFilterList(eq(listOf(filter))) } just Runs

        detailsFilterUseCase.deleteFilter(filter, true, resultMock)

        verify { realDataBaseRepository.deleteFilterList(eq(listOf(filter)), any()) }
        coVerify { filterRepository.deleteFilterList(eq(listOf(filter))) }
        verify { resultMock.invoke(expectedResult) }
    }

    @Test
    fun updateFilterTest() = runBlocking {
        val filter = Filter(filter = TEST_FILTER)
        every { firebaseAuth.currentUser } returns mockk()
        val expectedResult = Result.Success<Unit>()
        coEvery { realDataBaseRepository.insertFilter(eq(filter), any()) } coAnswers {
            val callback = secondArg<(Result<Unit>) -> Unit>()
            callback.invoke(expectedResult)
        }
        coEvery { filterRepository.updateFilter(eq(filter)) } just Runs

        detailsFilterUseCase.updateFilter(filter, true, resultMock)

        verify { realDataBaseRepository.insertFilter(eq(filter), any()) }
        coVerify { filterRepository.updateFilter(eq(filter)) }
        verify { resultMock.invoke(expectedResult) }
    }
}