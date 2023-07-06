package com.tarasovvp.smartblocker.usecases

import com.google.firebase.auth.FirebaseAuth
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_COUNTRY
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_COUNTRY_CODE
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_FILTER
import com.tarasovvp.smartblocker.domain.entities.db_entities.Contact
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.entities.db_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithFilteredNumber
import com.tarasovvp.smartblocker.domain.repository.ContactRepository
import com.tarasovvp.smartblocker.domain.repository.FilterRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.CreateFilterUseCase
import com.tarasovvp.smartblocker.presentation.main.number.create.CreateFilterUseCaseImpl
import com.tarasovvp.smartblocker.utils.AppPhoneNumberUtil
import com.tarasovvp.smartblocker.utils.extensions.isAuthorisedUser
import io.mockk.*
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class CreateFilterUseCaseUnitTest {

    @MockK
    private lateinit var contactRepository: ContactRepository

    @MockK
    private lateinit var appPhoneNumberUtil: AppPhoneNumberUtil


    @MockK
    private lateinit var filterRepository: FilterRepository

    @MockK
    private lateinit var realDataBaseRepository: RealDataBaseRepository
    
    @MockK
    private lateinit var firebaseAuth: FirebaseAuth

    @MockK(relaxed = true)
    private lateinit var resultMock: (Result<Unit>) -> Unit

    private lateinit var createFilterUseCase: CreateFilterUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mockkStatic("com.tarasovvp.smartblocker.utils.extensions.DeviceExtensionsKt")
        every { firebaseAuth.isAuthorisedUser() } returns true
        createFilterUseCase = CreateFilterUseCaseImpl(contactRepository, appPhoneNumberUtil, filterRepository, realDataBaseRepository, firebaseAuth)
    }

    @Test
    fun allContactsWithFiltersByCreateFilterTest() = runBlocking {
        val filter = TEST_FILTER
        val contactList = listOf(ContactWithFilter(contact =  Contact(number = "1")), ContactWithFilter(contact =  Contact(number = "1")))
        coEvery { contactRepository.allContactsWithFiltersByCreateFilter(filter) } returns contactList
        val result = createFilterUseCase.allContactsWithFiltersByCreateFilter(filter, TEST_COUNTRY, TEST_COUNTRY_CODE, true)
        assertEquals(contactList, result)
    }

    @Test
    fun getFilterTest() = runBlocking {
        val filter = TEST_FILTER
        val filterWithFilteredNumber = FilterWithFilteredNumber(filter = Filter(filter = TEST_FILTER))
        coEvery { filterRepository.getFilter(filter) } returns filterWithFilteredNumber
        val result = createFilterUseCase.getFilter(filter)
        assertEquals(TEST_FILTER, result?.filter?.filter)
    }

    @Test
    fun createFilterTest() = runBlocking {
        val filter = Filter(filter = TEST_FILTER)
        val expectedResult = Result.Success<Unit>()
        every { firebaseAuth.currentUser } returns mockk()
        coEvery { realDataBaseRepository.insertFilter(eq(filter), any()) } coAnswers {
            val callback = secondArg<(Result<Unit>) -> Unit>()
            callback.invoke(expectedResult)
        }
        coEvery { filterRepository.insertFilter(eq(filter)) } just Runs

        createFilterUseCase.createFilter(filter, true, resultMock)

        verify { realDataBaseRepository.insertFilter(eq(filter), any()) }
        coVerify { filterRepository.insertFilter(eq(filter)) }
        verify { resultMock.invoke(expectedResult) }
    }

    @Test
    fun updateFilterTest() = runBlocking {
        val filter = Filter(filter = TEST_FILTER)
        val expectedResult = Result.Success<Unit>()
        every { firebaseAuth.currentUser } returns mockk()
        coEvery { realDataBaseRepository.insertFilter(eq(filter), any()) } coAnswers {
            val callback = secondArg<(Result<Unit>) -> Unit>()
            callback.invoke(expectedResult)
        }
        coEvery { filterRepository.updateFilter(eq(filter)) } just Runs

        createFilterUseCase.updateFilter(filter, true, resultMock)

        verify { realDataBaseRepository.insertFilter(eq(filter), any()) }
        coVerify { filterRepository.updateFilter(eq(filter)) }
        verify { resultMock.invoke(expectedResult) }
    }

    @Test
    fun deleteFilterTest() = runBlocking {
        val filter = Filter(filter = TEST_FILTER)
        val expectedResult = Result.Success<Unit>()
        every { firebaseAuth.currentUser } returns mockk()
        coEvery { realDataBaseRepository.deleteFilterList(eq(listOf(filter)), any()) } coAnswers {
            val callback = secondArg<(Result<Unit>) -> Unit>()
            callback.invoke(expectedResult)
        }
        coEvery { filterRepository.deleteFilterList(eq(listOf(filter))) } just Runs

        createFilterUseCase.deleteFilter(filter, true, resultMock)

        verify { realDataBaseRepository.deleteFilterList(eq(listOf(filter)), any()) }
        coVerify { filterRepository.deleteFilterList(eq(listOf(filter))) }
        verify { resultMock.invoke(expectedResult) }
    }
}