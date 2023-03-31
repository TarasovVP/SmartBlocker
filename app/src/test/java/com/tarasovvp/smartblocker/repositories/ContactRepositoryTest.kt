package com.tarasovvp.smartblocker.repositories

import com.nhaarman.mockitokotlin2.*
import com.tarasovvp.smartblocker.TestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.data.database.dao.ContactDao
import com.tarasovvp.smartblocker.data.repositoryImpl.ContactRepositoryImpl
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.domain.models.entities.Contact
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.domain.repository.ContactRepository
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKER
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class ContactRepositoryTest {

    @Mock
    private lateinit var contactDao: ContactDao

    private lateinit var contactRepository: ContactRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        contactRepository = ContactRepositoryImpl(contactDao)
    }

    @Test
    fun insertContactsTest() = runTest {
        val contactList = listOf(Contact().apply { number = TEST_NUMBER }, Contact())
        contactRepository.insertContacts(contactList)
        verify(contactDao, times(1)).insertAllContacts(contactList)
    }

    @Test
    fun setFilterToContactTest() = runTest {
        val filterList = listOf(
            Filter("1234567890", conditionType = FilterCondition.FILTER_CONDITION_FULL.index),
            Filter("345", conditionType = FilterCondition.FILTER_CONDITION_START.index),
            Filter("789", conditionType = FilterCondition.FILTER_CONDITION_CONTAIN.index)
        )
        val contactList = listOf(
            Contact(number = "1234567890"),
            Contact(number = "3456789012"),
            Contact(number = "5678901234")
        )

        val resultMock = mock<(Int, Int) -> Unit>()
        val modifiedContacts = contactRepository.setFilterToContact(filterList, contactList, resultMock)

        assertEquals("1234567890", modifiedContacts[0].filter)
        assertEquals("345", modifiedContacts[1].filter)
        assertEquals("789", modifiedContacts[2].filter)
        verify(resultMock, times(contactList.size)).invoke(eq(contactList.size), any())
    }

    @Test
    fun getContactsWithFiltersTest() = runTest {

    }

    @Test
    fun getContactsWithFilterByFilterTest() = runTest {

    }

    @Test
    fun getSystemContactListTest() = runTest {

    }

    @Test
    fun getHashMapFromContactListTest() = runTest {

    }

    @Test
    fun filteredNumberDataListTest() = runTest {

    }
}