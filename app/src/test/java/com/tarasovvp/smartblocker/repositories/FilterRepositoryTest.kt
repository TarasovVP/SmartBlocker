package com.tarasovvp.smartblocker.repositories

import com.nhaarman.mockitokotlin2.*
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_FILTER
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.data.database.dao.FilterDao
import com.tarasovvp.smartblocker.data.repositoryImpl.FilterRepositoryImpl
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.domain.repository.FilterRepository
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKER
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
class FilterRepositoryTest {

    @MockK
    private lateinit var filterDao: FilterDao

    private lateinit var filterRepository: FilterRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        filterRepository = FilterRepositoryImpl(filterDao)
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
        filterRepository.updateFilter(filter)
        verify(filterDao).updateFilter(filter)
    }

    @Test
    fun insertFilterTest() = runTest {
        val filter = Filter(filter = TEST_FILTER)
        filterRepository.insertFilter(filter)
        verify(filterDao).insertFilter(filter)
    }

    @Test
    fun deleteFilterListTest() = runTest {
        val filter = Filter(filter = TEST_FILTER)
        filterRepository.deleteFilterList(listOf(filter))
        verify(filterDao).delete(filter)
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