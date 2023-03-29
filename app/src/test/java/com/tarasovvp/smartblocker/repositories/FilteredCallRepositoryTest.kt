package com.tarasovvp.smartblocker.repositories

import com.tarasovvp.smartblocker.data.database.dao.FilteredCallDao
import com.tarasovvp.smartblocker.data.repositoryImpl.FilteredCallRepositoryImpl
import com.tarasovvp.smartblocker.domain.repository.FilteredCallRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class FilteredCallRepositoryTest {

    @Mock
    private lateinit var filteredCallDao: FilteredCallDao

    @Mock
    private lateinit var realDataBaseRepository: RealDataBaseRepository

    lateinit var filteredCallRepository: FilteredCallRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        filteredCallRepository = FilteredCallRepositoryImpl(filteredCallDao, realDataBaseRepository)
    }

    @Test
    suspend fun setFilterToFilteredCallTest() {

    }

    @Test
    suspend fun insertAllFilteredCallsTest() {

    }

    @Test
    suspend fun insertFilteredCallTest() {

    }

    @Test
    suspend fun allFilteredCallsTest() {

    }

    @Test
    suspend fun allFilteredCallWithFilterTest() {

    }

    @Test
    suspend fun filteredCallsByFilterTest() {

    }

    @Test
    suspend fun filteredCallsByNumberTest() {

    }

    @Test
    suspend fun deleteFilteredCallsTest() {

    }

}