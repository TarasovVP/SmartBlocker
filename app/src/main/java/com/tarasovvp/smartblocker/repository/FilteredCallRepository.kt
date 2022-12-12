package com.tarasovvp.smartblocker.repository

import com.tarasovvp.smartblocker.BlackListerApp
import com.tarasovvp.smartblocker.extensions.isTrue
import com.tarasovvp.smartblocker.models.Call
import com.tarasovvp.smartblocker.models.FilteredCall

object FilteredCallRepository {

    private val realDataBaseRepository = RealDataBaseRepository
    private val filteredCallDao = BlackListerApp.instance?.database?.filteredCallDao()

    suspend fun insertAllFilteredCalls(filteredCallList: ArrayList<FilteredCall>) {
        filteredCallDao?.deleteAllFilteredCalls()
        filteredCallDao?.insertAllFilteredCalls(filteredCallList)
    }

    fun insertFilteredCall(filteredCall: FilteredCall) {
        if (BlackListerApp.instance?.isLoggedInUser().isTrue()) {
            realDataBaseRepository.insertFilteredCall(filteredCall) {
                filteredCallDao?.insertFilteredCall(filteredCall)
            }
        } else {
            filteredCallDao?.insertFilteredCall(filteredCall)
        }
    }

    suspend fun allFilteredCalls(): List<FilteredCall>? {
        return filteredCallDao?.allFilteredCalls()
    }

    suspend fun filteredCallsByFilter(filter: String): List<FilteredCall>? {
        return filteredCallDao?.filteredCallsByFilter(filter)
    }

    fun deleteFilteredCalls(filteredCallList: List<Call>, result: () -> Unit) {
        if (BlackListerApp.instance?.isLoggedInUser().isTrue()) {
            realDataBaseRepository.deleteFilteredCallList(filteredCallList) {
                filteredCallDao?.deleteFilteredCalls(filteredCallList.map { it.callId })
                result.invoke()
            }
        } else {
            filteredCallDao?.deleteFilteredCalls(filteredCallList.map { it.callId })
            result.invoke()
        }
    }

}