package com.tarasovvp.smartblocker.usecases

import android.app.Application
import com.tarasovvp.smartblocker.UnitTestUtils
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_COUNTRY
import com.tarasovvp.smartblocker.domain.entities.models.CurrentUser
import com.tarasovvp.smartblocker.domain.entities.db_entities.*
import com.tarasovvp.smartblocker.domain.repository.*
import com.tarasovvp.smartblocker.domain.usecases.MainUseCase
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

    @MockK
    private lateinit var dataStoreRepository: DataStoreRepository

    @MockK(relaxed = true)
    private lateinit var resultMock: (Int, Int) -> Unit

    private lateinit var mainUseCase: MainUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mainUseCase = MainUseCaseImpl(contactRepository, countryCodeRepository, filterRepository, logCallRepository, filteredCallRepository, realDataBaseRepository, dataStoreRepository)
    }

    @Test
    fun getAppLanguageTest() = runBlocking {

    }

    @Test
    fun setAppLanguageTest() = runBlocking {

    }

    @Test
    fun getAppThemeTest() = runBlocking {

    }

    @Test
    fun getOnBoardingSeenTest() = runBlocking {

    }

    @Test
    fun getBlockerTurnOffTest() = runBlocking {

    }

    @Test
    fun setBlockHiddenTest(blockHidden: Boolean) = runBlocking {

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
    fun getSystemCountryCodeListTest() = runBlocking {
        val countryCodeList = listOf(CountryCode(country = TEST_COUNTRY))
        coEvery { countryCodeRepository.getSystemCountryCodeList(any()) } returns countryCodeList
        val resultCountryCodeList = mainUseCase.getSystemCountryCodes(resultMock)
        assertEquals(countryCodeList, resultCountryCodeList)
    }

    @Test
    fun getCurrentCountryCodeTest() = runBlocking {

    }

    @Test
    fun setCurrentCountryCodeTest(blockHidden: Boolean) = runBlocking {

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
        val country = TEST_COUNTRY
        coEvery { contactRepository.getSystemContactList(eq(application), eq(country), any()) } returns contactList
        val resultContactList = mainUseCase.getSystemContacts(application, resultMock)
        assertEquals(contactList, resultContactList)
    }

    @Test
    fun insertAllContactsTest() = runBlocking {
        val contactList = listOf(Contact(), Contact())
        coEvery { contactRepository.insertAllContacts(contactList) } just Runs
        mainUseCase.insertAllContacts(contactList)
        coVerify { contactRepository.insertAllContacts(contactList) }
    }

    @Test
    fun getSystemLogCallListTest() = runBlocking {
        val logCallList = listOf(LogCall().apply { number = UnitTestUtils.TEST_NUMBER })
        val country = TEST_COUNTRY
        coEvery { logCallRepository.getSystemLogCallList(eq(application), eq(country), any()) } returns logCallList
        val resultLogCallList = mainUseCase.getSystemLogCalls(application, resultMock)
        assertEquals(logCallList, resultLogCallList)
    }

    @Test
    fun insertAllFilteredCallsTest() = runBlocking {
        val filteredCallList = listOf(FilteredCall(), FilteredCall())
        coEvery { filteredCallRepository.insertAllFilteredCalls(filteredCallList) } just Runs
        mainUseCase.insertAllFilteredCalls(filteredCallList)
        coVerify { filteredCallRepository.insertAllFilteredCalls(filteredCallList) }
    }

    @Test
    fun insertAllFiltersTest() = runBlocking {
        val filterList = listOf(Filter(filter = UnitTestUtils.TEST_FILTER), Filter(filter = "mockFilter2"))
        coEvery { filterRepository.insertAllFilters(filterList) } just Runs
        mainUseCase.insertAllFilters(filterList)
        coVerify(exactly = 1) { filterRepository.insertAllFilters(filterList)  }
    }
}