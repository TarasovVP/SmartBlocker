package com.tarasovvp.smartblocker.repositories

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.os.Build
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_FILTER
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.data.database.dao.LogCallDao
import com.tarasovvp.smartblocker.data.repositoryImpl.LogCallRepositoryImpl
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.domain.enums.NumberDataFiltering
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.entities.db_views.LogCallWithFilter
import com.tarasovvp.smartblocker.domain.entities.models.CallWithFilter
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.entities.db_entities.FilteredCall
import com.tarasovvp.smartblocker.domain.entities.db_entities.LogCall
import com.tarasovvp.smartblocker.domain.repository.LogCallRepository
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKED_CALL
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
class LogCallRepositoryTest {

    @MockK
    private lateinit var logCallDao: LogCallDao

    private lateinit var logCallRepository: LogCallRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        logCallRepository = LogCallRepositoryImpl(logCallDao)
    }

    @Test
    fun setFilterToLogCallTest() = runBlocking {
        val filterList = listOf(
            Filter("1234567890", conditionType = FilterCondition.FILTER_CONDITION_FULL.ordinal),
            Filter("345", conditionType = FilterCondition.FILTER_CONDITION_START.ordinal),
            Filter("789", conditionType = FilterCondition.FILTER_CONDITION_CONTAIN.ordinal)
        )
        val logCallList = listOf(
            LogCall().apply { number = "1234567890" },
            LogCall().apply { number = "3456789012" },
            LogCall().apply { number = "5678901234" }
        )

        val resultMock = mockk<(Int, Int) -> Unit>(relaxed = true)
        val modifiedContacts = logCallRepository.setFilterToLogCall(filterList, logCallList, resultMock)

        assertEquals("1234567890", modifiedContacts[0].filter)
        assertEquals("345", modifiedContacts[1].filter)
        assertEquals("789", modifiedContacts[2].filter)
        verify(exactly = logCallList.size) { resultMock.invoke(any(), any()) }
    }

    @Test
    fun insertAllLogCallsTest() = runBlocking {
        val logCallList = listOf(LogCall().apply { number = TEST_NUMBER }, LogCall())
        coEvery { logCallDao.insertAllLogCalls(logCallList) } just Runs
        logCallRepository.insertAllLogCalls(logCallList)
        coVerify(exactly = 1) { logCallDao.insertAllLogCalls(logCallList) }
    }

    @Test
    fun getAllLogCallWithFilterTest() = runBlocking {
        val logCallList = listOf(LogCallWithFilter().apply { call = LogCall(callId = 1) }, LogCallWithFilter().apply { call = LogCall(callId = 2) })
        coEvery { logCallDao.allLogCallWithFilter() } returns logCallList
        val result = logCallRepository.getAllLogCallWithFilter()
        assertEquals(logCallList, result)
    }

    @Test
    fun allCallNumberWithFilterTest() = runBlocking {
        val logCallList = listOf(LogCallWithFilter().apply { call = LogCall(callId = 1).apply { number = "1" } }, LogCallWithFilter().apply { call = LogCall(callId = 2).apply { number = "1" } }, LogCallWithFilter().apply { call = LogCall(callId = 3).apply { number = "2" }})
        coEvery { logCallDao.allCallNumberWithFilter() } returns logCallList
        val result = logCallRepository.allCallNumberWithFilter()
        assertEquals(logCallList.distinctBy { it.call?.number }, result)
    }

    @Test
    fun getLogCallWithFilterByFilterTest() = runBlocking {
        val logCallList = listOf(LogCallWithFilter().apply { call = LogCall(callId = 1).apply { number = "1" } }, LogCallWithFilter().apply { call = LogCall(callId = 2).apply { number = "1" } }, LogCallWithFilter().apply { call = LogCall(callId = 3).apply { number = "2" }})
        coEvery { logCallDao.queryCallList(TEST_FILTER) } returns logCallList
        val result = logCallRepository.getLogCallWithFilterByFilter(TEST_FILTER)
        assertEquals(logCallList.distinctBy { it.call?.number }, result)
    }

    @Test
    fun getSystemLogCallListTest() = runBlocking {
        val logCall = LogCall()
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
        every { cursor.getInt(0) } returns logCall.callId
        every { cursor.getString(1) } returns logCall.callName
        every { cursor.getString(2) } returns logCall.number
        every { cursor.getString(3) } returns logCall.type
        every { cursor.getString(4) } returns logCall.callDate
        every { cursor.getString(5) } returns logCall.normalizedNumber
        every { cursor.getString(6) } returns logCall.countryIso
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            every { cursor.getString(7) } returns logCall.photoUrl
        }
        every { cursor.close() } just Runs

        val contactList = logCallRepository.getSystemLogCallList(context, resultMock)

        assertEquals(expectedSize, contactList.size)
        verify(exactly = expectedSize) { resultMock.invoke(any(), any()) }
        verify(exactly = 1) { context.contentResolver }
        verify(exactly = 1) { cursor.close() }
    }

    @Test
    fun getFilteredCallListTest() = runBlocking {
        val callList = listOf(CallWithFilter(call = FilteredCall().apply { type = BLOCKED_CALL }, filterWithCountryCode = FilterWithCountryCode(filter = Filter(filterType = Constants.BLOCKER))), CallWithFilter(call = LogCall().apply { number = "567" }))
        val result = logCallRepository.getFilteredCallList(callList, String.EMPTY, arrayListOf(NumberDataFiltering.CALL_BLOCKED.ordinal))
        assertEquals(callList.filter { it.filterWithCountryCode?.filter?.isBlocker().isTrue() }, result)
    }

    @Test
    fun getHashMapFromCallListTest() = runBlocking {
        val logCallList = listOf(LogCallWithFilter().apply { call = LogCall(callId = 1).apply { number = "1" } }, LogCallWithFilter().apply { call = LogCall(callId = 2).apply { number = "1" } }, LogCallWithFilter().apply { call = LogCall(callId = 3).apply { number = "2" }})
        val result = logCallRepository.getHashMapFromCallList(logCallList)
        assertEquals(logCallList.groupBy { it.call?.dateFromCallDate() }, result)
    }
}