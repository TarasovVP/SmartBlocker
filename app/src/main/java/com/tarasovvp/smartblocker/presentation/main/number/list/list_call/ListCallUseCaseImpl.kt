package com.tarasovvp.smartblocker.presentation.main.number.list.list_call

import com.google.firebase.auth.FirebaseAuth
import com.tarasovvp.smartblocker.domain.enums.NumberDataFiltering
import com.tarasovvp.smartblocker.domain.entities.db_views.CallWithFilter
import com.tarasovvp.smartblocker.domain.repository.FilteredCallRepository
import com.tarasovvp.smartblocker.domain.repository.LogCallRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.ListCallUseCase
import com.tarasovvp.smartblocker.utils.extensions.isContaining
import com.tarasovvp.smartblocker.utils.extensions.isNotNull
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ListCallUseCaseImpl @Inject constructor(
    private val logCallRepository: LogCallRepository,
    private val filteredCallRepository: FilteredCallRepository,
    private val realDataBaseRepository: RealDataBaseRepository,
    private val firebaseAuth: FirebaseAuth
): ListCallUseCase {

    override suspend fun getCallList(): List<CallWithFilter> {
            val callList = logCallRepository.getAllCallWithFilter()
            return callList.distinctBy {
                it.call?.callId
            }
    }

    override suspend fun getFilteredCallList(
        callList: List<CallWithFilter>,
        searchQuery: String,
        filterIndexes: ArrayList<Int>
    ): List<CallWithFilter> {
        return if (searchQuery.isBlank() && filterIndexes.isEmpty()) callList else callList.filter { callWithFilter ->
            (callWithFilter.call?.callName isContaining searchQuery || callWithFilter.call?.number isContaining searchQuery)
                    && (callWithFilter.call?.isBlockedCall().isTrue() && filterIndexes.contains(
                NumberDataFiltering.CALL_BLOCKED.ordinal).isTrue()
                    || callWithFilter.call?.isPermittedCall().isTrue() && filterIndexes.contains(
                NumberDataFiltering.CALL_PERMITTED.ordinal).isTrue()
                    || filterIndexes.isEmpty())
        }
    }

    override suspend fun getHashMapFromCallList(callList: List<CallWithFilter>): Map<String, List<CallWithFilter>> =
        withContext(Dispatchers.Default) {
            callList.sortedByDescending {
                it.call?.callDate
            }.groupBy { it.call?.dateFromCallDate().toString() }
        }

    override suspend fun deleteCallList(filteredCallIdList: List<Int>, isNetworkAvailable: Boolean, result: (Result<Unit>) -> Unit) {
        if (firebaseAuth.currentUser.isNotNull()) {
            if (isNetworkAvailable) {
                realDataBaseRepository.deleteFilteredCallList(filteredCallIdList.map { it.toString() }) {
                    runBlocking {
                        filteredCallRepository.deleteFilteredCalls(filteredCallIdList)
                        result.invoke(Result.Success())
                    }
                }
            } else {
                result.invoke(Result.Failure())
            }
        } else {
            filteredCallRepository.deleteFilteredCalls(filteredCallIdList)
            result.invoke(Result.Success())
        }
    }
}
