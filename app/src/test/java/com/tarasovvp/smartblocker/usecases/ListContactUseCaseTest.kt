package com.tarasovvp.smartblocker.usecases

import com.tarasovvp.smartblocker.TestUtils.TEST_NAME
import com.tarasovvp.smartblocker.domain.models.database_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.models.entities.Contact
import com.tarasovvp.smartblocker.domain.repository.ContactRepository
import com.tarasovvp.smartblocker.domain.usecase.number.list.list_contact.ListContactUseCase
import com.tarasovvp.smartblocker.domain.usecase.number.list.list_contact.ListContactUseCaseImpl
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ListContactUseCaseTest {

    @Mock
    private lateinit var contactRepository: ContactRepository

    private lateinit var listContactUseCase: ListContactUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        listContactUseCase = ListContactUseCaseImpl(contactRepository)
    }

    @Test
    fun getContactsWithFiltersTest() = runTest {
        val contactList = listOf(ContactWithFilter(contact = Contact(name = TEST_NAME)))
        Mockito.`when`(contactRepository.getContactsWithFilters())
            .thenReturn(contactList)
        val result = listContactUseCase.getContactsWithFilters()
        assertEquals(TEST_NAME, result[0].contact?.name)
    }

    @Test
    fun getHashMapFromContactListTest() = runTest {
        val contactList = listOf(ContactWithFilter(contact = Contact(name = TEST_NAME)), ContactWithFilter(contact = Contact(name = "zxy")))
        val contactMap = mapOf("a" to contactList)
        Mockito.`when`(contactRepository.getHashMapFromContactList(contactList))
            .thenReturn(contactMap)
        val result = listContactUseCase.getHashMapFromContactList(contactList)
        assertEquals(TEST_NAME, result.get("a")?.get(0)?.contact?.name)
    }
}