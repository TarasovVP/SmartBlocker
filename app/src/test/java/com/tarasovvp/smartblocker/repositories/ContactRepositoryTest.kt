package com.tarasovvp.smartblocker.repositories

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import com.nhaarman.mockitokotlin2.*
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_FILTER
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NAME
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.data.database.dao.ContactDao
import com.tarasovvp.smartblocker.data.repositoryImpl.ContactRepositoryImpl
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.domain.enums.NumberDataFiltering
import com.tarasovvp.smartblocker.domain.models.NumberData
import com.tarasovvp.smartblocker.domain.models.database_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.models.database_views.LogCallWithFilter
import com.tarasovvp.smartblocker.domain.models.entities.Contact
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.domain.repository.ContactRepository
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
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
        val contactWithFilterList = listOf(ContactWithFilter().apply { Contact().apply { number = TEST_NUMBER }}, ContactWithFilter().apply {Contact()})
        Mockito.`when`(contactDao.getContactsWithFilters())
            .thenReturn(contactWithFilterList)
        val result = contactRepository.getContactsWithFilters()
        assertEquals(contactWithFilterList, result)
    }

    @Test
    fun getContactsWithFilterByFilterTest() = runTest {
        val contactWithFilterList = listOf(ContactWithFilter().apply { Contact().apply { number = TEST_NUMBER }}, ContactWithFilter().apply {Contact()})
        Mockito.`when`(contactDao.getContactsWithFiltersByFilter(TEST_FILTER))
            .thenReturn(contactWithFilterList)
        val result = contactRepository.getContactsWithFilterByFilter(TEST_FILTER)
        assertEquals(contactWithFilterList, result)
    }

    @Test
    fun getSystemContactListTest() = runTest {
        val contact = Contact()
        val context = mock<Context>()
        val contentResolver = mock<ContentResolver>()
        val cursor = mock<Cursor>()
        val resultMock = mock<(Int, Int) -> Unit>()
        val expectedSize = 10

        Mockito.`when`(context.contentResolver).thenReturn(contentResolver)
        Mockito.`when`(
            contentResolver.query(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenReturn(cursor)

        Mockito.`when`(cursor.count).thenReturn(expectedSize)
        var index = 0
        Mockito.`when`(cursor.moveToNext()).thenAnswer {
            index++ < expectedSize
        }
        Mockito.`when`(cursor.getString(0)).thenReturn(contact.id)
        Mockito.`when`(cursor.getString(1)).thenReturn(contact.name)
        Mockito.`when`(cursor.getString(2)).thenReturn(contact.photoUrl)
        Mockito.`when`(cursor.getString(3)).thenReturn(contact.number)

        val contactList = contactRepository.getSystemContactList(context, resultMock)

        assertEquals(expectedSize, contactList.size)
        verify(resultMock, times(expectedSize)).invoke(eq(expectedSize), any())

        verify(context).contentResolver
        verify(cursor).close()
    }

    @Test
    fun getFilteredContactListTest() = runTest {
        val contactList = listOf(ContactWithFilter(contact = Contact(name = TEST_NAME), filterWithCountryCode = FilterWithCountryCode(filter = Filter(filterType = Constants.BLOCKER))), ContactWithFilter(contact = Contact(name = "zxy")))
        val result = contactRepository.getFilteredContactList(contactList, String.EMPTY, arrayListOf(
            NumberDataFiltering.CONTACT_WITH_BLOCKER.ordinal))
        assertEquals(contactList.filter { it.filterWithCountryCode?.filter?.isBlocker().isTrue() }, result)
    }

    @Test
    fun getHashMapFromContactListTest() = runTest {
        val contactWithFilterList = listOf(ContactWithFilter().apply { Contact().apply { name = TEST_NAME }}, ContactWithFilter().apply {Contact().apply { name = "abc" }})
        val result = contactRepository.getHashMapFromContactList(contactWithFilterList)
        assertEquals(contactWithFilterList.groupBy {
            if (it.contact?.name.isNullOrEmpty()) String.EMPTY else it.contact?.name?.get(0).toString()
        }, result)
    }

    @Test
    fun filteredNumberDataListTest() = runTest {
        val filter = Filter(filter = "123", conditionType = FilterCondition.FILTER_CONDITION_START.index)
        val numberDataList = arrayListOf(ContactWithFilter().apply { Contact().apply { number = TEST_NUMBER }}, LogCallWithFilter().apply { Contact().apply { number = TEST_NUMBER }}, NumberData().apply { Contact().apply { number = TEST_NUMBER }})
        val result = contactRepository.filteredNumberDataList(filter, numberDataList, 0)
        assertEquals(arrayListOf(numberDataList[1], numberDataList[2], numberDataList[0]), result)
    }
}