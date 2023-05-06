package com.tarasovvp.smartblocker.domain.usecases

import com.tarasovvp.smartblocker.domain.entities.models.CallWithFilter
import com.tarasovvp.smartblocker.domain.sealed_classes.Result

interface ListCallUseCase {

    suspend fun getCallList(): List<CallWithFilter>

    suspend fun getFilteredCallList(callList: List<CallWithFilter>, searchQuery: String, filterIndexes: ArrayList<Int>): List<CallWithFilter>

    suspend fun getHashMapFromCallList(callList: List<CallWithFilter>): Map<String, List<CallWithFilter>>?

    suspend fun deleteCallList(filteredCallIdList: List<Int>, isNetworkAvailable: Boolean, result: (Result<Unit>) -> Unit)
}
