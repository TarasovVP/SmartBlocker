package com.tarasovvp.smartblocker.domain.repository

import com.tarasovvp.smartblocker.domain.models.database_views.FilteredCallWithFilter
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.domain.models.entities.FilteredCall

interface FilteredCallRepository {

    suspend fun setFilterToFilteredCall(filterList: List<Filter>, callList: List<FilteredCall>, result: (Int, Int) -> Unit): List<FilteredCall>

    suspend fun insertAllFilteredCalls(filteredCallList: List<FilteredCall>)

    suspend fun insertFilteredCall(filteredCall: FilteredCall)

    suspend fun allFilteredCalls(): List<FilteredCall>

    suspend fun allFilteredCallWithFilter(): List<FilteredCallWithFilter>

    suspend fun filteredCallsByFilter(filter: String): List<FilteredCallWithFilter>

    suspend fun filteredCallsByNumber(number: String): List<FilteredCallWithFilter>

    suspend fun deleteFilteredCalls(filteredCallIdList: List<Int>, result: () -> Unit)
}