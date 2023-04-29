package com.tarasovvp.smartblocker.usecases

import android.app.Application
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.tarasovvp.smartblocker.UnitTestUtils
import com.tarasovvp.smartblocker.domain.models.entities.*
import com.tarasovvp.smartblocker.domain.repository.*
import com.tarasovvp.smartblocker.domain.usecase.main.MainUseCase
import com.tarasovvp.smartblocker.domain.usecase.main.MainUseCaseImpl
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

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

    private lateinit var mainUseCase: MainUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mainUseCase = MainUseCaseImpl(contactRepository, countryCodeRepository, filterRepository, logCallRepository, filteredCallRepository, realDataBaseRepository)
    }

    @Test
    fun getCurrentUserTest() = runBlocking {
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
    fun insertUserFiltersTest() = runBlocking {
        val filterList = listOf(Filter(filter = UnitTestUtils.TEST_FILTER), Filter(filter = "mockFilter2"))
        mainUseCase.insertAllFilters(filterList)
        verify(filterRepository, times(1)).insertAllFilters(filterList)
    }

    @Test
    fun insertUserFilteredCallsTest() = runBlocking {
        val filteredCallList = listOf(FilteredCall().apply { number = UnitTestUtils.TEST_FILTER }, FilteredCall().apply { number = UnitTestUtils.TEST_FILTER })
        mainUseCase.insertAllFilteredCalls(filteredCallList)
        verify(filteredCallRepository, times(1)).insertAllFilteredCalls(filteredCallList)
    }

    @Test
    fun getSystemCountryCodeListTest() = runBlocking {
        val countryCodeList = listOf(CountryCode(country = UnitTestUtils.TEST_COUNTRY))
        Mockito.`when`(countryCodeRepository.getSystemCountryCodeList(any()))
            .thenReturn(countryCodeList)
        val resultCountryCodeList = mainUseCase.getSystemCountryCodeList(any())
        assertEquals(countryCodeList, resultCountryCodeList)
    }

    @Test
    fun insertAllCountryCodesTest() = runBlocking {
        val countryCodeList = listOf(CountryCode(), CountryCode())
        mainUseCase.insertAllCountryCodes(countryCodeList)
        verify(countryCodeRepository, times(1)).insertAllCountryCodes(countryCodeList)
    }

    @Test
    fun getAllFiltersTest() = runBlocking {
        val filterList = listOf(Filter(filter = UnitTestUtils.TEST_FILTER), Filter(filter = "mockFilter2"))
        Mockito.`when`(filterRepository.allFilters())
            .thenReturn(filterList)
        val resultFilterList = mainUseCase.getAllFilters()
        assertEquals(filterList, resultFilterList)
    }

    @Test
    fun getSystemContactListTest() = runBlocking {
        val contactList = arrayListOf(Contact(name = UnitTestUtils.TEST_NAME))
        Mockito.`when`(contactRepository.getSystemContactList(eq(application), any()))
            .thenReturn(contactList)
        val resultContactList = mainUseCase.getSystemContactList(eq(application), any())
        assertEquals(contactList, resultContactList)
    }

    @Test
    fun setFilterToContactTest() = runBlocking {
        val filterList = listOf(Filter(filter = UnitTestUtils.TEST_FILTER))
        val contactList = listOf(Contact(name = UnitTestUtils.TEST_NAME))
        Mockito.`when`(contactRepository.setFilterToContact(eq(filterList), eq(contactList), any()))
            .thenReturn(contactList)
        val resultContactList = mainUseCase.setFilterToContact(eq(filterList), eq(contactList), any())
        assertEquals(contactList, resultContactList)
    }

    @Test
    fun insertContactsTest() = runBlocking {
        val contactList = listOf(Contact(), Contact())
        mainUseCase.insertContacts(contactList)
        verify(contactRepository, times(1)).insertContacts(contactList)
    }

    @Test
    fun getSystemLogCallListTest() = runBlocking {
        val logCallList = listOf(LogCall().apply { number = UnitTestUtils.TEST_NUMBER })
        Mockito.`when`(logCallRepository.getSystemLogCallList(eq(application), any()))
            .thenReturn(logCallList)
        val resultLogCallList = mainUseCase.getSystemLogCallList(eq(application), any())
        assertEquals(logCallList, resultLogCallList)
    }

    @Test
    fun setFilterToLogCallTest() = runBlocking {
        val filterList = listOf(Filter(filter = UnitTestUtils.TEST_FILTER))
        val logCallList = listOf(LogCall().apply { number = UnitTestUtils.TEST_NUMBER })
        Mockito.`when`(logCallRepository.setFilterToLogCall(eq(filterList), eq(logCallList), any()))
            .thenReturn(logCallList)
        val resultLogCallList = mainUseCase.setFilterToLogCall(eq(filterList), eq(logCallList), any())
        assertEquals(logCallList, resultLogCallList)
    }

    @Test
    fun getAllFilteredCallsTest() = runBlocking {
        val filteredCallList = listOf(FilteredCall().apply { number = UnitTestUtils.TEST_NUMBER })
        Mockito.`when`(filteredCallRepository.allFilteredCalls())
            .thenReturn(filteredCallList)
        val resultFilteredCallList = mainUseCase.getAllFilteredCalls()
        assertEquals(filteredCallList, resultFilteredCallList)
    }

    @Test
    fun setFilterToFilteredCallTest() = runBlocking {
        val filterList = listOf(Filter(filter = UnitTestUtils.TEST_FILTER))
        val filteredCallList = listOf(FilteredCall().apply { number = UnitTestUtils.TEST_NUMBER })
        Mockito.`when`(filteredCallRepository.setFilterToFilteredCall(eq(filterList), eq(filteredCallList), any()))
            .thenReturn(filteredCallList)
        val resultFilteredCallList = mainUseCase.setFilterToFilteredCall(eq(filterList), eq(filteredCallList), any())
        assertEquals(filteredCallList, resultFilteredCallList)
    }

    @Test
    fun insertAllFilteredCallsTest() = runBlocking {
        val filteredCallList = listOf(FilteredCall(), FilteredCall())
        mainUseCase.insertAllFilteredCalls(filteredCallList)
        verify(filteredCallRepository, times(1)).insertAllFilteredCalls(filteredCallList)
    }
}