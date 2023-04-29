package com.tarasovvp.smartblocker.usecases

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NAME
import com.tarasovvp.smartblocker.domain.enums.NumberDataFiltering
import com.tarasovvp.smartblocker.domain.models.database_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.models.entities.Contact
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.domain.repository.ContactRepository
import com.tarasovvp.smartblocker.domain.usecase.number.list.list_contact.ListContactUseCase
import com.tarasovvp.smartblocker.domain.usecase.number.list.list_contact.ListContactUseCaseImpl
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.utils.extensions.isTrue
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
    fun getContactsWithFiltersTest() = runBlocking {
        val contactList = listOf(ContactWithFilter(contact = Contact(name = TEST_NAME)))
        Mockito.`when`(contactRepository.getContactsWithFilters())
            .thenReturn(contactList)
        val result = listContactUseCase.getContactsWithFilters()
        assertEquals(TEST_NAME, result[0].contact?.name)
    }

    @Test
    fun getFilteredContactListTest() = runBlocking {
        val contactList = listOf(ContactWithFilter(contact = Contact(name = TEST_NAME), filterWithCountryCode = FilterWithCountryCode(filter = Filter(filterType = Constants.BLOCKER))), ContactWithFilter(contact = Contact(name = "zxy")))
        Mockito.`when`(contactRepository.getFilteredContactList(contactList, String.EMPTY, arrayListOf(NumberDataFiltering.CONTACT_WITH_BLOCKER.ordinal)))
            .thenReturn(contactList.filter { it.filterWithCountryCode?.filter?.isBlocker().isTrue() })
        val result = listContactUseCase.getFilteredContactList(contactList, String.EMPTY, arrayListOf(NumberDataFiltering.CONTACT_WITH_BLOCKER.ordinal))
        assertEquals(contactList.filter { it.filterWithCountryCode?.filter?.isBlocker().isTrue() }, result)
    }

    @Test
    fun getHashMapFromContactListTest() = runBlocking {
        val contactList = listOf(ContactWithFilter(contact = Contact(name = TEST_NAME)), ContactWithFilter(contact = Contact(name = "zxy")))
        val contactMap = mapOf("a" to contactList)
        Mockito.`when`(contactRepository.getHashMapFromContactList(contactList))
            .thenReturn(contactMap)
        val result = listContactUseCase.getHashMapFromContactList(contactList)
        assertEquals(TEST_NAME, result.get("a")?.get(0)?.contact?.name)
    }
}