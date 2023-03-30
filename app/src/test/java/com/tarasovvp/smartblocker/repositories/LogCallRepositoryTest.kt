package com.tarasovvp.smartblocker.repositories

import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.tarasovvp.smartblocker.TestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.data.database.dao.LogCallDao
import com.tarasovvp.smartblocker.data.repositoryImpl.LogCallRepositoryImpl
import com.tarasovvp.smartblocker.domain.models.entities.LogCall
import com.tarasovvp.smartblocker.domain.repository.LogCallRepository
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