package com.tarasovvp.smartblocker.domain.usecase.number.list.list_call

import com.tarasovvp.smartblocker.domain.models.entities.CallWithFilter

interface ListCallUseCase {

    suspend fun getCallList(): List<CallWithFilter>

    suspend fun getHashMapFromCallList(callList: List<CallWithFilter>): Map<String, List<CallWithFilter>>?

    suspend fun deleteCallList(filteredCallIdList: List<Int>, result: () -> Unit)
}
