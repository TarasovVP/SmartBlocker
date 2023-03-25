package com.tarasovvp.smartblocker.usecases

import com.tarasovvp.smartblocker.domain.models.entities.CallWithFilter
import com.tarasovvp.smartblocker.domain.repository.FilteredCallRepository
import com.tarasovvp.smartblocker.domain.repository.LogCallRepository
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import javax.inject.Inject

@RunWith(MockitoJUnitRunner::class)
class ListCallUseCaseTest @Inject constructor(
    private val logCallRepository: LogCallRepository,
    private val filteredCallRepository: FilteredCallRepository
) {

    suspend fun getCallList(): List<CallWithFilter> {
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

    suspend fun getHashMapFromCallList(callList: List<CallWithFilter>) = logCallRepository.getHashMapFromCallList(callList.sortedByDescending {
        it.call?.callDate
    })

    suspend fun deleteCallList(filteredCallIdList: List<Int>, result: () -> Unit) = filteredCallRepository.deleteFilteredCalls(filteredCallIdList) {
        result.invoke()
    }
}
