package com.tarasovvp.smartblocker.domain.usecase.number.list.list_call

import com.tarasovvp.smartblocker.domain.models.entities.CallWithFilter
import com.tarasovvp.smartblocker.domain.repository.FilteredCallRepository
import com.tarasovvp.smartblocker.domain.repository.LogCallRepository
import javax.inject.Inject

class ListCallUseCaseImpl @Inject constructor(
    private val logCallRepository: LogCallRepository,
    private val filteredCallRepository: FilteredCallRepository
): ListCallUseCase {

    override suspend fun getCallList(): List<CallWithFilter> {
            val logCalls = logCallRepository.getAllLogCallWithFilter()
            val filteredCalls =  filteredCallRepository.allFilteredCallWithFilter()
            val filteredCallList = filteredCalls
            val logCallList = logCalls
            val callList = ArrayList<CallWithFilter>().apply {
                addAll(filteredCallList)
                addAll(logCallList)
            }
            return callList.distinctBy {
                it.call?.callId
            }
    }

    override suspend fun getFilteredCallList(callList: List<CallWithFilter>, searchQuery: String, filterIndexes: ArrayList<Int>): List<CallWithFilter> = logCallRepository.getFilteredCallList(callList, searchQuery, filterIndexes)

    override suspend fun getHashMapFromCallList(callList: List<CallWithFilter>) = logCallRepository.getHashMapFromCallList(callList.sortedByDescending {
        it.call?.callDate
    })

    override suspend fun deleteCallList(filteredCallIdList: List<Int>, result: () -> Unit) = filteredCallRepository.deleteFilteredCalls(filteredCallIdList) {
        result.invoke()
    }
}
