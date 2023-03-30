package com.tarasovvp.smartblocker.repositories

import com.nhaarman.mockitokotlin2.*
import com.tarasovvp.smartblocker.TestUtils.TEST_FILTER
import com.tarasovvp.smartblocker.data.database.dao.FilterDao
import com.tarasovvp.smartblocker.data.repositoryImpl.FilterRepositoryImpl
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.domain.repository.FilterRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
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
class FilterRepositoryTest {

    @Mock
    private lateinit var filterDao: FilterDao

    @Mock
    private lateinit var realDataBaseRepository: RealDataBaseRepository

    @Mock
    private lateinit var resultMock: () -> Unit

    private lateinit var filterRepository: FilterRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        filterRepository = FilterRepositoryImpl(filterDao, realDataBaseRepository)
    }

    @Test
    fun getHashMapFromFilterListTest() = runTest {

    }

    @Test
    fun insertAllFiltersTest() = runTest {
        val filterList = listOf(Filter(filter = TEST_FILTER), Filter())
        filterRepository.insertAllFilters(filterList)
        verify(filterDao, times(1)).insertAllFilters(filterList)
    }

    @Test
    fun allFiltersTest() = runTest {

    }

    @Test
    fun allFilterWithCountryCodeTest() = runTest {

    }

    @Test
    fun allFiltersByTypeTest() = runTest {

    }

    @Test
    fun getFilterTest() = runTest {

    }

    @Test
    fun updateFilterTest() = runTest {
        val filter = Filter(filter = TEST_FILTER)
        filterRepository.updateFilter(filter, resultMock)
        verify(filterDao).updateFilter(filter)
        verify(resultMock).invoke()
    }

    @Test
    fun insertFilterTest() = runTest {
        val filter = Filter(filter = TEST_FILTER)
        filterRepository.insertFilter(filter, resultMock)
        verify(filterDao).insertFilter(filter)
        verify(resultMock).invoke()
    }

    @Test
    fun deleteFilterListTest() = runTest {
        val filter = Filter(filter = TEST_FILTER)
        filterRepository.deleteFilterList(listOf(filter), resultMock)
        verify(filterDao).delete(filter)
        verify(resultMock).invoke()
    }

    @Test
    fun queryFilterListTest() = runTest {

    }

    @Test
    fun queryFilterTest() = runTest {

    }

}