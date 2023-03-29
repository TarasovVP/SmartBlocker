package com.tarasovvp.smartblocker.repositories

import com.tarasovvp.smartblocker.data.database.dao.FilterDao
import com.tarasovvp.smartblocker.data.repositoryImpl.FilterRepositoryImpl
import com.tarasovvp.smartblocker.domain.repository.FilterRepository
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
class FilterRepositoryTest {

    @Mock
    private lateinit var filterDao: FilterDao

    @Mock
    private lateinit var realDataBaseRepository: RealDataBaseRepository

    lateinit var filterRepository: FilterRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        filterRepository = FilterRepositoryImpl(filterDao, realDataBaseRepository)
    }

    @Test
    suspend fun getHashMapFromFilterListTest() {

    }

    @Test
    suspend fun insertAllFiltersTest() {

    }

    @Test
    suspend fun allFiltersTest() {

    }

    @Test
    suspend fun allFilterWithCountryCodeTest() {

    }

    @Test
    suspend fun allFiltersByTypeTest() {

    }

    @Test
    suspend fun getFilterTest() {

    }

    @Test
    suspend fun updateFilterTest() {

    }

    @Test
    suspend fun insertFilterTest() {

    }

    @Test
    suspend fun deleteFilterListTest() {

    }

    @Test
    suspend fun queryFilterListTest() {

    }

    @Test
    suspend fun queryFilterTest() {

    }

}