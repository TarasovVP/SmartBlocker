package com.tarasovvp.smartblocker.usecases

import android.app.Application
import com.tarasovvp.smartblocker.UnitTestUtils
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_COUNTRY
import com.tarasovvp.smartblocker.domain.entities.db_entities.*
import com.tarasovvp.smartblocker.domain.entities.models.CurrentUser
import com.tarasovvp.smartblocker.domain.repository.*
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.MainUseCase
import com.tarasovvp.smartblocker.presentation.main.MainUseCaseImpl
import io.mockk.*
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class MainUseCaseUnitTest {

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
    fun getBlockerTurnOffTest() = runBlocking {
        val blockerTurnOff = true
        coEvery { dataStoreRepository.blockerTurnOn() } returns flowOf(blockerTurnOff)
        val result = mainUseCase.getBlockerTurnOn().single()
        assertEquals(blockerTurnOff, result)
        coVerify { dataStoreRepository.blockerTurnOn() }
    }

    @Test
    fun setBlockHiddenTest() = runBlocking {
        val blockHidden = true
        coEvery { dataStoreRepository.setBlockHidden(blockHidden) } just Runs
        mainUseCase.setBlockHidden(blockHidden)
        coVerify { dataStoreRepository.setBlockHidden(blockHidden) }
    }

    @Test
    fun getCurrentUserTest() = runBlocking {
        val expectedResult = Result.Success(CurrentUser())
        every { realDataBaseRepository.getCurrentUser(any()) } answers {
            val result = firstArg<(Result<CurrentUser>) -> Unit>()
            result.invoke(expectedResult)
        }
        mainUseCase.getCurrentUser { resultUser ->
            assertEquals(expectedResult, resultUser)
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
        val countryCode = CountryCode()
        coEvery { dataStoreRepository.getCountryCode() } returns flowOf(countryCode)
        val result = mainUseCase.getCurrentCountryCode().single()
        assertEquals(countryCode, result)
        coVerify { dataStoreRepository.getCountryCode() }
    }

    @Test
    fun setCurrentCountryCodeTest() = runBlocking {
        val countryCode = CountryCode()
        coEvery { dataStoreRepository.setCountryCode(countryCode) } just Runs
        mainUseCase.setCurrentCountryCode(countryCode)
        coVerify { dataStoreRepository.setCountryCode(countryCode) }
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
        coEvery { dataStoreRepository.getCountryCode() } returns flowOf(CountryCode())
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
        coEvery { dataStoreRepository.getCountryCode() } returns flowOf(CountryCode())
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