package com.tarasovvp.smartblocker.repositories

import com.nhaarman.mockitokotlin2.*
import com.tarasovvp.smartblocker.TestUtils.TEST_FILTER
import com.tarasovvp.smartblocker.TestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.data.database.dao.FilterDao
import com.tarasovvp.smartblocker.data.repositoryImpl.FilterRepositoryImpl
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.domain.repository.FilterRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKER
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
    fun insertAllFiltersTest() = runTest {
        val filterList = listOf(Filter(filter = TEST_FILTER), Filter())
        filterRepository.insertAllFilters(filterList)
        verify(filterDao, times(1)).insertAllFilters(filterList)
    }

    @Test
    fun allFiltersTest() = runTest {
        val filterList = listOf(Filter(filter = TEST_FILTER), Filter())
        Mockito.`when`(filterDao.allFilters()).thenReturn(filterList)
        val result = filterRepository.allFilters()
        assertEquals(filterList, result)
    }

    @Test
    fun allFilterWithCountryCodeTest() = runTest {
        val filterWithCountryCodeList = listOf(FilterWithCountryCode().apply { filter = Filter(filter = TEST_FILTER)}, FilterWithCountryCode().apply { filter = Filter()})
        Mockito.`when`(filterDao.allFilterWithCountryCode()).thenReturn(filterWithCountryCodeList)
        val result = filterRepository.allFilterWithCountryCode()
        assertEquals(filterWithCountryCodeList, result)
    }

    @Test
    fun allFiltersByTypeTest() = runTest {
        val filterWithCountryCodeList = listOf(FilterWithCountryCode().apply { filter = Filter(filter = TEST_FILTER)}, FilterWithCountryCode().apply { filter = Filter()})
        Mockito.`when`(filterDao.allFiltersByType(BLOCKER)).thenReturn(filterWithCountryCodeList)
        val result = filterRepository.allFiltersByType(BLOCKER)
        assertEquals(filterWithCountryCodeList, result)
    }

    @Test
    fun getFilterTest() = runTest {
        val filterWithCountryCode = FilterWithCountryCode().apply { filter = Filter(filter = TEST_FILTER, conditionType = FilterCondition.FILTER_CONDITION_CONTAIN.index)}
        Mockito.`when`(filterDao.getFilter(TEST_FILTER)).thenReturn(filterWithCountryCode)
        val result = filterRepository.getFilter(filterWithCountryCode)
        assertEquals(filterWithCountryCode, result)
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
        val filterWithCountryCodeList = listOf(FilterWithCountryCode().apply { filter = Filter(filter = TEST_FILTER)}, FilterWithCountryCode().apply { filter = Filter()})
        Mockito.`when`(filterDao.queryFullMatchFilterList(TEST_NUMBER)).thenReturn(filterWithCountryCodeList)
        val result = filterRepository.queryFilterList(TEST_NUMBER)
        assertEquals(filterWithCountryCodeList, result)
    }

    @Test
    fun queryFilterTest() = runTest {
        val filterWithCountryCode = FilterWithCountryCode().apply { filter = Filter(filter = TEST_FILTER, conditionType = FilterCondition.FILTER_CONDITION_CONTAIN.index)}
        Mockito.`when`(filterDao.queryFullMatchFilterList(TEST_NUMBER)).thenReturn(listOf(filterWithCountryCode))
        val result = filterRepository.queryFilter(TEST_NUMBER)
        assertEquals(filterWithCountryCode, result)
    }

}