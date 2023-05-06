package com.tarasovvp.smartblocker.viewmodels

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NAME
import com.tarasovvp.smartblocker.UnitTestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.domain.enums.NumberDataFiltering
import com.tarasovvp.smartblocker.domain.entities.db_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.entities.db_entities.Contact
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.usecases.ListContactUseCase
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.presentation.main.number.list.list_contact.ListContactViewModel
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Test

@ExperimentalCoroutinesApi
class ListContactViewModelTest: BaseViewModelTest<ListContactViewModel>() {


    @MockK
    private lateinit var useCase: ListContactUseCase
    override fun createViewModel() = ListContactViewModel(application, useCase)

    @Test
    fun getContactsWithFiltersTest() = runTest {
        val contactList = listOf(ContactWithFilter(contact = Contact(name = TEST_NAME)))
        coEvery { useCase.getContactsWithFilters() } returns contactList
        viewModel.getContactsWithFilters(false)
        advanceUntilIdle()
        val result = viewModel.contactListLiveData.getOrAwaitValue()
        assertEquals(TEST_NAME, result[0].contact?.name)
    }

    @Test
    fun getFilteredContactListTest() = runTest {
        val contactList = listOf(ContactWithFilter(contact = Contact(name = TEST_NAME), filterWithCountryCode = FilterWithCountryCode(filter = Filter(filterType = BLOCKER))), ContactWithFilter(contact = Contact(name = "zxy")))
        coEvery { useCase.getFilteredContactList(contactList, String.EMPTY, arrayListOf(NumberDataFiltering.CONTACT_WITH_BLOCKER.ordinal)) } returns contactList.filter { it.filterWithCountryCode?.filter?.isBlocker().isTrue() }
        viewModel.getFilteredContactList(contactList, String.EMPTY, arrayListOf(NumberDataFiltering.CONTACT_WITH_BLOCKER.ordinal))
        advanceUntilIdle()
        val result = viewModel.filteredContactListLiveData.getOrAwaitValue()
        assertEquals(contactList.filter { it.filterWithCountryCode?.filter?.isBlocker().isTrue() }, result)
    }

    @Test
    fun getHashMapFromContactListTest() = runTest {
        val contactList = listOf(ContactWithFilter(contact = Contact(name = TEST_NAME)), ContactWithFilter(contact = Contact(name = "zxy")))
        val contactMap = mapOf("a" to contactList)
        coEvery { useCase.getHashMapFromContactList(contactList) } returns contactMap
        viewModel.getHashMapFromContactList(contactList, false)
        advanceUntilIdle()
        val result = viewModel.contactHashMapLiveData.getOrAwaitValue()
        assertEquals(TEST_NAME, result?.get("a")?.get(0)?.contact?.name)
    }
}