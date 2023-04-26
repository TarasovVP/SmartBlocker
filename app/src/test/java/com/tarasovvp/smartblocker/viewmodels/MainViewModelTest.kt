package com.tarasovvp.smartblocker.viewmodels

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_COUNTRY
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_FILTER
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NAME
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.UnitTestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.domain.models.entities.*
import com.tarasovvp.smartblocker.domain.usecase.main.MainUseCase
import com.tarasovvp.smartblocker.presentation.MainViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest: BaseViewModelTest<MainViewModel>() {

    @Mock
    private lateinit var mainUseCase: MainUseCase

    override fun createViewModel() = MainViewModel(application, mainUseCase)

    @Test
    fun getCurrentUser() = runTest {
        val currentUser = CurrentUser()
        Mockito.doAnswer {
            @Suppress("UNCHECKED_CAST")
            val result = it.arguments[0] as (CurrentUser) -> Unit
            result.invoke(currentUser)
        }.`when`(mainUseCase).getCurrentUser(any())
        viewModel.getCurrentUser()
        advanceUntilIdle()
        val result = viewModel.currentUserLiveData.getOrAwaitValue()
        assertEquals(result, currentUser)
    }

    @Test
    fun insertUserFilters() = runTest {
        val filterList = listOf(Filter(filter = TEST_FILTER), Filter(filter = "mockFilter2"))
        viewModel.insertUserFilters(filterList)
        verify(mainUseCase, times(1)).insertAllFilters(filterList)
    }

    @Test
    fun insertUserFilteredCalls() = runTest {
        val filteredCallList = listOf(FilteredCall().apply { number = TEST_FILTER }, FilteredCall().apply { number = TEST_FILTER })
        viewModel.insertUserFilteredCalls(filteredCallList)
        verify(mainUseCase, times(1)).insertAllFilteredCalls(filteredCallList)
    }

    @Test
    fun getSystemCountryCodeList() = runTest {
        val countryCodeList = listOf(CountryCode(country = TEST_COUNTRY))
        Mockito.`when`(mainUseCase.getSystemCountryCodeList(any()))
            .thenReturn(countryCodeList)
        val resultCountryCodeList = viewModel.getSystemCountryCodeList()
        assertEquals(countryCodeList, resultCountryCodeList)
    }

    @Test
    fun insertAllCountryCodes() = runTest {
        val countryCodeList = listOf(CountryCode(), CountryCode())
        viewModel.insertAllCountryCodes(countryCodeList)
        verify(mainUseCase, times(1)).insertAllCountryCodes(countryCodeList)
    }

    @Test
    fun getSystemContactList() = runTest {
        val contactList = listOf(Contact(name = TEST_NAME))
        Mockito.`when`(mainUseCase.getSystemContactList(eq(application), any()))
            .thenReturn(contactList)
        val resultContactList = viewModel.getSystemContactList()
        assertEquals(contactList, resultContactList)
    }

    @Test
    fun setFilterToContact() = runTest {
        val filterList = listOf(Filter(filter = TEST_FILTER))
        val contactList = listOf(Contact(name = TEST_NAME))
        Mockito.`when`(mainUseCase.setFilterToContact(eq(filterList), eq(contactList), any()))
            .thenReturn(contactList)
        val resultContactList = viewModel.setFilterToContact(filterList, contactList)
        assertEquals(contactList, resultContactList)
    }

    @Test
    fun insertContacts() = runTest {
        val contactList = listOf(Contact(), Contact())
        viewModel.insertContacts(contactList)
        verify(mainUseCase, times(1)).insertContacts(contactList)
    }

    @Test
    fun getSystemLogCallList() = runTest {
        val logCallList = listOf(LogCall().apply { number = TEST_NUMBER })
        Mockito.`when`(mainUseCase.getSystemLogCallList(eq(application), any()))
            .thenReturn(logCallList)
        val resultLogCallList = viewModel.getSystemLogCallList()
        assertEquals(logCallList, resultLogCallList)
    }

    @Test
    fun setFilterToLogCall() = runTest {
        val filterList = listOf(Filter(filter = TEST_FILTER))
        val logCallList = listOf(LogCall().apply { number = TEST_NUMBER })
        Mockito.`when`(mainUseCase.setFilterToLogCall(eq(filterList), eq(logCallList), any()))
            .thenReturn(logCallList)
        val resultLogCallList = viewModel.setFilterToLogCall(filterList, logCallList)
        assertEquals(logCallList, resultLogCallList)
    }

    @Test
    fun getAllFilteredCalls() = runTest {
        val filteredCallList = listOf(FilteredCall().apply { number = TEST_NUMBER })
        Mockito.`when`(mainUseCase.getAllFilteredCalls())
            .thenReturn(filteredCallList)
        val resultFilteredCallList = viewModel.getAllFilteredCalls()
        assertEquals(filteredCallList, resultFilteredCallList)
    }

    @Test
    fun setFilterToFilteredCall() = runTest {
        val filterList = listOf(Filter(filter = TEST_FILTER))
        val filteredCallList = listOf(FilteredCall().apply { number = TEST_NUMBER })
        Mockito.`when`(mainUseCase.setFilterToFilteredCall(eq(filterList), eq(filteredCallList), any()))
            .thenReturn(filteredCallList)
        val resultFilteredCallList = viewModel.setFilterToFilteredCall(filterList, filteredCallList)
        assertEquals(filteredCallList, resultFilteredCallList)
    }

    @Test
    fun insertAllFilteredCalls() = runTest {
        val filteredCallList = listOf(FilteredCall(), FilteredCall())
        viewModel.insertAllFilteredCalls(filteredCallList)
        verify(mainUseCase, times(1)).insertAllFilteredCalls(filteredCallList)
    }
}