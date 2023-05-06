package com.tarasovvp.smartblocker.presentation.main.number.list.list_call

import com.google.firebase.auth.FirebaseAuth
import com.tarasovvp.smartblocker.domain.models.entities.CallWithFilter
import com.tarasovvp.smartblocker.domain.repository.FilteredCallRepository
import com.tarasovvp.smartblocker.domain.repository.LogCallRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecase.ListCallUseCase
import com.tarasovvp.smartblocker.utils.extensions.isNotNull
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class ListCallUseCaseImpl @Inject constructor(
    private val logCallRepository: LogCallRepository,
    private val filteredCallRepository: FilteredCallRepository,
    private val realDataBaseRepository: RealDataBaseRepository,
    private val firebaseAuth: FirebaseAuth
): ListCallUseCase {

    override suspend fun getCallList(): List<CallWithFilter> {
            val logCalls = logCallRepository.getAllLogCallWithFilter()
            val filteredCalls =  filteredCallRepository.allFilteredCallWithFilter()
            val callList = ArrayList<CallWithFilter>().apply {
                addAll(filteredCalls)
                addAll(logCalls)
            }
            return callList.distinctBy {
                it.call?.callId
            }
    }

    override suspend fun getFilteredCallList(callList: List<CallWithFilter>, searchQuery: String, filterIndexes: ArrayList<Int>): List<CallWithFilter> = logCallRepository.getFilteredCallList(callList, searchQuery, filterIndexes)

    override suspend fun getHashMapFromCallList(callList: List<CallWithFilter>) = logCallRepository.getHashMapFromCallList(callList.sortedByDescending {
        it.call?.callDate
    })

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
