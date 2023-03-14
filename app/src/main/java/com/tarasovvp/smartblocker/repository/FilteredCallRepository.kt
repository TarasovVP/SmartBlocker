package com.tarasovvp.smartblocker.repository

import com.tarasovvp.smartblocker.models.FilteredCall
import com.tarasovvp.smartblocker.models.FilteredCallWithFilter

interface FilteredCallRepository {

    suspend fun insertAllFilteredCalls(filteredCallList: ArrayList<FilteredCall>)

    fun insertFilteredCall(filteredCall: FilteredCall)

    suspend fun allFilteredCalls(): List<FilteredCall>

    suspend fun allFilteredCallWithFilter(): List<FilteredCallWithFilter>

    suspend fun filteredCallsByFilter(filter: String): List<FilteredCallWithFilter>

    suspend fun filteredCallsByNumber(number: String): List<FilteredCallWithFilter>

    fun deleteFilteredCalls(filteredCallIdList: List<Int>, result: () -> Unit)
}