package com.tarasovvp.smartblocker.repositories

import android.content.Context
import android.database.Cursor
import com.nhaarman.mockitokotlin2.*
import com.tarasovvp.smartblocker.TestUtils.TEST_FILTER
import com.tarasovvp.smartblocker.TestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.data.database.dao.LogCallDao
import com.tarasovvp.smartblocker.data.repositoryImpl.LogCallRepositoryImpl
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.domain.models.database_views.LogCallWithFilter
import com.tarasovvp.smartblocker.domain.models.entities.Contact
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.domain.models.entities.LogCall
import com.tarasovvp.smartblocker.domain.repository.LogCallRepository
import junit.framework.TestCase
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class LogCallRepositoryTest {

    @Mock
    private lateinit var logCallDao: LogCallDao

    private lateinit var logCallRepository: LogCallRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        logCallRepository = LogCallRepositoryImpl(logCallDao)
    }

    @Test
    fun setFilterToLogCallTest() = runTest {
        val filterList = listOf(
            Filter("1234567890", conditionType = FilterCondition.FILTER_CONDITION_FULL.index),
            Filter("345", conditionType = FilterCondition.FILTER_CONDITION_START.index),
            Filter("789", conditionType = FilterCondition.FILTER_CONDITION_CONTAIN.index)
        )
        val logCallList = listOf(
            LogCall().apply { number = "1234567890" },
            LogCall().apply { number = "3456789012" },
            LogCall().apply { number = "5678901234" }
        )

        val resultMock = mock<(Int, Int) -> Unit>()
        val modifiedContacts = logCallRepository.setFilterToLogCall(filterList, logCallList, resultMock)

        assertEquals("1234567890", modifiedContacts[0].filter)
        assertEquals("345", modifiedContacts[1].filter)
        assertEquals("789", modifiedContacts[2].filter)
        verify(resultMock, times(logCallList.size)).invoke(eq(logCallList.size), any())
    }

    @Test
    fun insertAllLogCallsTest() = runTest {
        val logCallList = listOf(LogCall().apply { number = TEST_NUMBER }, LogCall())
        logCallRepository.insertAllLogCalls(logCallList)
        verify(logCallDao, times(1)).insertAllLogCalls(logCallList)
    }

    @Test
    fun getAllLogCallWithFilterTest() = runTest {
        val logCallList = listOf(LogCallWithFilter().apply { call = LogCall(callId = 1) }, LogCallWithFilter().apply { call = LogCall(callId = 2) })
        Mockito.`when`(logCallDao.allLogCallWithFilter())
            .thenReturn(logCallList)
        val result = logCallRepository.getAllLogCallWithFilter()
        assertEquals(logCallList, result)
    }

    @Test
    fun allCallNumberWithFilterTest() = runTest {
        val logCallList = listOf(LogCallWithFilter().apply { call = LogCall(callId = 1).apply { number = "1" } }, LogCallWithFilter().apply { call = LogCall(callId = 2).apply { number = "1" } }, LogCallWithFilter().apply { call = LogCall(callId = 3).apply { number = "2" }})
        Mockito.`when`(logCallDao.allCallNumberWithFilter())
            .thenReturn(logCallList)
        val result = logCallRepository.allCallNumberWithFilter()
        assertEquals(logCallList.distinctBy { it.call?.number }, result)
    }

    @Test
    fun getLogCallWithFilterByFilterTest() = runTest {
        val logCallList = listOf(LogCallWithFilter().apply { call = LogCall(callId = 1).apply { number = "1" } }, LogCallWithFilter().apply { call = LogCall(callId = 2).apply { number = "1" } }, LogCallWithFilter().apply { call = LogCall(callId = 3).apply { number = "2" }})
        Mockito.`when`(logCallDao.queryCallList(TEST_FILTER))
            .thenReturn(logCallList)
        val result = logCallRepository.getLogCallWithFilterByFilter(TEST_FILTER)
        assertEquals(logCallList.distinctBy { it.call?.number }, result)
    }

    @Test
    fun getSystemLogCallListTest() = runTest {
        val contact = Contact()
        val context = mock<Context>()
        val cursor = mock<Cursor>()
        val resultMock = mock<(Int, Int) -> Unit>()
        val expectedSize = 10

        `when`(context.contentResolver).thenReturn(mock())
        `when`(context.contentResolver.query(
            any(),
            any(),
            any(),
            any(),
            anyOrNull()
        )).thenReturn(cursor)

        `when`(cursor.count).thenReturn(expectedSize)
        `when`(cursor.moveToNext()).thenReturn(true, false)
        `when`(cursor.getString(0)).thenReturn(contact.id)
        `when`(cursor.getString(1)).thenReturn(contact.name)
        `when`(cursor.getString(2)).thenReturn(contact.photoUrl)
        `when`(cursor.getString(3)).thenReturn(contact.number)

        val contactList = logCallRepository.getSystemLogCallList(context, resultMock)

        assertEquals(expectedSize, contactList.size)
        verify(resultMock, times(expectedSize)).invoke(expectedSize, any())

        verify(context).contentResolver
        verify(cursor, times(expectedSize)).moveToNext()
        verify(cursor).close()
    }

    @Test
    fun getHashMapFromCallListTest() = runTest {
        val logCallList = listOf(LogCallWithFilter().apply { call = LogCall(callId = 1).apply { number = "1" } }, LogCallWithFilter().apply { call = LogCall(callId = 2).apply { number = "1" } }, LogCallWithFilter().apply { call = LogCall(callId = 3).apply { number = "2" }})
        val result = logCallRepository.getHashMapFromCallList(logCallList)
        assertEquals(logCallList.groupBy { it.call?.dateFromCallDate() }, result)
    }
}