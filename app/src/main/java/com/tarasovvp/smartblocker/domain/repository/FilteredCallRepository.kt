package com.tarasovvp.smartblocker.domain.repository

import com.tarasovvp.smartblocker.domain.entities.db_entities.FilteredCall
import com.tarasovvp.smartblocker.domain.entities.db_views.CallWithFilter

interface FilteredCallRepository {
    suspend fun insertAllFilteredCalls(filteredCallList: List<FilteredCall>)

    suspend fun insertFilteredCall(filteredCall: FilteredCall)

    suspend fun allFilteredCalls(): List<FilteredCall>

    suspend fun allFilteredCallsByFilter(filter: String): List<CallWithFilter>

    suspend fun allFilteredCallsByNumber(
        number: String,
        name: String,
    ): List<CallWithFilter>

    suspend fun deleteFilteredCalls(filteredCallIdList: List<Int>)
}
