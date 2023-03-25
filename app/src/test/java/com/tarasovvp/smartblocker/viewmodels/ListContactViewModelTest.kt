package com.tarasovvp.smartblocker.viewmodels

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.tarasovvp.smartblocker.TestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.domain.models.database_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.models.entities.Contact
import com.tarasovvp.smartblocker.domain.usecase.number.list.list_contact.ListContactUseCase
import com.tarasovvp.smartblocker.presentation.main.number.list.list_contact.ListContactViewModel
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class ListContactViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var application: Application

    @Mock
    private lateinit var listContactUseCase: ListContactUseCase

    private lateinit var viewModel: ListContactViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(StandardTestDispatcher())
        viewModel =
            ListContactViewModel(application, listContactUseCase)
    }

    @Test
    fun getContactsWithFiltersTest() = runTest {
        val contactName = "mockContact"
        val contactList = listOf(ContactWithFilter(contact = Contact(name = contactName)))
        Mockito.`when`(listContactUseCase.getContactsWithFilters())
            .thenReturn(contactList)
        viewModel.getContactsWithFilters(false)
        advanceUntilIdle()
        val result = viewModel.contactLiveData.getOrAwaitValue()
        assertEquals(contactName, result[0].contact?.name)
    }

    @Test
    fun getHashMapFromContactListTest() = runTest {
        val name = "abc"
        val contactList = listOf(ContactWithFilter(contact = Contact(name = name)), ContactWithFilter(contact = Contact(name = "zxy")))
        val contactMap = mapOf("a" to contactList)
        Mockito.`when`(listContactUseCase.getHashMapFromContactList(contactList))
            .thenReturn(contactMap)
        viewModel.getHashMapFromContactList(contactList, false)
        advanceUntilIdle()
        val result = viewModel.contactHashMapLiveData.getOrAwaitValue()
        assertEquals(name, result?.get("a")?.get(0)?.contact?.name)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}