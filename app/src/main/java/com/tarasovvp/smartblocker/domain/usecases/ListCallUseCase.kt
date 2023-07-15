package com.tarasovvp.smartblocker.domain.usecases

import com.tarasovvp.smartblocker.domain.entities.db_views.CallWithFilter
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import kotlinx.coroutines.flow.Flow

interface ListCallUseCase {

    suspend fun allCallWithFilters(): List<CallWithFilter>

    suspend fun deleteCallList(filteredCallIdList: List<Int>, isNetworkAvailable: Boolean, result: (Result<Unit>) -> Unit)

    suspend fun getReviewVoted() : Flow<Boolean?>

    fun setReviewVoted(result: (Result<Unit>) -> Unit)
}
