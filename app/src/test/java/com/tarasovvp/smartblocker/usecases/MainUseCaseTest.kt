package com.tarasovvp.smartblocker.usecases

import android.app.Application
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.tarasovvp.smartblocker.TestUtils
import com.tarasovvp.smartblocker.domain.models.entities.*
import com.tarasovvp.smartblocker.domain.repository.*
import com.tarasovvp.smartblocker.domain.usecase.main.MainUseCase
import com.tarasovvp.smartblocker.domain.usecase.main.MainUseCaseImpl
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
class MainUseCaseTest {

    @Mock
    private lateinit var application: Application

    @Mock
    private lateinit var contactRepository: ContactRepository

    @Mock
    private lateinit var countryCodeRepository: CountryCodeRepository

    @Mock
    private lateinit var filterRepository: FilterRepository

    @Mock
    private lateinit var logCallRepository: LogCallRepository

    @Mock
    private lateinit var filteredCallRepository: FilteredCallRepository

    @Mock
    private lateinit var realDataBaseRepository: RealDataBaseRepository

    private lateinit var mainUseCase: MainUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        mainUseCase = MainUseCaseImpl(contactRepository, countryCodeRepository, filterRepository, logCallRepository, filteredCallRepository, realDataBaseRepository)
    }

    @Test
    fun getCurrentUserTest() = runTest {
        val currentUser = CurrentUser()
        Mockito.doAnswer {
            @Suppress("UNCHECKED_CAST")
            val result = it.arguments[0] as (CurrentUser) -> Unit
            result.invoke(currentUser)
        }.`when`(realDataBaseRepository).getCurrentUser(any())
        mainUseCase.getCurrentUser { resultUser ->
            assertEquals(currentUser, resultUser)
        }
        verify(realDataBaseRepository).getCurrentUser(any())
    }

    @Test
    fun insertUserFiltersTest() = runTest {
        val filterList = listOf(Filter(filter = TestUtils.TEST_FILTER), Filter(filter = "mockFilter2"))
        mainUseCase.insertAllFilters(filterList)
        verify(filterRepository, times(1)).insertAllFilters(filterList)
    }

    @Test
    fun insertUserFilteredCallsTest() = runTest {
        val filteredCallList = listOf(FilteredCall().apply { number = TestUtils.TEST_FILTER }, FilteredCall().apply { number = TestUtils.TEST_FILTER })
        mainUseCase.insertAllFilteredCalls(filteredCallList)
        verify(filteredCallRepository, times(1)).insertAllFilteredCalls(filteredCallList)
    }

    @Test
    fun getSystemCountryCodeListTest() = runTest {
        val countryCodeList = listOf(CountryCode(country = TestUtils.TEST_COUNTRY))
        Mockito.`when`(countryCodeRepository.getSystemCountryCodeList(any()))
            .thenReturn(countryCodeList)
        val resultCountryCodeList = mainUseCase.getSystemCountryCodeList(any())
        assertEquals(countryCodeList, resultCountryCodeList)
    }

    @Test
    fun insertAllCountryCodesTest() = runTest {
        val countryCodeList = listOf(CountryCode(), CountryCode())
        mainUseCase.insertAllCountryCodes(countryCodeList)
        verify(countryCodeRepository, times(1)).insertAllCountryCodes(countryCodeList)
    }

    @Test
    fun getAllFiltersTest() = runTest {
        val filterList = listOf(Filter(filter = TestUtils.TEST_FILTER), Filter(filter = "mockFilter2"))
        Mockito.`when`(filterRepository.allFilters())
            .thenReturn(filterList)
        val resultFilterList = mainUseCase.getAllFilters()
        assertEquals(filterList, resultFilterList)
    }

    @Test
    fun getSystemContactListTest() = runTest {
        val contactList = arrayListOf(Contact(name = TestUtils.TEST_NAME))
        Mockito.`when`(contactRepository.getSystemContactList(eq(application), any()))
            .thenReturn(contactList)
        val resultContactList = mainUseCase.getSystemContactList(eq(application), any())
        assertEquals(contactList, resultContactList)
    }

    @Test
    fun setFilterToContactTest() = runTest {
        val filterList = listOf(Filter(filter = TestUtils.TEST_FILTER))
        val contactList = listOf(Contact(name = TestUtils.TEST_NAME))
        Mockito.`when`(contactRepository.setFilterToContact(eq(filterList), eq(contactList), any()))
            .thenReturn(contactList)
        val resultContactList = mainUseCase.setFilterToContact(eq(filterList), eq(contactList), any())
        assertEquals(contactList, resultContactList)
    }

    @Test
    fun insertContactsTest() = runTest {
        val contactList = listOf(Contact(), Contact())
        mainUseCase.insertContacts(contactList)
        verify(contactRepository, times(1)).insertContacts(contactList)
    }

    @Test
    fun getSystemLogCallListTest() = runTest {
        val logCallList = listOf(LogCall().apply { number = TestUtils.TEST_NUMBER })
        Mockito.`when`(logCallRepository.getSystemLogCallList(eq(application), any()))
            .thenReturn(logCallList)
        val resultLogCallList = mainUseCase.getSystemLogCallList(eq(application), any())
        assertEquals(logCallList, resultLogCallList)
    }

    @Test
    fun setFilterToLogCallTest() = runTest {
        val filterList = listOf(Filter(filter = TestUtils.TEST_FILTER))
        val logCallList = listOf(LogCall().apply { number = TestUtils.TEST_NUMBER })
        Mockito.`when`(logCallRepository.setFilterToLogCall(eq(filterList), eq(logCallList), any()))
            .thenReturn(logCallList)
        val resultLogCallList = mainUseCase.setFilterToLogCall(eq(filterList), eq(logCallList), any())
        assertEquals(logCallList, resultLogCallList)
    }

    @Test
    fun getAllFilteredCallsTest() = runTest {
        val filteredCallList = listOf(FilteredCall().apply { number = TestUtils.TEST_NUMBER })
        Mockito.`when`(filteredCallRepository.allFilteredCalls())
            .thenReturn(filteredCallList)
        val resultFilteredCallList = mainUseCase.getAllFilteredCalls()
        assertEquals(filteredCallList, resultFilteredCallList)
    }

    @Test
    fun setFilterToFilteredCallTest() = runTest {
        val filterList = listOf(Filter(filter = TestUtils.TEST_FILTER))
        val filteredCallList = listOf(FilteredCall().apply { number = TestUtils.TEST_NUMBER })
        Mockito.`when`(filteredCallRepository.setFilterToFilteredCall(eq(filterList), eq(filteredCallList), any()))
            .thenReturn(filteredCallList)
        val resultFilteredCallList = mainUseCase.setFilterToFilteredCall(eq(filterList), eq(filteredCallList), any())
        assertEquals(filteredCallList, resultFilteredCallList)
    }

    @Test
    fun insertAllFilteredCallsTest() = runTest {
        val filteredCallList = listOf(FilteredCall(), FilteredCall())
        mainUseCase.insertAllFilteredCalls(filteredCallList)
        verify(filteredCallRepository, times(1)).insertAllFilteredCalls(filteredCallList)
    }
}