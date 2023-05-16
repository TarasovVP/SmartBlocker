package com.tarasovvp.smartblocker.domain.repository

import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithFilteredNumbers
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter

interface FilterRepository {

    suspend fun insertAllFilters(filterList: List<Filter>)

    suspend fun allFilters(): List<Filter>

    suspend fun allFilterWithFilteredNumbersByType(filterType: Int): List<FilterWithFilteredNumbers>

    suspend fun getFilter(filter: String): FilterWithFilteredNumbers?

    suspend fun updateFilter(filter: Filter)

    suspend fun insertFilter(filter: Filter)

    suspend fun deleteFilterList(filterList: List<Filter>)

    suspend fun allFilterWithFilteredNumbersByNumber(number: String): List<FilterWithFilteredNumbers>
}