package com.tarasovvp.smartblocker.data.repositoryImpl

import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.data.database.dao.FilterDao
import com.tarasovvp.smartblocker.domain.enums.NumberDataFiltering
import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import com.tarasovvp.smartblocker.domain.repository.FilterRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.utils.extensions.isContaining
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FilterRepositoryImpl @Inject constructor(
    private val filterDao: FilterDao,
    private val realDataBaseRepository: RealDataBaseRepository
) : FilterRepository {

    override suspend fun insertAllFilters(filterList: List<Filter>) {
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

    override suspend fun getFilteredFilterList(
        filterList: List<FilterWithCountryCode>,
        searchQuery: String,
        filterIndexes: ArrayList<Int>
    ) = withContext(Dispatchers.Default) {
        if (searchQuery.isBlank() && filterIndexes.isEmpty()) filterList else filterList.filter { filterWithCountryCode ->
            (filterWithCountryCode.filter?.filter isContaining  searchQuery)
                    && (filterIndexes.contains(NumberDataFiltering.FILTER_CONDITION_FULL_FILTERING.ordinal) && filterWithCountryCode.filter?.isTypeFull().isTrue()
                    || filterIndexes.contains(NumberDataFiltering.FILTER_CONDITION_START_FILTERING.ordinal) && filterWithCountryCode.filter?.isTypeStart().isTrue()
                    || filterIndexes.contains(NumberDataFiltering.FILTER_CONDITION_CONTAIN_FILTERING.ordinal) && filterWithCountryCode.filter?.isTypeContain().isTrue()
                    || filterIndexes.isEmpty())
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