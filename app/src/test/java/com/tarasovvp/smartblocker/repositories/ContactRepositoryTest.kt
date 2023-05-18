package com.tarasovvp.smartblocker.repositories

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_COUNTRY
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_FILTER
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.data.database.dao.ContactDao
import com.tarasovvp.smartblocker.data.repositoryImpl.ContactRepositoryImpl
import com.tarasovvp.smartblocker.domain.entities.db_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.entities.db_entities.Contact
import com.tarasovvp.smartblocker.domain.repository.ContactRepository
import com.tarasovvp.smartblocker.utils.AppPhoneNumberUtil
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import io.mockk.*
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class ContactRepositoryTest {

    @MockK
    private lateinit var appPhoneNumberUtil: AppPhoneNumberUtil

    @MockK
    private lateinit var contactDao: ContactDao

    private lateinit var contactRepository: ContactRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        contactRepository = ContactRepositoryImpl(appPhoneNumberUtil, contactDao)
    }

    @Test
    fun getSystemContactListTest() = runBlocking {
        val contact = Contact()
        val context = mockk<Context>()
        val contentResolver = mockk<ContentResolver>()
        val cursor = mockk<Cursor>()
        val country = TEST_COUNTRY
        val resultMock = mockk<(Int, Int) -> Unit>(relaxed = true)
        val expectedSize = 10

        every { context.contentResolver } returns contentResolver
        every { contentResolver.query(any(), any(), any(), any(), any()) } returns cursor
        every { cursor.count } returns expectedSize
        var index = 0
        every { cursor.moveToNext() } answers {
            index++ < expectedSize
        }
        every { cursor.getString(0) } returns contact.contactId
        every { cursor.getString(1) } returns contact.name
        every { cursor.getString(2) } returns contact.photoUrl
        every { cursor.getString(3) } returns contact.number
        every { cursor.close() } just Runs
        every { appPhoneNumberUtil.phoneNumberValue(contact.number, country) } returns contact.phoneNumberValue.orEmpty()
        every { appPhoneNumberUtil.isPhoneNumberValid(contact.number, country) } returns contact.isPhoneNumberValid.isTrue()

        val contactList = contactRepository.getSystemContactList(context, country, resultMock)

        assertEquals(expectedSize, contactList.size)
        verify(exactly = expectedSize) { resultMock.invoke(any(), any()) }
        verify(exactly = 1) { context.contentResolver }
        verify(exactly = 1) { cursor.close() }
    }

    @Test
    fun insertContactsTest() = runBlocking {
        val contactList = listOf(Contact().apply { number = TEST_NUMBER }, Contact())
        coEvery { contactDao.insertAllContacts(contactList) } just Runs
        contactRepository.insertAllContacts(contactList)
        coVerify(exactly = 1) { contactDao.insertAllContacts(contactList) }
    }

    @Test
    fun allContactsWithFiltersTest() = runBlocking {
        val contactWithFilterList = listOf(ContactWithFilter().apply { Contact().apply { number = TEST_NUMBER }}, ContactWithFilter().apply {Contact()})
        coEvery { contactDao.allContactsWithFilters() } returns contactWithFilterList
        val result = contactRepository.allContactWithFilters()
        assertEquals(contactWithFilterList, result)
    }

    @Test
    fun allContactsWithFiltersByFilterTest() = runBlocking {
        val contactWithFilterList = listOf(ContactWithFilter().apply { Contact().apply { number = TEST_NUMBER }}, ContactWithFilter().apply {Contact()})
        coEvery { contactDao.allContactsWithFiltersByFilter(TEST_FILTER) } returns contactWithFilterList
        val result = contactRepository.allContactsWithFiltersByFilter(TEST_FILTER)
        assertEquals(contactWithFilterList, result)
    }
}