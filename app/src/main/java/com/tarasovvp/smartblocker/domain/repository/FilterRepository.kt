package com.tarasovvp.smartblocker.domain.repository

import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.models.entities.Filter

interface FilterRepository {

    suspend fun insertAllFilters(filterList: List<Filter>)

    suspend fun allFilters(): List<Filter>

    suspend fun allFilterWithCountryCode(): List<FilterWithCountryCode>

    suspend fun allFiltersByType(filterType: Int): List<FilterWithCountryCode>

    suspend fun getFilter(filter: FilterWithCountryCode): FilterWithCountryCode?

    suspend fun updateFilter(filter: Filter, result: () -> Unit)

    suspend fun insertFilter(filter: Filter, result: () -> Unit)

    suspend fun deleteFilterList(filterList: List<Filter?>, result: () -> Unit)

    suspend fun getFilteredFilterList(filterList: List<FilterWithCountryCode>, searchQuery: String, filterIndexes: ArrayList<Int>): List<FilterWithCountryCode>

    suspend fun queryFilterList(number: String): List<FilterWithCountryCode>

    suspend fun queryFilter(number: String): FilterWithCountryCode?
}