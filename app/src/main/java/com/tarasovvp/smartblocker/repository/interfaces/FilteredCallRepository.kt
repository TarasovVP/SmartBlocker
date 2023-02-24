package com.tarasovvp.smartblocker.repository.interfaces

import com.tarasovvp.smartblocker.models.Call
import com.tarasovvp.smartblocker.models.FilteredCall

interface FilteredCallRepository {

    suspend fun insertAllFilteredCalls(filteredCallList: ArrayList<FilteredCall>)

    fun insertFilteredCall(filteredCall: FilteredCall)

    suspend fun allFilteredCalls(): List<FilteredCall>

    suspend fun filteredCallsByFilter(filter: String): List<FilteredCall>

    suspend fun filteredCallsByNumber(number: String): List<FilteredCall>

    fun deleteFilteredCalls(filteredCallList: List<Call>, result: () -> Unit)
}