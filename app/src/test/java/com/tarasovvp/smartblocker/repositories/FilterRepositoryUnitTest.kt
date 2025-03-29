package com.tarasovvp.smartblocker.repositories

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_FILTER
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.data.database.dao.FilterDao
import com.tarasovvp.smartblocker.data.repositoryImpl.FilterRepositoryImpl
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithFilteredNumber
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.domain.repository.FilterRepository
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKER
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.just
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class FilterRepositoryUnitTest {
    @MockK
    private lateinit var filterDao: FilterDao

    private lateinit var filterRepository: FilterRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        filterRepository = FilterRepositoryImpl(filterDao)
    }

    @Test
    fun insertAllFiltersTest() =
        runBlocking {
            val filterList = listOf(Filter(filter = TEST_FILTER), Filter())
            coEvery { filterDao.insertAllFilters(filterList) } just Runs
            filterRepository.insertAllFilters(filterList)
            coVerify(exactly = 1) { filterDao.insertAllFilters(filterList) }
        }

    @Test
    fun allFiltersTest() =
        runBlocking {
            val filterList = listOf(Filter(filter = TEST_FILTER), Filter())
            coEvery { filterDao.allFilters() } returns filterList
            val result = filterRepository.allFilters()
            assertEquals(filterList, result)
        }

    @Test
    fun allFilterWithFilteredNumbersByTypeTest() =
        runBlocking {
            val filterWithFilteredNumberLists =
                listOf(
                    FilterWithFilteredNumber().apply { filter = Filter(filter = TEST_FILTER) },
                    FilterWithFilteredNumber().apply { filter = Filter() },
                )
            coEvery { filterDao.allFilterWithFilteredNumbersByType(BLOCKER) } returns filterWithFilteredNumberLists
            val result = filterRepository.allFilterWithFilteredNumbersByType(BLOCKER)
            assertEquals(filterWithFilteredNumberLists, result)
        }

    @Test
    fun getFilterTest() =
        runBlocking {
            val filterWithFilteredNumber =
                FilterWithFilteredNumber().apply {
                    filter =
                        Filter(
                            filter = TEST_FILTER,
                            conditionType = FilterCondition.FILTER_CONDITION_CONTAIN.ordinal,
                        )
                }
            coEvery { filterDao.getFilter(TEST_FILTER) } returns filterWithFilteredNumber
            val result = filterRepository.getFilter(TEST_FILTER)
            assertEquals(filterWithFilteredNumber, result)
        }

    @Test
    fun updateFilterTest() =
        runBlocking {
            val filter = Filter(filter = TEST_FILTER)
            coEvery { filterDao.updateFilter(filter) } just Runs
            filterRepository.updateFilter(filter)
            coVerify(exactly = 1) { filterDao.updateFilter(filter) }
        }

    @Test
    fun insertFilterTest() =
        runBlocking {
            val filter = Filter(filter = TEST_FILTER)
            coEvery { filterDao.insertFilter(filter) } just Runs
            filterRepository.insertFilter(filter)
            coVerify(exactly = 1) { filterDao.insertFilter(filter) }
        }

    @Test
    fun deleteFilterListTest() =
        runBlocking {
            val filters = listOf(Filter(filter = TEST_FILTER))
            coEvery { filterDao.deleteFilters(filters) } just Runs
            filterRepository.deleteFilterList(filters)
            coVerify(exactly = 1) { filterDao.deleteFilters(filters) }
        }

    @Test
    fun allFilterWithFilteredNumbersByNumberTest() =
        runBlocking {
            val filterWithFilteredNumberLists =
                listOf(
                    FilterWithFilteredNumber().apply { filter = Filter(filter = TEST_FILTER) },
                    FilterWithFilteredNumber().apply { filter = Filter() },
                )
            coEvery { filterDao.allFilterWithFilteredNumbersByNumber(TEST_NUMBER) } returns filterWithFilteredNumberLists
            val result = filterRepository.allFilterWithFilteredNumbersByNumber(TEST_NUMBER)
            assertEquals(filterWithFilteredNumberLists, result)
        }
}
