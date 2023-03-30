package com.tarasovvp.smartblocker.repositories

import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.tarasovvp.smartblocker.TestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.data.database.dao.ContactDao
import com.tarasovvp.smartblocker.data.repositoryImpl.ContactRepositoryImpl
import com.tarasovvp.smartblocker.domain.models.entities.Contact
import com.tarasovvp.smartblocker.domain.repository.ContactRepository
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