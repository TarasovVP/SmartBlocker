package com.tarasovvp.smartblocker.usecases

import android.app.Application
import com.tarasovvp.smartblocker.UnitTestUtils
import com.tarasovvp.smartblocker.domain.models.CurrentUser
import com.tarasovvp.smartblocker.domain.models.entities.*
import com.tarasovvp.smartblocker.domain.repository.*
import com.tarasovvp.smartblocker.domain.usecase.MainUseCase
import com.tarasovvp.smartblocker.presentation.main.MainUseCaseImpl
import io.mockk.*
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class MainUseCaseTest {

    @MockK
    private lateinit var application: Application

    @MockK
    private lateinit var contactRepository: ContactRepository

    @MockK
    private lateinit var countryCodeRepository: CountryCodeRepository

    @MockK
    private lateinit var filterRepository: FilterRepository

    @MockK
    private lateinit var logCallRepository: LogCallRepository

    @MockK
    private lateinit var filteredCallRepository: FilteredCallRepository

    @MockK
    private lateinit var realDataBaseRepository: RealDataBaseRepository

    @MockK(relaxed = true)
    private lateinit var resultMock: (Int, Int) -> Unit

    private lateinit var mainUseCase: MainUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mainUseCase = MainUseCaseImpl(contactRepository, countryCodeRepository, filterRepository, logCallRepository, filteredCallRepository, realDataBaseRepository)
    }

    @Test
    fun getCurrentUserTest() = runBlocking {
        val currentUser = CurrentUser()
        every { realDataBaseRepository.getCurrentUser(any()) } answers {
            val result = firstArg<(CurrentUser) -> Unit>()
            result.invoke(currentUser)
        }
        mainUseCase.getCurrentUser { resultUser ->
            assertEquals(currentUser, resultUser)
        }
        verify { realDataBaseRepository.getCurrentUser(any()) }
    }

    @Test
    fun insertUserFiltersTest() = runBlocking {
        val filterList = listOf(Filter(filter = UnitTestUtils.TEST_FILTER), Filter(filter = "mockFilter2"))
        coEvery { filterRepository.insertAllFilters(filterList) } just Runs
        mainUseCase.insertAllFilters(filterList)
        coVerify(exactly = 1) { filterRepository.insertAllFilters(filterList)  }
    }

    @Test
    fun insertUserFilteredCallsTest() = runBlocking {
        val filteredCallList = listOf(FilteredCall().apply { number = UnitTestUtils.TEST_FILTER }, FilteredCall().apply { number = UnitTestUtils.TEST_FILTER })
        coEvery { filteredCallRepository.insertAllFilteredCalls(filteredCallList) } just Runs
        mainUseCase.insertAllFilteredCalls(filteredCallList)
        coVerify(exactly = 1) { filteredCallRepository.insertAllFilteredCalls(filteredCallList)  }
    }

    @Test
    fun getSystemCountryCodeListTest() = runBlocking {
        val countryCodeList = listOf(CountryCode(country = UnitTestUtils.TEST_COUNTRY))
        coEvery { countryCodeRepository.getSystemCountryCodeList(any()) } returns countryCodeList
        val resultCountryCodeList = mainUseCase.getSystemCountryCodeList(resultMock)
        assertEquals(countryCodeList, resultCountryCodeList)
    }

    @Test
    fun insertAllCountryCodesTest() = runBlocking {
        val countryCodeList = listOf(CountryCode(), CountryCode())
        coEvery { countryCodeRepository.insertAllCountryCodes(countryCodeList) } just Runs
        mainUseCase.insertAllCountryCodes(countryCodeList)
        coVerify(exactly = 1) { countryCodeRepository.insertAllCountryCodes(countryCodeList) }
    }

    @Test
    fun getAllFiltersTest() = runBlocking {
        val filterList = listOf(Filter(filter = UnitTestUtils.TEST_FILTER), Filter(filter = "mockFilter2"))
        coEvery { filterRepository.allFilters() } returns filterList
        val resultFilterList = mainUseCase.getAllFilters()
        assertEquals(filterList, resultFilterList)
    }

    @Test
    fun getSystemContactListTest() = runBlocking {
        val contactList = arrayListOf(Contact(name = UnitTestUtils.TEST_NAME))
        coEvery { contactRepository.getSystemContactList(eq(application), any()) } returns contactList
        val resultContactList = mainUseCase.getSystemContactList(application, resultMock)
        assertEquals(contactList, resultContactList)
    }

    @Test
    fun setFilterToContactTest() = runBlocking {
        val filterList = listOf(Filter(filter = UnitTestUtils.TEST_FILTER))
        val contactList = listOf(Contact(name = UnitTestUtils.TEST_NAME))
        coEvery { contactRepository.setFilterToContact(eq(filterList), eq(contactList), any()) } returns contactList
        val resultContactList = mainUseCase.setFilterToContact(filterList, contactList, resultMock)
        assertEquals(contactList, resultContactList)
    }

    @Test
    fun insertContactsTest() = runBlocking {
        val contactList = listOf(Contact(), Contact())
        coEvery { contactRepository.insertAllContacts(contactList) } just Runs
        mainUseCase.insertContacts(contactList)
        coVerify { contactRepository.insertAllContacts(contactList) }
    }

    @Test
    fun getSystemLogCallListTest() = runBlocking {
        val logCallList = listOf(LogCall().apply { number = UnitTestUtils.TEST_NUMBER })
        coEvery { logCallRepository.getSystemLogCallList(eq(application), any()) } returns logCallList
        val resultLogCallList = mainUseCase.getSystemLogCallList(application, resultMock)
        assertEquals(logCallList, resultLogCallList)
    }

    @Test
    fun setFilterToLogCallTest() = runBlocking {
        val filterList = listOf(Filter(filter = UnitTestUtils.TEST_FILTER))
        val logCallList = listOf(LogCall().apply { number = UnitTestUtils.TEST_NUMBER })
        coEvery { logCallRepository.setFilterToLogCall(eq(filterList), eq(logCallList), any()) } returns logCallList
        val resultLogCallList = mainUseCase.setFilterToLogCall(filterList, logCallList, resultMock)
        assertEquals(logCallList, resultLogCallList)
    }

    @Test
    fun getAllFilteredCallsTest() = runBlocking {
        val filteredCallList = listOf(FilteredCall().apply { number = UnitTestUtils.TEST_NUMBER })
        coEvery { filteredCallRepository.allFilteredCalls() } returns filteredCallList
        val resultFilteredCallList = mainUseCase.getAllFilteredCalls()
        assertEquals(filteredCallList, resultFilteredCallList)
    }

    @Test
    fun setFilterToFilteredCallTest() = runBlocking {
        val filterList = listOf(Filter(filter = UnitTestUtils.TEST_FILTER))
        val filteredCallList = listOf(FilteredCall().apply { number = UnitTestUtils.TEST_NUMBER })
        coEvery { filteredCallRepository.setFilterToFilteredCall(eq(filterList), eq(filteredCallList), any()) } returns filteredCallList
        val resultFilteredCallList = mainUseCase.setFilterToFilteredCall(filterList, filteredCallList, resultMock)
        assertEquals(filteredCallList, resultFilteredCallList)
    }

    @Test
    fun insertAllFilteredCallsTest() = runBlocking {
        val filteredCallList = listOf(FilteredCall(), FilteredCall())
        coEvery { filteredCallRepository.insertAllFilteredCalls(filteredCallList) } just Runs
        mainUseCase.insertAllFilteredCalls(filteredCallList)
        coVerify { filteredCallRepository.insertAllFilteredCalls(filteredCallList) }
    }
}