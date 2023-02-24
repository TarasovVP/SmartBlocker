package com.tarasovvp.smartblocker.repository.implementations

import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.database.dao.FilterDao
import com.tarasovvp.smartblocker.extensions.EMPTY
import com.tarasovvp.smartblocker.extensions.filteredFilterList
import com.tarasovvp.smartblocker.extensions.isTrue
import com.tarasovvp.smartblocker.models.Filter
import com.tarasovvp.smartblocker.repository.interfaces.FilterRepository
import com.tarasovvp.smartblocker.repository.interfaces.RealDataBaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FilterRepositoryImpl @Inject constructor(
    private val filterDao: FilterDao,
    private val realDataBaseRepository: RealDataBaseRepository
) : FilterRepository {

    override suspend fun getHashMapFromFilterList(filterList: List<Filter>): Map<String, List<Filter>> =
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

    override suspend fun allFiltersByType(filterType: Int): List<Filter> {
        return filterDao.allFiltersByType(filterType)
    }

    override suspend fun getFilter(filter: Filter): Filter? {
        return filterDao.getFilter(filter.createFilter(), filter.conditionType)
    }

    override fun updateFilter(filter: Filter, result: () -> Unit) {
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

    override fun insertFilter(filter: Filter, result: () -> Unit) {
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

    override fun deleteFilterList(filterList: List<Filter>, result: () -> Unit) {
        if (SmartBlockerApp.instance?.isLoggedInUser().isTrue()) {
            realDataBaseRepository.deleteFilterList(filterList) {
                filterList.forEach { filter ->
                    filterDao.delete(filter)
                }
                result.invoke()
            }
        } else {
            filterList.forEach { whiteFilter ->
                filterDao.delete(whiteFilter)
            }
            result.invoke()
        }
    }

    override suspend fun queryFilterList(number: String): List<Filter> {
        val filterList = filterDao.allFilters()
        return filterList.filteredFilterList(number)
    }

    override suspend fun queryFilter(number: String): Filter? {
        val filterList = filterDao.queryFullMatchFilterList(number)
        val maxLengthFilterList =
            filterList.filter { filter -> filter.filter.length == filterList.maxByOrNull { it.filter.length }?.filter?.length }
        return if (maxLengthFilterList.size > 1) maxLengthFilterList
            .minByOrNull {
                number.indexOf(it.filter)
            } else maxLengthFilterList.firstOrNull()
    }

}