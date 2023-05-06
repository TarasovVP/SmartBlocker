package com.tarasovvp.smartblocker.data.repositoryImpl

import com.tarasovvp.smartblocker.data.database.dao.FilterDao
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.repository.FilterRepository
import javax.inject.Inject

class FilterRepositoryImpl @Inject constructor(
    private val filterDao: FilterDao
) : FilterRepository {

    override suspend fun insertAllFilters(filterList: List<Filter>) =
        filterDao.insertAllFilters(filterList)

    override suspend fun allFilters(): List<Filter> =
        filterDao.allFilters()

    override suspend fun allFiltersByType(filterType: Int): List<FilterWithCountryCode> =
        filterDao.allFiltersByType(filterType)

    override suspend fun getFilter(filter: String): FilterWithCountryCode? =
        filterDao.getFilter(filter)

    override suspend fun updateFilter(filter: Filter) =
        filterDao.updateFilter(filter)

    override suspend fun insertFilter(filter: Filter) =
        filterDao.insertFilter(filter)

    override suspend fun deleteFilterList(filterList: List<Filter>) =
        filterDao.deleteFilters(filterList)

    override suspend fun queryFilterList(number: String): List<FilterWithCountryCode> =
        filterDao.queryFullMatchFilterList(number)
}