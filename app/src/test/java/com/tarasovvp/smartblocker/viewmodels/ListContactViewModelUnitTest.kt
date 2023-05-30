package com.tarasovvp.smartblocker.viewmodels

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NAME
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.UnitTestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.domain.enums.NumberDataFiltering
import com.tarasovvp.smartblocker.domain.entities.db_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.entities.db_entities.Contact
import com.tarasovvp.smartblocker.domain.usecases.ListContactUseCase
import com.tarasovvp.smartblocker.presentation.main.number.list.list_contact.ListContactViewModel
import com.tarasovvp.smartblocker.presentation.mappers.ContactWithFilterUIMapper
import com.tarasovvp.smartblocker.presentation.ui_models.ContactWithFilterUIModel
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Test

@ExperimentalCoroutinesApi
class ListContactViewModelUnitTest: BaseViewModelUnitTest<ListContactViewModel>() {


    @MockK
    private lateinit var useCase: ListContactUseCase

    @MockK
    private lateinit var contactWithFilterUIMapper: ContactWithFilterUIMapper

    override fun createViewModel() = ListContactViewModel(application, useCase, contactWithFilterUIMapper)

    @Test
    fun getContactsWithFiltersTest() = runTest {
        val contactList = listOf(ContactWithFilter(contact = Contact(name = TEST_NAME)), ContactWithFilter(contact = Contact(name = TEST_NUMBER)))
        val contactUIModelList = listOf(ContactWithFilterUIModel(contactName = TEST_NAME), ContactWithFilterUIModel(contactName = TEST_NUMBER))
        coEvery { useCase.allContactWithFilters() } returns contactList
        every { contactWithFilterUIMapper.mapToUIModelList(contactList) } returns contactUIModelList
        viewModel.getContactsWithFilters(false)
        advanceUntilIdle()
        coVerify { useCase.allContactWithFilters() }
        verify { contactWithFilterUIMapper.mapToUIModelList(contactList) }
        assertEquals(contactUIModelList, viewModel.contactListLiveData.getOrAwaitValue())
    }

    @Test
    fun getFilteredContactListTest() = runTest {
        val numberDataFilters = arrayListOf(NumberDataFiltering.CONTACT_WITH_BLOCKER.ordinal)
        val contactList = listOf(ContactWithFilter(contact = Contact(name = TEST_NAME)), ContactWithFilter(contact = Contact(name = TEST_NUMBER)))
        val contactUIModelList = listOf(ContactWithFilterUIModel(contactName = TEST_NAME), ContactWithFilterUIModel(contactName = TEST_NUMBER))
        coEvery { useCase.getFilteredContactList(contactList, String.EMPTY, arrayListOf(NumberDataFiltering.CONTACT_WITH_BLOCKER.ordinal)) } returns contactList
        every { contactWithFilterUIMapper.mapToUIModelList(contactList) } returns contactUIModelList
        every { contactWithFilterUIMapper.mapFromUIModelList(contactUIModelList) } returns contactList
        viewModel.getFilteredContactList(contactUIModelList, String.EMPTY, numberDataFilters)
        advanceUntilIdle()
        coVerify { useCase.getFilteredContactList(contactList, String.EMPTY, numberDataFilters) }
        verify { contactWithFilterUIMapper.mapToUIModelList(contactList) }
        verify { contactWithFilterUIMapper.mapFromUIModelList(contactUIModelList) }
        assertEquals(contactUIModelList, viewModel.filteredContactListLiveData.getOrAwaitValue())
    }

    @Test
    fun getHashMapFromContactListTest() {
        val contactUIModelList = listOf(ContactWithFilterUIModel(contactName = TEST_NAME), ContactWithFilterUIModel(contactName = "testName2"))
        val contactMap = linkedMapOf("t" to contactUIModelList)
        viewModel.getHashMapFromContactList(contactUIModelList, false)
        assertEquals(contactMap, viewModel.contactHashMapLiveData.getOrAwaitValue())
    }
}