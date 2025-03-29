package com.tarasovvp.smartblocker.domain.repository

import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithFilteredNumber

interface FilterRepository {
    suspend fun insertAllFilters(filterList: List<Filter>)

    suspend fun allFilters(): List<Filter>

    suspend fun allFilterWithFilteredNumbersByType(filterType: Int): List<FilterWithFilteredNumber>

    suspend fun getFilter(filter: String): FilterWithFilteredNumber?

    suspend fun updateFilter(filter: Filter)

    suspend fun insertFilter(filter: Filter)

    suspend fun deleteFilterList(filterList: List<Filter>)

    suspend fun allFilterWithFilteredNumbersByNumber(number: String): List<FilterWithFilteredNumber>
}
