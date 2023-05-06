package com.tarasovvp.smartblocker.domain.repository

import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.models.entities.Filter

interface FilterRepository {

    suspend fun insertAllFilters(filterList: List<Filter>)

    suspend fun allFilters(): List<Filter>

    suspend fun allFiltersByType(filterType: Int): List<FilterWithCountryCode>

    suspend fun getFilter(filter: String): FilterWithCountryCode?

    suspend fun updateFilter(filter: Filter)

    suspend fun insertFilter(filter: Filter)

    suspend fun deleteFilterList(filterList: List<Filter>)

    suspend fun queryFilterList(number: String): List<FilterWithCountryCode>
}