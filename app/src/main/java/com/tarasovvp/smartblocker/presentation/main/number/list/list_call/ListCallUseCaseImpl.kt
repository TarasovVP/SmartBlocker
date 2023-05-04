package com.tarasovvp.smartblocker.presentation.main.number.list.list_call

import com.tarasovvp.smartblocker.domain.enums.NumberDataFiltering
import com.tarasovvp.smartblocker.presentation.ui_models.CallWithFilterUIModel
import com.tarasovvp.smartblocker.domain.repository.FilteredCallRepository
import com.tarasovvp.smartblocker.domain.repository.LogCallRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.sealed_classes.OperationResult
import com.tarasovvp.smartblocker.domain.usecase.ListCallUseCase
import com.tarasovvp.smartblocker.utils.extensions.isContaining
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ListCallUseCaseImpl @Inject constructor(
    private val logCallRepository: LogCallRepository,
    private val filteredCallRepository: FilteredCallRepository,
    private val realDataBaseRepository: RealDataBaseRepository
): ListCallUseCase {

    override suspend fun getCallList(): List<CallWithFilterUIModel> {
            val logCalls = logCallRepository.getAllLogCallWithFilter()
            val filteredCalls =  filteredCallRepository.allFilteredCallWithFilter()
            val callList = ArrayList<CallWithFilterUIModel>().apply {
                addAll(filteredCalls)
                addAll(logCalls)
            }
            return callList.distinctBy {
                it.callUIModel?.callId
            }
    }

    override suspend fun getFilteredCallList(
        callList: List<CallWithFilterUIModel>,
        searchQuery: String,
        filterIndexes: ArrayList<Int>
    ): List<CallWithFilterUIModel> {
        return if (searchQuery.isBlank() && filterIndexes.isEmpty()) callList else callList.filter { callWithFilter ->
            (callWithFilter.callUIModel?.callName isContaining searchQuery || callWithFilter.callUIModel?.number isContaining searchQuery)
                    && (callWithFilter.callUIModel?.isBlockedCall().isTrue() && filterIndexes.contains(
                NumberDataFiltering.CALL_BLOCKED.ordinal).isTrue()
                    || callWithFilter.callUIModel?.isPermittedCall().isTrue() && filterIndexes.contains(
                NumberDataFiltering.CALL_PERMITTED.ordinal).isTrue()
                    || filterIndexes.isEmpty())
        }
    }

    override suspend fun getHashMapFromCallList(logCallList: List<CallWithFilterUIModel>): Map<String, List<CallWithFilterUIModel>> =
        withContext(Dispatchers.Default) {
            logCallList.sortedByDescending {
                it.callUIModel?.callDate
            }.groupBy { it.callUIModel?.dateFromCallDate().toString() }
        }

    override suspend fun deleteCallList(filteredCallIdList: List<Int>, isLoggedInUser: Boolean, result: (OperationResult<Unit>) -> Unit) {
        if (isLoggedInUser) {
            realDataBaseRepository.deleteFilteredCallList(filteredCallIdList.map { it.toString() }) {
                runBlocking {
                    filteredCallRepository.deleteFilteredCalls(filteredCallIdList)
                    result.invoke()
                }
            }
        } else {
            filteredCallRepository.deleteFilteredCalls(filteredCallIdList)
            result.invoke()
        }
    }
}
