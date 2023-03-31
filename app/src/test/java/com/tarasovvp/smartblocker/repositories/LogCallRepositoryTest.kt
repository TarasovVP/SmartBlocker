package com.tarasovvp.smartblocker.repositories

import com.nhaarman.mockitokotlin2.*
import com.tarasovvp.smartblocker.TestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.data.database.dao.LogCallDao
import com.tarasovvp.smartblocker.data.repositoryImpl.LogCallRepositoryImpl
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.domain.models.entities.LogCall
import com.tarasovvp.smartblocker.domain.repository.LogCallRepository
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
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

        TestCase.assertEquals("1234567890", modifiedContacts[0].filter)
        TestCase.assertEquals("345", modifiedContacts[1].filter)
        TestCase.assertEquals("789", modifiedContacts[2].filter)
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

    }

    @Test
    fun allCallNumberWithFilterTest() = runTest {

    }

    @Test
    fun getLogCallWithFilterByFilterTest() = runTest {

    }

    @Test
    fun getSystemLogCallListTest() = runTest {

    }

    @Test
    fun getHashMapFromCallListTest() = runTest {

    }
}