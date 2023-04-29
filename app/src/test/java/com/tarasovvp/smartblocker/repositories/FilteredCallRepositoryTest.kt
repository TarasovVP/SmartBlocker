package com.tarasovvp.smartblocker.repositories

import com.nhaarman.mockitokotlin2.*
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_FILTER
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.data.database.dao.FilteredCallDao
import com.tarasovvp.smartblocker.data.repositoryImpl.FilteredCallRepositoryImpl
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.domain.models.database_views.FilteredCallWithFilter
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.domain.models.entities.FilteredCall
import com.tarasovvp.smartblocker.domain.repository.FilteredCallRepository
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
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
    fun setFilterToFilteredCallTest() = runTest {
        val filterList = listOf(
            Filter("1234567890", conditionType = FilterCondition.FILTER_CONDITION_FULL.index),
            Filter("345", conditionType = FilterCondition.FILTER_CONDITION_START.index),
            Filter("789", conditionType = FilterCondition.FILTER_CONDITION_CONTAIN.index)
        )
        val filteredCallList = listOf(
            FilteredCall().apply { number = "1234567890" },
            FilteredCall().apply { number = "3456789012" },
            FilteredCall().apply { number = "5678901234" }
        )

        val resultMock = mock<(Int, Int) -> Unit>()
        val modifiedContacts = filteredCallRepository.setFilterToFilteredCall(filterList, filteredCallList, resultMock)

        assertEquals("1234567890", modifiedContacts[0].filter)
        assertEquals("345", modifiedContacts[1].filter)
        assertEquals("789", modifiedContacts[2].filter)
        verify(resultMock, times(filteredCallList.size)).invoke(eq(filteredCallList.size), any())
    }

    @Test
    fun insertAllFilteredCallsTest() = runTest {
        val filteredCallList = listOf(FilteredCall().apply { number = TEST_NUMBER }, FilteredCall())
        filteredCallRepository.insertAllFilteredCalls(filteredCallList)
        verify(filteredCallDao, times(1)).insertAllFilteredCalls(filteredCallList)
    }

    @Test
    fun insertFilteredCallTest() = runTest {
        val filteredCall = FilteredCall().apply { number = TEST_NUMBER }
        filteredCallRepository.insertFilteredCall(filteredCall)
        verify(filteredCallDao, times(1)).insertFilteredCall(filteredCall)
    }

    @Test
    fun allFilteredCallsTest() = runTest {
        val filteredCallList = listOf(FilteredCall(callId = 1), FilteredCall(callId = 3))
        Mockito.`when`(filteredCallDao.allFilteredCalls())
            .thenReturn(filteredCallList)
        val result = filteredCallRepository.allFilteredCalls()
        assertEquals(filteredCallList, result)
    }

    @Test
    fun allFilteredCallWithFilterTest() = runTest {
        val filteredCallList = listOf(FilteredCallWithFilter().apply { call=  FilteredCall(callId = 1)}, FilteredCallWithFilter().apply { call=  FilteredCall(callId = 3)})
        Mockito.`when`(filteredCallDao.allFilteredCallWithFilter())
            .thenReturn(filteredCallList)
        val result = filteredCallRepository.allFilteredCallWithFilter()
        assertEquals(filteredCallList, result)
    }

    @Test
    fun filteredCallsByFilterTest() = runTest {
        val filteredCallList = listOf(FilteredCallWithFilter().apply { call=  FilteredCall(callId = 1)}, FilteredCallWithFilter().apply { call=  FilteredCall(callId = 3)})
        Mockito.`when`(filteredCallDao.filteredCallsByFilter(TEST_FILTER))
            .thenReturn(filteredCallList)
        val result = filteredCallRepository.filteredCallsByFilter(TEST_FILTER)
        assertEquals(filteredCallList, result)
    }

    @Test
    fun filteredCallsByNumberTest() = runTest {
        val filteredCallList = listOf(FilteredCallWithFilter().apply { call=  FilteredCall(callId = 1)}, FilteredCallWithFilter().apply { call=  FilteredCall(callId = 3)})
        Mockito.`when`(filteredCallDao.filteredCallsByNumber(TEST_NUMBER))
            .thenReturn(filteredCallList)
        val result = filteredCallRepository.filteredCallsByNumber(TEST_NUMBER)
        assertEquals(filteredCallList, result)
    }

    @Test
    fun deleteFilteredCallsTest() = runTest {
        val filteredCallIdList = listOf(1, 2)
        filteredCallRepository.deleteFilteredCalls(filteredCallIdList)
        verify(filteredCallDao).deleteFilteredCalls(filteredCallIdList)
    }

}