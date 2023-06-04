package com.tarasovvp.smartblocker.repositories

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.os.Build
import com.tarasovvp.smartblocker.UnitTestUtils
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_FILTER
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.data.database.dao.LogCallDao
import com.tarasovvp.smartblocker.data.repositoryImpl.LogCallRepositoryImpl
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.entities.db_views.CallWithFilter
import com.tarasovvp.smartblocker.domain.entities.db_entities.LogCall
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithFilteredNumber
import com.tarasovvp.smartblocker.domain.repository.LogCallRepository
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
class LogCallRepositoryUnitTest {

    @MockK
    private lateinit var appPhoneNumberUtil: AppPhoneNumberUtil

    @MockK
    private lateinit var logCallDao: LogCallDao

    private lateinit var logCallRepository: LogCallRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        logCallRepository = LogCallRepositoryImpl(appPhoneNumberUtil, logCallDao)
    }

    @Test
    fun getSystemLogCallListTest() = runBlocking {
        val logCall = LogCall()
        val context = mockk<Context>()
        val contentResolver = mockk<ContentResolver>()
        val cursor = mockk<Cursor>()
        val country = UnitTestUtils.TEST_COUNTRY
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            every { cursor.getString(7) } returns logCall.photoUrl
        }
        every { cursor.close() } just Runs
        every { appPhoneNumberUtil.phoneNumberValue(logCall.number, country) } returns logCall.phoneNumberValue.orEmpty()
        every { appPhoneNumberUtil.isPhoneNumberValid(logCall.number, country) } returns logCall.isPhoneNumberValid.isTrue()

        val logCallList = logCallRepository.getSystemLogCallList(context, country, resultMock)

        assertEquals(expectedSize, logCallList.size)
        verify(exactly = expectedSize) { resultMock.invoke(any(), any()) }
        verify(exactly = 1) { context.contentResolver }
        verify(exactly = 1) { cursor.close() }
    }

    @Test
    fun insertAllLogCallsTest() = runBlocking {
        val logCallList = listOf(LogCall().apply { number = TEST_NUMBER }, LogCall())
        coEvery { logCallDao.insertAllLogCalls(logCallList) } just Runs
        logCallRepository.insertAllLogCalls(logCallList)
        coVerify(exactly = 1) { logCallDao.insertAllLogCalls(logCallList) }
    }

    @Test
    fun allCallWithFiltersTest() = runBlocking {
        val logCallList = listOf(CallWithFilter().apply { call = LogCall(callId = 1) }, CallWithFilter().apply { call = LogCall(callId = 2) })
        coEvery { logCallDao.allCallWithFilters() } returns logCallList
        val result = logCallRepository.allCallWithFilters()
        assertEquals(logCallList, result)
    }

    @Test
    fun allCallWithFiltersByFilterTest() = runBlocking {
        val logCallList = listOf(CallWithFilter().apply { call = LogCall(callId = 1).apply { number = "1" }
            filterWithFilteredNumber =  FilterWithFilteredNumber(filter = Filter(filter = TEST_FILTER)) },
            CallWithFilter().apply { call = LogCall(callId = 2).apply { number = "1" } },
            CallWithFilter().apply { call = LogCall(callId = 3).apply { number = "2" }})
        val expectedResult = logCallList.filter { it.filterWithFilteredNumber?.filter?.filter ==  TEST_FILTER}
        coEvery { logCallDao.allCallWithFiltersByFilter(TEST_FILTER) } returns expectedResult
        val result = logCallRepository.allCallWithFiltersByFilter(TEST_FILTER)
        assertEquals(expectedResult, result)
    }
}