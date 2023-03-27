package com.tarasovvp.smartblocker.viewmodels

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.tarasovvp.smartblocker.TestUtils.getOrAwaitValue
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

@Suppress("UNCHECKED_CAST")
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
        val mockFilter = "mockFilter"
        val filterList = listOf(Filter(filter = mockFilter), Filter(filter = "mockFilter2"))
        viewModel.insertUserFilters(filterList)
        verify(mainUseCase, times(1)).insertAllFilters(filterList)
    }

    @Test
    fun insertUserFilteredCalls() = runTest {
        val mockNumber = "mockFilter"
        val filteredCallList = listOf(FilteredCall().apply { number = mockNumber }, FilteredCall().apply { number = mockNumber })
        viewModel.insertUserFilteredCalls(filteredCallList)
        verify(mainUseCase, times(1)).insertAllFilteredCalls(filteredCallList)
    }

    @Test
    fun getSystemCountryCodeList() = runTest {
        //mainUseCase.getSystemCountryCodeList()
    }

    @Test
    fun insertAllCountryCodes() = runTest {
        //mainUseCase.insertAllCountryCodes()
    }

    @Test
    fun getSystemContactList() = runTest {
        //mainUseCase.getSystemContactList(application)
    }

    @Test
    fun setFilterToContact() = runTest {
        //mainUseCase.setFilterToContact(filterList, contactList)
    }

    @Test
    fun insertContacts() = runTest {
        // mainUseCase.insertContacts(contactList)
    }

    @Test
    fun getSystemLogCallList() = runTest {
        //mainUseCase.getSystemLogCallList(application)
    }

    @Test
    fun setFilterToLogCall() = runTest {
        //mainUseCase.setFilterToLogCall(filterList, logCallList)
    }

    @Test
    fun getAllFilteredCalls() = runTest {
        //mainUseCase.getAllFilteredCalls()
    }

    @Test
    fun setFilterToFilteredCall() = runTest {
        //mainUseCase.setFilterToFilteredCall(filterList, filteredCallList)
    }

    @Test
    fun insertAllFilteredCalls() = runTest {
        //mainUseCase.insertAllFilteredCalls(filteredCallList)
    }
}