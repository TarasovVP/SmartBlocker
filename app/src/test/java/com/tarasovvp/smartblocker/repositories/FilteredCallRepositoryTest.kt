package com.tarasovvp.smartblocker.repositories

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_FILTER
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.data.database.dao.FilteredCallDao
import com.tarasovvp.smartblocker.data.repositoryImpl.FilteredCallRepositoryImpl
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.domain.entities.db_views.FilteredCallWithFilter
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.entities.db_entities.FilteredCall
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
    fun setFilterToFilteredCallTest() = runBlocking {
        val filterList = listOf(
            Filter("1234567890", conditionType = FilterCondition.FILTER_CONDITION_FULL.ordinal),
            Filter("345", conditionType = FilterCondition.FILTER_CONDITION_START.ordinal),
            Filter("789", conditionType = FilterCondition.FILTER_CONDITION_CONTAIN.ordinal)
        )
        val filteredCallList = listOf(
            FilteredCall().apply { number = "1234567890" },
            FilteredCall().apply { number = "3456789012" },
            FilteredCall().apply { number = "5678901234" }
        )

        val resultMock = mockk<(Int, Int) -> Unit>(relaxed = true)
        val modifiedContacts = filteredCallRepository.setFilterToFilteredCall(filterList, filteredCallList, resultMock)

        assertEquals("1234567890", modifiedContacts[0].filter)
        assertEquals("345", modifiedContacts[1].filter)
        assertEquals("789", modifiedContacts[2].filter)
        verify(exactly = filteredCallList.size) { resultMock.invoke(filteredCallList.size, any()) }
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
    fun allFilteredCallWithFilterTest() = runBlocking {
        val filteredCallList = listOf(FilteredCallWithFilter().apply { call=  FilteredCall(callId = 1)}, FilteredCallWithFilter().apply { call=  FilteredCall(callId = 3)})
        coEvery { filteredCallDao.allFilteredCallWithFilter() } returns filteredCallList
        val result = filteredCallRepository.allFilteredCallWithFilter()
        assertEquals(filteredCallList, result)
    }

    @Test
    fun filteredCallsByFilterTest() = runBlocking {
        val filteredCallList = listOf(FilteredCallWithFilter().apply { call=  FilteredCall(callId = 1)}, FilteredCallWithFilter().apply { call=  FilteredCall(callId = 3)})
        coEvery { filteredCallDao.filteredCallsByFilter(TEST_FILTER) } returns filteredCallList
        val result = filteredCallRepository.filteredCallsByFilter(TEST_FILTER)
        assertEquals(filteredCallList, result)
    }

    @Test
    fun filteredCallsByNumberTest() = runBlocking {
        val filteredCallList = listOf(FilteredCallWithFilter().apply { call=  FilteredCall(callId = 1)}, FilteredCallWithFilter().apply { call=  FilteredCall(callId = 3)})
        coEvery { filteredCallDao.filteredCallsByNumber(TEST_NUMBER) } returns filteredCallList
        val result = filteredCallRepository.filteredCallsByNumber(TEST_NUMBER)
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