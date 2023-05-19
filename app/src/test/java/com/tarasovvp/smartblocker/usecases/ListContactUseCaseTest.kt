package com.tarasovvp.smartblocker.usecases

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NAME
import com.tarasovvp.smartblocker.domain.enums.NumberDataFiltering
import com.tarasovvp.smartblocker.domain.entities.db_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithFilteredNumbers
import com.tarasovvp.smartblocker.domain.entities.db_entities.Contact
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.repository.ContactRepository
import com.tarasovvp.smartblocker.domain.usecases.ListContactUseCase
import com.tarasovvp.smartblocker.presentation.main.number.list.list_contact.ListContactUseCaseImpl
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class ListContactUseCaseTest {

    @MockK
    private lateinit var contactRepository: ContactRepository

    private lateinit var listContactUseCase: ListContactUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        listContactUseCase = ListContactUseCaseImpl(contactRepository)
    }

    @Test
    fun allContactWithFiltersTest() = runBlocking {
        val contactList = listOf(ContactWithFilter(contact = Contact(name = TEST_NAME)))
        coEvery { contactRepository.allContactWithFilters() } returns contactList
        val result = listContactUseCase.allContactWithFilters()
        assertEquals(contactList, result)
    }

    @Test
    fun getFilteredContactListTest() = runBlocking {
        val contactList = listOf(ContactWithFilter(contact = Contact(name = TEST_NAME), filterWithFilteredNumbers = FilterWithFilteredNumbers(filter = Filter(filterType = Constants.BLOCKER))), ContactWithFilter(contact = Contact(name = "zxy")))
        val searchQuery = String.EMPTY
        val filterIndexes = arrayListOf(NumberDataFiltering.CONTACT_WITH_BLOCKER.ordinal)
        val expectedContactList = listOf(
            ContactWithFilter(contact = Contact(name = TEST_NAME), filterWithFilteredNumbers = FilterWithFilteredNumbers(filter = Filter(filterType = Constants.BLOCKER))))
        val result = listContactUseCase.getFilteredContactList(contactList, searchQuery, filterIndexes)
        assertEquals(expectedContactList, result)
    }
}