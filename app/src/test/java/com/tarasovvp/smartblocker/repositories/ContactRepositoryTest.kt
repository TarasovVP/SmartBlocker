package com.tarasovvp.smartblocker.repositories

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_FILTER
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NAME
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.data.database.dao.ContactDao
import com.tarasovvp.smartblocker.data.repositoryImpl.ContactRepositoryImpl
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.domain.enums.NumberDataFiltering
import com.tarasovvp.smartblocker.presentation.ui_models.NumberDataUIModel
import com.tarasovvp.smartblocker.domain.entities.db_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.entities.db_entities.Contact
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.repository.ContactRepository
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
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
    private lateinit var contactDao: ContactDao

    private lateinit var contactRepository: ContactRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        contactRepository = ContactRepositoryImpl(contactDao)
    }

    @Test
    fun insertContactsTest() = runBlocking {
        val contactList = listOf(Contact().apply { number = TEST_NUMBER }, Contact())
        coEvery { contactDao.insertAllContacts(contactList) } just Runs
        contactRepository.insertContacts(contactList)
        coVerify(exactly = 1) { contactDao.insertAllContacts(contactList) }
    }

    @Test
    fun setFilterToContactTest() = runBlocking {
        val filterList = listOf(
            Filter("1234567890", conditionType = FilterCondition.FILTER_CONDITION_FULL.ordinal),
            Filter("345", conditionType = FilterCondition.FILTER_CONDITION_START.ordinal),
            Filter("789", conditionType = FilterCondition.FILTER_CONDITION_CONTAIN.ordinal)
        )
        val contactList = listOf(
            Contact(number = "1234567890"),
            Contact(number = "3456789012"),
            Contact(number = "5678901234")
        )

        val resultMock = mockk<(Int, Int) -> Unit>(relaxed = true)
        val modifiedContacts = contactRepository.setFilterToContact(filterList, contactList, resultMock)

        assertEquals("1234567890", modifiedContacts[0].filter)
        assertEquals("345", modifiedContacts[1].filter)
        assertEquals("789", modifiedContacts[2].filter)
        verify(exactly = contactList.size) { resultMock.invoke(eq(contactList.size), any()) }
    }

    @Test
    fun getContactsWithFiltersTest() = runBlocking {
        val contactWithFilterList = listOf(ContactWithFilter().apply { Contact().apply { number = TEST_NUMBER }}, ContactWithFilter().apply {Contact()})
        coEvery { contactDao.allContactsWithFilters() } returns contactWithFilterList
        val result = contactRepository.allContactWithFilters()
        assertEquals(contactWithFilterList, result)
    }

    @Test
    fun getContactsWithFilterByFilterTest() = runBlocking {
        val contactWithFilterList = listOf(ContactWithFilter().apply { Contact().apply { number = TEST_NUMBER }}, ContactWithFilter().apply {Contact()})
        coEvery { contactDao.allContactsWithFiltersByFilter(TEST_FILTER) } returns contactWithFilterList
        val result = contactRepository.allContactsWithFiltersByFilter(TEST_FILTER)
        assertEquals(contactWithFilterList, result)
    }

    @Test
    fun getSystemContactListTest() = runBlocking {
        val contact = Contact()
        val context = mockk<Context>()
        val contentResolver = mockk<ContentResolver>()
        val cursor = mockk<Cursor>()
        val resultMock = mockk<(Int, Int) -> Unit>(relaxed = true)
        val expectedSize = 10

        every { context.contentResolver } returns contentResolver
        every { contentResolver.query(any(), any(), any(), any(), any()) } returns cursor
        every { cursor.count } returns expectedSize
        var index = 0
        every { cursor.moveToNext() } answers {
            index++ < expectedSize
        }
        every { cursor.getString(0) } returns contact.id
        every { cursor.getString(1) } returns contact.name
        every { cursor.getString(2) } returns contact.photoUrl
        every { cursor.getString(3) } returns contact.number
        every { cursor.close() } just Runs

        val contactList = contactRepository.getSystemContactList(context, resultMock)

        assertEquals(expectedSize, contactList.size)
        verify(exactly = expectedSize) { resultMock.invoke(any(), any()) }
        verify(exactly = 1) { context.contentResolver }
        verify(exactly = 1) { cursor.close() }
    }

    @Test
    fun getFilteredContactListTest() = runBlocking {
        val contactList = listOf(ContactWithFilter(contact = Contact(name = TEST_NAME), filterWithCountryCode = FilterWithCountryCode(filter = Filter(filterType = Constants.BLOCKER))), ContactWithFilter(contact = Contact(name = "zxy")))
        val result = contactRepository.getFilteredContactList(contactList, String.EMPTY, arrayListOf(
            NumberDataFiltering.CONTACT_WITH_BLOCKER.ordinal))
        assertEquals(contactList.filter { it.filterWithCountryCode?.filter?.isBlocker().isTrue() }, result)
    }

    @Test
    fun getHashMapFromContactListTest() = runBlocking {
        val contactWithFilterList = listOf(ContactWithFilter().apply { Contact().apply { name = TEST_NAME }}, ContactWithFilter().apply {Contact().apply { name = "abc" }})
        val result = contactRepository.getHashMapFromContactList(contactWithFilterList)
        assertEquals(contactWithFilterList.groupBy {
            if (it.contact?.name.isNullOrEmpty()) String.EMPTY else it.contact?.name?.get(0).toString()
        }, result)
    }

    @Test
    fun filteredNumberDataListTest() = runBlocking {
        val filter = Filter(filter = "123", conditionType = FilterCondition.FILTER_CONDITION_START.ordinal)
        val numberDataUIModelList = arrayListOf(ContactWithFilter().apply { Contact().apply { number = TEST_NUMBER }}, LogCallWithFilter().apply { Contact().apply { number = TEST_NUMBER }}, NumberDataUIModel().apply { Contact().apply { number = TEST_NUMBER }})
        val result = contactRepository.filteredNumberDataList(filter, numberDataUIModelList, 0)
        assertEquals(arrayListOf(numberDataUIModelList[1], numberDataUIModelList[2], numberDataUIModelList[0]), result)
    }
}