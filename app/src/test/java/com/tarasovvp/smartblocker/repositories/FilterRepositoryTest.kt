package com.tarasovvp.smartblocker.repositories

import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.data.database.dao.FilterDao
import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import com.tarasovvp.smartblocker.domain.repository.FilterRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import javax.inject.Inject

@RunWith(MockitoJUnitRunner::class)
class FilterRepositoryTest @Inject constructor(
    private val filterDao: FilterDao,
    private val realDataBaseRepository: RealDataBaseRepository
) {

    suspend fun getHashMapFromFilterList(filterList: List<FilterWithCountryCode>): Map<String, List<FilterWithCountryCode>> =
        withContext(Dispatchers.Default) {
            filterList.groupBy {
                String.EMPTY
            }
        }

    suspend fun insertAllFilters(filterList: List<Filter>) {
        filterDao.deleteAllFilters()
        filterDao.insertAllFilters(filterList)
    }

    suspend fun allFilters(): List<Filter> {
        return filterDao.allFilters()
    }

    suspend fun allFilterWithCountryCode(): List<FilterWithCountryCode> {
        return filterDao.allFilterWithCountryCode()
    }

    suspend fun allFiltersByType(filterType: Int): List<FilterWithCountryCode> {
        return filterDao.allFiltersByType(filterType)
    }

    suspend fun getFilter(filter: FilterWithCountryCode): FilterWithCountryCode? {
        return filterDao.getFilter(filter.createFilter())
    }

    suspend fun updateFilter(filter: Filter, result: () -> Unit) {
        if (SmartBlockerApp.instance?.isLoggedInUser().isTrue()) {
            realDataBaseRepository.insertFilter(filter) {
                filterDao.updateFilter(filter)
                result.invoke()
            }
        } else {
            filterDao.updateFilter(filter)
            result.invoke()
        }
    }

    suspend fun insertFilter(filter: Filter, result: () -> Unit) {
        if (SmartBlockerApp.instance?.isLoggedInUser().isTrue()) {
            realDataBaseRepository.insertFilter(filter) {
                filterDao.insertFilter(filter)
                result.invoke()
            }
        } else {
            filterDao.insertFilter(filter)
            result.invoke()
        }
    }

    suspend fun deleteFilterList(filterList: List<Filter?>, result: () -> Unit) {
        if (SmartBlockerApp.instance?.isLoggedInUser().isTrue()) {
            realDataBaseRepository.deleteFilterList(filterList) {
                filterList.forEach { filter ->
                    filter?.let { filterDao.delete(it) }
                }
                result.invoke()
            }
        } else {
            filterList.forEach { filter ->
                filter?.let { filterDao.delete(it) }
            }
            result.invoke()
        }
    }

    suspend fun queryFilterList(number: String): List<FilterWithCountryCode> {
        return filterDao.queryFullMatchFilterList(number).sortedWith(
            compareByDescending<FilterWithCountryCode> { it.filter?.filter?.length }
                .thenBy { number.indexOf(it.filter?.filter.orEmpty()) }
        )
    }

    suspend fun queryFilter(number: String): FilterWithCountryCode? {
        return queryFilterList(number).firstOrNull()
    }

}