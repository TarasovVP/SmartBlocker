package com.tarasovvp.smartblocker.repositories

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_FILTER
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.data.database.dao.FilterDao
import com.tarasovvp.smartblocker.data.repositoryImpl.FilterRepositoryImpl
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.domain.repository.FilterRepository
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKER
import io.mockk.*
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

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
    fun insertAllFiltersTest() = runBlocking {
        val filterList = listOf(Filter(filter = TEST_FILTER), Filter())
        coEvery { filterDao.insertAllFilters(filterList) } just Runs
        filterRepository.insertAllFilters(filterList)
        coVerify(exactly = 1) { filterDao.insertAllFilters(filterList) }
    }

    @Test
    fun allFiltersTest() = runBlocking {
        val filterList = listOf(Filter(filter = TEST_FILTER), Filter())
        coEvery { filterDao.allFilters() } returns filterList
        val result = filterRepository.allFilters()
        assertEquals(filterList, result)
    }

    @Test
    fun allFiltersByTypeTest() = runBlocking {
        val filterWithCountryCodeList = listOf(FilterWithCountryCode().apply { filter = Filter(filter = TEST_FILTER)}, FilterWithCountryCode().apply { filter = Filter()})
        coEvery { filterDao.allFiltersByType(BLOCKER) } returns filterWithCountryCodeList
        val result = filterRepository.allFiltersByType(BLOCKER)
        assertEquals(filterWithCountryCodeList, result)
    }

    @Test
    fun getFilterTest() = runBlocking {
        val filterWithCountryCode = FilterWithCountryCode().apply { filter = Filter(filter = TEST_FILTER, conditionType = FilterCondition.FILTER_CONDITION_CONTAIN.ordinal)}
        coEvery { filterDao.getFilter(TEST_FILTER) } returns filterWithCountryCode
        val result = filterRepository.getFilter(filterWithCountryCode)
        assertEquals(filterWithCountryCode, result)
    }

    @Test
    fun updateFilterTest() = runBlocking {
        val filter = Filter(filter = TEST_FILTER)
        coEvery { filterDao.updateFilter(filter) } just Runs
        filterRepository.updateFilter(filter)
        coVerify(exactly = 1) { filterDao.updateFilter(filter) }
    }

    @Test
    fun insertFilterTest() = runBlocking {
        val filter = Filter(filter = TEST_FILTER)
        coEvery { filterDao.insertFilter(filter) } just Runs
        filterRepository.insertFilter(filter)
        coVerify(exactly = 1) { filterDao.insertFilter(filter) }
    }

    @Test
    fun deleteFilterListTest() = runBlocking {
        val filter = Filter(filter = TEST_FILTER)
        coEvery { filterDao.deleteFilter(filter) } just Runs
        filterRepository.deleteFilterList(listOf(filter))
        coVerify(exactly = 1) { filterDao.deleteFilter(filter) }
    }

    @Test
    fun queryFilterListTest() = runBlocking {
        val filterWithCountryCodeList = listOf(FilterWithCountryCode().apply { filter = Filter(filter = TEST_FILTER)}, FilterWithCountryCode().apply { filter = Filter()})
        coEvery { filterDao.queryFullMatchFilterList(TEST_NUMBER) } returns filterWithCountryCodeList
        val result = filterRepository.queryFilterList(TEST_NUMBER)
        assertEquals(filterWithCountryCodeList, result)
    }

    @Test
    fun queryFilterTest() = runBlocking {
        val filterWithCountryCode = FilterWithCountryCode().apply { filter = Filter(filter = TEST_FILTER, conditionType = FilterCondition.FILTER_CONDITION_CONTAIN.ordinal)}
        coEvery { filterDao.queryFullMatchFilterList(TEST_NUMBER) } returns listOf(filterWithCountryCode)
        val result = filterRepository.queryFilter(TEST_NUMBER)
        assertEquals(filterWithCountryCode, result)
    }

}