package com.tarasovvp.smartblocker.repositories

import com.tarasovvp.smartblocker.data.database.dao.LogCallDao
import com.tarasovvp.smartblocker.data.repositoryImpl.LogCallRepositoryImpl
import com.tarasovvp.smartblocker.domain.repository.LogCallRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    private lateinit var callDao: LogCallDao

    lateinit var logCallRepository: LogCallRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        logCallRepository = LogCallRepositoryImpl(callDao)
    }

    @Test
    suspend fun setFilterToLogCallTest() {

    }

    @Test
    suspend fun insertAllLogCallsTest() {

    }

    @Test
    suspend fun getAllLogCallWithFilterTest() {

    }

    @Test
    suspend fun allCallNumberWithFilterTest() {

    }

    @Test
    suspend fun getLogCallWithFilterByFilterTest() {

    }

    @Test
    suspend fun getSystemLogCallListTest() {

    }

    @Test
    suspend fun getHashMapFromCallListTest() {

    }
}