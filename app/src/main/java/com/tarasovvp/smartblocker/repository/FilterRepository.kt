package com.tarasovvp.smartblocker.repository

import com.tarasovvp.smartblocker.models.Filter
import com.tarasovvp.smartblocker.models.FilterWithCountryCode

interface FilterRepository {

    suspend fun getHashMapFromFilterList(filterList: List<Filter>): Map<String, List<Filter>>

    suspend fun insertAllFilters(filterList: ArrayList<Filter>)

    suspend fun allFilters(): List<Filter>

    suspend fun allFiltersByType(filterType: Int): List<Filter>

    suspend fun getFilter(filter: Filter): Filter?

    suspend fun getFilterWithCountryCode(filter: Filter): FilterWithCountryCode?

    fun updateFilter(filter: Filter, result: () -> Unit)

    fun insertFilter(filter: Filter, result: () -> Unit)

    fun deleteFilterList(filterList: List<Filter>, result: () -> Unit)

    suspend fun queryFilterList(number: String): List<Filter>

    suspend fun queryFilter(number: String): Filter?
}