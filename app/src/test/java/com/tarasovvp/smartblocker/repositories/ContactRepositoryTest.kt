package com.tarasovvp.smartblocker.repositories

import com.tarasovvp.smartblocker.data.database.dao.ContactDao
import com.tarasovvp.smartblocker.data.repositoryImpl.ContactRepositoryImpl
import com.tarasovvp.smartblocker.domain.repository.ContactRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    suspend fun insertContactsTest() {

    }

    @Test
    suspend fun setFilterToContactTest() {

    }

    @Test
    suspend fun getContactsWithFiltersTest() {

    }

    @Test
    suspend fun getContactsWithFilterByFilterTest() {

    }

    @Test
    suspend fun getSystemContactListTest() {

    }

    @Test
    suspend fun getHashMapFromContactListTest() {

    }

    @Test
    suspend fun filteredNumberDataListTest() {

    }
}