package com.tarasovvp.smartblocker.repositories

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_FILTER
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.data.database.dao.FilteredCallDao
import com.tarasovvp.smartblocker.data.repositoryImpl.FilteredCallRepositoryImpl
import com.tarasovvp.smartblocker.domain.entities.db_entities.FilteredCall
import com.tarasovvp.smartblocker.domain.entities.db_views.CallWithFilter
import com.tarasovvp.smartblocker.domain.repository.FilteredCallRepository
import io.mockk.*
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class FilteredCallRepositoryTest {

    @MockK
    private lateinit var filteredCallDao: FilteredCallDao

    private lateinit var filteredCallRepository: FilteredCallRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        filteredCallRepository = FilteredCallRepositoryImpl(filteredCallDao)
    }

    @Test
    fun insertAllFilteredCallsTest() = runBlocking {
        val filteredCallList = listOf(FilteredCall().apply { number = TEST_NUMBER }, FilteredCall())
        coEvery { filteredCallDao.insertAllFilteredCalls(filteredCallList) } just Runs
        filteredCallRepository.insertAllFilteredCalls(filteredCallList)
        coVerify(exactly = 1) { filteredCallRepository.insertAllFilteredCalls(filteredCallList) }
    }

    @Test
    fun insertFilteredCallTest() = runBlocking {
        val filteredCall = FilteredCall().apply { number = TEST_NUMBER }
        coEvery { filteredCallDao.insertFilteredCall(filteredCall) } just Runs
        filteredCallRepository.insertFilteredCall(filteredCall)
        coVerify(exactly = 1) { filteredCallRepository.insertFilteredCall(filteredCall) }
    }

    @Test
    fun allFilteredCallsTest() = runBlocking {
        val filteredCallList = listOf(FilteredCall(callId = 1), FilteredCall(callId = 3))
        coEvery { filteredCallDao.allFilteredCalls() } returns filteredCallList
        val result = filteredCallRepository.allFilteredCalls()
        assertEquals(filteredCallList, result)
    }

    @Test
    fun filteredCallsByFilterTest() = runBlocking {
        val filteredCallList = listOf(CallWithFilter().apply { call=  FilteredCall(callId = 1)}, CallWithFilter().apply { call=  FilteredCall(callId = 3)})
        coEvery { filteredCallDao.allFilteredCallsByFilter(TEST_FILTER) } returns filteredCallList
        val result = filteredCallRepository.allFilteredCallsByFilter(TEST_FILTER)
        assertEquals(filteredCallList, result)
    }

    @Test
    fun filteredCallsByNumberTest() = runBlocking {
        val filteredCallList = listOf(CallWithFilter().apply { call=  FilteredCall(callId = 1)}, CallWithFilter().apply { call=  FilteredCall(callId = 3)})
        coEvery { filteredCallDao.allFilteredCallsByNumber(TEST_NUMBER) } returns filteredCallList
        val result = filteredCallRepository.allFilteredCallsByNumber(TEST_NUMBER)
        assertEquals(filteredCallList, result)
    }

    @Test
    fun deleteFilteredCallsTest() = runBlocking {
        val filteredCallIdList = listOf(1, 2)
        coEvery { filteredCallDao.deleteFilteredCalls(filteredCallIdList) } just Runs
        filteredCallRepository.deleteFilteredCalls(filteredCallIdList)
        coVerify(exactly = 1) { filteredCallRepository.deleteFilteredCalls(filteredCallIdList) }
    }

}