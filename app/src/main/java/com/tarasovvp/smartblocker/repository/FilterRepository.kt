package com.tarasovvp.smartblocker.repository

import com.tarasovvp.smartblocker.models.Filter
import com.tarasovvp.smartblocker.models.FilterWithCountryCode

interface FilterRepository {

    suspend fun getHashMapFromFilterList(filterList: List<FilterWithCountryCode>): Map<String, List<FilterWithCountryCode>>

    suspend fun insertAllFilters(filterList: ArrayList<Filter>)

    suspend fun allFilters(): List<Filter>

    suspend fun allFilterWithCountryCode(): List<FilterWithCountryCode>

    suspend fun allFiltersByType(filterType: Int): List<FilterWithCountryCode>

    suspend fun getFilter(filter: FilterWithCountryCode): FilterWithCountryCode?

    fun updateFilter(filter: Filter, result: () -> Unit)

    fun insertFilter(filter: Filter, result: () -> Unit)

    fun deleteFilterList(filterList: List<Filter?>, result: () -> Unit)

    suspend fun queryFilterList(number: String): List<FilterWithCountryCode>

    suspend fun queryFilter(number: String): FilterWithCountryCode?
}