package com.tarasovvp.smartblocker.data.repositoryImpl

import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.data.database.dao.FilterDao
import com.tarasovvp.smartblocker.data.database.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.data.database.entities.Filter
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import com.tarasovvp.smartblocker.domain.repository.FilterRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FilterRepositoryImpl @Inject constructor(
    private val filterDao: FilterDao,
    private val realDataBaseRepository: RealDataBaseRepository
) : FilterRepository {

    override suspend fun getHashMapFromFilterList(filterList: List<FilterWithCountryCode>): Map<String, List<FilterWithCountryCode>> =
        withContext(Dispatchers.Default) {
            filterList.groupBy {
                String.EMPTY
            }
        }

    override suspend fun insertAllFilters(filterList: ArrayList<Filter>) {
        filterDao.deleteAllFilters()
        filterDao.insertAllFilters(filterList)
    }

    override suspend fun allFilters(): List<Filter> {
        return filterDao.allFilters()
    }

    override suspend fun allFilterWithCountryCode(): List<FilterWithCountryCode> {
        return filterDao.allFilterWithCountryCode()
    }

    override suspend fun allFiltersByType(filterType: Int): List<FilterWithCountryCode> {
        return filterDao.allFiltersByType(filterType)
    }

    override suspend fun getFilter(filter: FilterWithCountryCode): FilterWithCountryCode? {
        return filterDao.getFilter(filter.createFilter())
    }

    override suspend fun updateFilter(filter: Filter, result: () -> Unit) {
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

    override suspend fun insertFilter(filter: Filter, result: () -> Unit) {
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

    override suspend fun deleteFilterList(filterList: List<Filter?>, result: () -> Unit) {
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

    override suspend fun queryFilterList(number: String): List<FilterWithCountryCode> {
        return filterDao.queryFullMatchFilterList(number).sortedWith(
            compareByDescending<FilterWithCountryCode> { it.filter?.filter?.length }
                .thenBy { number.indexOf(it.filter?.filter.orEmpty()) }
        )
    }

    override suspend fun queryFilter(number: String): FilterWithCountryCode? {
        return queryFilterList(number).firstOrNull()
    }

}