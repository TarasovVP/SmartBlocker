package com.tarasovvp.smartblocker.domain.repository

import com.tarasovvp.smartblocker.data.database.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.data.database.entities.Filter

interface FilterRepository {

    suspend fun getHashMapFromFilterList(filterList: List<FilterWithCountryCode>): Map<String, List<FilterWithCountryCode>>

    suspend fun insertAllFilters(filterList: ArrayList<Filter>)

    suspend fun allFilters(): List<Filter>

    suspend fun allFilterWithCountryCode(): List<FilterWithCountryCode>

    suspend fun allFiltersByType(filterType: Int): List<FilterWithCountryCode>

    suspend fun getFilter(filter: FilterWithCountryCode): FilterWithCountryCode?

    suspend fun updateFilter(filter: Filter, result: () -> Unit)

    suspend fun insertFilter(filter: Filter, result: () -> Unit)

    suspend fun deleteFilterList(filterList: List<Filter?>, result: () -> Unit)

    suspend fun queryFilterList(number: String): List<FilterWithCountryCode>

    suspend fun queryFilter(number: String): FilterWithCountryCode?
}