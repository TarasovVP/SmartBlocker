package com.tarasovvp.smartblocker.repositories

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.os.Build
import com.nhaarman.mockitokotlin2.*
import com.tarasovvp.smartblocker.TestUtils.TEST_FILTER
import com.tarasovvp.smartblocker.TestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.data.database.dao.LogCallDao
import com.tarasovvp.smartblocker.data.repositoryImpl.LogCallRepositoryImpl
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.domain.models.database_views.LogCallWithFilter
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.domain.models.entities.LogCall
import com.tarasovvp.smartblocker.domain.repository.LogCallRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
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
        `when`(logCallDao.allLogCallWithFilter())
            .thenReturn(logCallList)
        val result = logCallRepository.getAllLogCallWithFilter()
        assertEquals(logCallList, result)
    }

    @Test
    fun allCallNumberWithFilterTest() = runTest {
        val logCallList = listOf(LogCallWithFilter().apply { call = LogCall(callId = 1).apply { number = "1" } }, LogCallWithFilter().apply { call = LogCall(callId = 2).apply { number = "1" } }, LogCallWithFilter().apply { call = LogCall(callId = 3).apply { number = "2" }})
        `when`(logCallDao.allCallNumberWithFilter())
            .thenReturn(logCallList)
        val result = logCallRepository.allCallNumberWithFilter()
        assertEquals(logCallList.distinctBy { it.call?.number }, result)
    }

    @Test
    fun getLogCallWithFilterByFilterTest() = runTest {
        val logCallList = listOf(LogCallWithFilter().apply { call = LogCall(callId = 1).apply { number = "1" } }, LogCallWithFilter().apply { call = LogCall(callId = 2).apply { number = "1" } }, LogCallWithFilter().apply { call = LogCall(callId = 3).apply { number = "2" }})
        `when`(logCallDao.queryCallList(TEST_FILTER))
            .thenReturn(logCallList)
        val result = logCallRepository.getLogCallWithFilterByFilter(TEST_FILTER)
        assertEquals(logCallList.distinctBy { it.call?.number }, result)
    }

    @Test
    fun getSystemLogCallListTest() = runTest {
        val logCall = LogCall()
        val context = mock<Context>()
        val contentResolver = mock<ContentResolver>()
        val cursor = mock<Cursor>()
        val resultMock = mock<(Int, Int) -> Unit>()
        val expectedSize = 10

        `when`(context.contentResolver).thenReturn(contentResolver)
        `when`(
            contentResolver.query(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenReturn(cursor)

        `when`(cursor.count).thenReturn(expectedSize)
        var index = 0
        `when`(cursor.moveToNext()).thenAnswer {
            index++ < expectedSize
        }
        `when`(cursor.getInt(0)).thenReturn(logCall.callId)
        `when`(cursor.getString(1)).thenReturn(logCall.callName)
        `when`(cursor.getString(2)).thenReturn(logCall.number)
        `when`(cursor.getString(3)).thenReturn(logCall.type)
        `when`(cursor.getString(4)).thenReturn(logCall.callDate)
        `when`(cursor.getString(5)).thenReturn(logCall.normalizedNumber)
        `when`(cursor.getString(6)).thenReturn(logCall.countryIso)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            `when`(cursor.getString(7)).thenReturn(logCall.photoUrl)
        }

        val contactList = logCallRepository.getSystemLogCallList(context, resultMock)

        assertEquals(expectedSize, contactList.size)
        verify(resultMock, times(expectedSize)).invoke(eq(expectedSize), any())

        verify(context).contentResolver
        verify(cursor).close()
    }

    @Test
    fun getHashMapFromCallListTest() = runTest {
        val logCallList = listOf(LogCallWithFilter().apply { call = LogCall(callId = 1).apply { number = "1" } }, LogCallWithFilter().apply { call = LogCall(callId = 2).apply { number = "1" } }, LogCallWithFilter().apply { call = LogCall(callId = 3).apply { number = "2" }})
        val result = logCallRepository.getHashMapFromCallList(logCallList)
        assertEquals(logCallList.groupBy { it.call?.dateFromCallDate() }, result)
    }
}