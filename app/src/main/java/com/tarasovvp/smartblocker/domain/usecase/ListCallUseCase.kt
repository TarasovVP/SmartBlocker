package com.tarasovvp.smartblocker.domain.usecase

import com.tarasovvp.smartblocker.domain.sealed_classes.OperationResult
import com.tarasovvp.smartblocker.presentation.ui_models.CallWithFilterUIModel

interface ListCallUseCase {

    suspend fun getCallList(): List<CallWithFilterUIModel>

    suspend fun getFilteredCallList(callList: List<CallWithFilterUIModel>, searchQuery: String, filterIndexes: ArrayList<Int>): List<CallWithFilterUIModel>

    suspend fun getHashMapFromCallList(callList: List<CallWithFilterUIModel>): Map<String, List<CallWithFilterUIModel>>?

    suspend fun deleteCallList(filteredCallIdList: List<Int>, isLoggedInUser: Boolean, result: (OperationResult<Unit>) -> Unit)
}
