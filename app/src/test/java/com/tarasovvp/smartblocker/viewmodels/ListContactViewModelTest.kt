package com.tarasovvp.smartblocker.viewmodels

import com.tarasovvp.smartblocker.TestUtils.TEST_NAME
import com.tarasovvp.smartblocker.TestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.domain.models.database_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.models.entities.Contact
import com.tarasovvp.smartblocker.domain.usecase.number.list.list_contact.ListContactUseCase
import com.tarasovvp.smartblocker.presentation.main.number.list.list_contact.ListContactViewModel
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
class ListContactViewModelTest: BaseViewModelTest<ListContactViewModel>() {


    @Mock
    private lateinit var useCase: ListContactUseCase
    override fun createViewModel() = ListContactViewModel(application, useCase)

    @Test
    fun getContactsWithFiltersTest() = runTest {
        val contactList = listOf(ContactWithFilter(contact = Contact(name = TEST_NAME)))
        Mockito.`when`(useCase.getContactsWithFilters())
            .thenReturn(contactList)
        viewModel.getContactsWithFilters(false)
        advanceUntilIdle()
        val result = viewModel.contactLiveData.getOrAwaitValue()
        assertEquals(TEST_NAME, result[0].contact?.name)
    }

    @Test
    fun getHashMapFromContactListTest() = runTest {
        val contactList = listOf(ContactWithFilter(contact = Contact(name = TEST_NAME)), ContactWithFilter(contact = Contact(name = "zxy")))
        val contactMap = mapOf("a" to contactList)
        Mockito.`when`(useCase.getHashMapFromContactList(contactList))
            .thenReturn(contactMap)
        viewModel.getHashMapFromContactList(contactList, false)
        advanceUntilIdle()
        val result = viewModel.contactHashMapLiveData.getOrAwaitValue()
        assertEquals(TEST_NAME, result?.get("a")?.get(0)?.contact?.name)
    }
}