package com.tarasovvp.smartblocker.viewmodels

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_COUNTRY
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_FILTER
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NAME
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.UnitTestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.domain.models.entities.*
import com.tarasovvp.smartblocker.domain.usecase.main.MainUseCase
import com.tarasovvp.smartblocker.presentation.MainViewModel
import io.mockk.*
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Test

@ExperimentalCoroutinesApi
class MainViewModelTest: BaseViewModelTest<MainViewModel>() {

    @MockK
    private lateinit var mainUseCase: MainUseCase

    override fun createViewModel() = MainViewModel(application, mainUseCase)

    @Test
    fun getCurrentUser() = runTest {
        val currentUser = CurrentUser()
        every { mainUseCase.getCurrentUser(any()) } answers {
            val result = firstArg<(CurrentUser) -> Unit>()
            result.invoke(currentUser)
        }
        viewModel.getCurrentUser()
        advanceUntilIdle()
        val result = viewModel.currentUserLiveData.getOrAwaitValue()
        assertEquals(result, currentUser)
    }

    @Test
    fun insertUserFilters() = runTest {
        val filterList = listOf(Filter(filter = TEST_FILTER), Filter(filter = "mockFilter2"))
        coEvery { mainUseCase.insertAllFilters(filterList) } just Runs
        viewModel.insertUserFilters(filterList)
        coVerify { mainUseCase.insertAllFilters(filterList) }
    }

    @Test
    fun insertUserFilteredCalls() = runTest {
        val filteredCallList = listOf(FilteredCall().apply { number = TEST_FILTER }, FilteredCall().apply { number = TEST_FILTER })
        coEvery { mainUseCase.insertAllFilteredCalls(filteredCallList) } just Runs
        viewModel.insertUserFilteredCalls(filteredCallList)
        coVerify { mainUseCase.insertAllFilteredCalls(filteredCallList) }
    }

    @Test
    fun getSystemCountryCodeList() = runTest {
        val countryCodeList = listOf(CountryCode(country = TEST_COUNTRY))
        coEvery { mainUseCase.getSystemCountryCodeList(any()) } returns countryCodeList
        val resultCountryCodeList = viewModel.getSystemCountryCodeList()
        assertEquals(countryCodeList, resultCountryCodeList)
    }

    @Test
    fun insertAllCountryCodes() = runTest {
        val countryCodeList = listOf(CountryCode(), CountryCode())
        coEvery { mainUseCase.insertAllCountryCodes(countryCodeList) } just Runs
        viewModel.insertAllCountryCodes(countryCodeList)
        coVerify { mainUseCase.insertAllCountryCodes(countryCodeList) }
    }

    @Test
    fun getSystemContactList() = runTest {
        val contactList = listOf(Contact(name = TEST_NAME))
        coEvery { mainUseCase.getSystemContactList(eq(application), any()) } returns contactList
        val resultContactList = viewModel.getSystemContactList()
        assertEquals(contactList, resultContactList)
    }

    @Test
    fun setFilterToContact() = runTest {
        val filterList = listOf(Filter(filter = TEST_FILTER))
        val contactList = listOf(Contact(name = TEST_NAME))
        coEvery { mainUseCase.setFilterToContact(eq(filterList), eq(contactList), any()) } returns contactList
        val resultContactList = viewModel.setFilterToContact(filterList, contactList)
        assertEquals(contactList, resultContactList)
    }

    @Test
    fun insertContacts() = runTest {
        val contactList = listOf(Contact(), Contact())
        coEvery { mainUseCase.insertContacts(contactList) } just Runs
        viewModel.insertContacts(contactList)
        coVerify { mainUseCase.insertContacts(contactList) }
    }

    @Test
    fun getSystemLogCallList() = runTest {
        val logCallList = listOf(LogCall().apply { number = TEST_NUMBER })
        coEvery { mainUseCase.getSystemLogCallList(eq(application), any()) } returns logCallList
        val resultLogCallList = viewModel.getSystemLogCallList()
        assertEquals(logCallList, resultLogCallList)
    }

    @Test
    fun setFilterToLogCall() = runTest {
        val filterList = listOf(Filter(filter = TEST_FILTER))
        val logCallList = listOf(LogCall().apply { number = TEST_NUMBER })
        coEvery { mainUseCase.setFilterToLogCall(eq(filterList), eq(logCallList), any()) } returns logCallList
        val resultLogCallList = viewModel.setFilterToLogCall(filterList, logCallList)
        assertEquals(logCallList, resultLogCallList)
    }

    @Test
    fun getAllFilteredCalls() = runTest {
        val filteredCallList = listOf(FilteredCall().apply { number = TEST_NUMBER })
        coEvery { mainUseCase.getAllFilteredCalls() } returns filteredCallList
        val resultFilteredCallList = viewModel.getAllFilteredCalls()
        assertEquals(filteredCallList, resultFilteredCallList)
    }

    @Test
    fun setFilterToFilteredCall() = runTest {
        val filterList = listOf(Filter(filter = TEST_FILTER))
        val filteredCallList = listOf(FilteredCall().apply { number = TEST_NUMBER })
        coEvery { mainUseCase.setFilterToFilteredCall(eq(filterList), eq(filteredCallList), any()) } returns filteredCallList
        val resultFilteredCallList = viewModel.setFilterToFilteredCall(filterList, filteredCallList)
        assertEquals(filteredCallList, resultFilteredCallList)
    }

    @Test
    fun insertAllFilteredCalls() = runTest {
        val filteredCallList = listOf(FilteredCall(), FilteredCall())
        coEvery { mainUseCase.insertAllFilteredCalls(filteredCallList) } just Runs
        viewModel.insertAllFilteredCalls(filteredCallList)
        coVerify { mainUseCase.insertAllFilteredCalls(filteredCallList) }
    }
}