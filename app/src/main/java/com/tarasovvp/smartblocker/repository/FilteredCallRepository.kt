package com.tarasovvp.smartblocker.repository

import com.tarasovvp.smartblocker.BlackListerApp
import com.tarasovvp.smartblocker.extensions.isTrue
import com.tarasovvp.smartblocker.model.FilteredCall
import com.tarasovvp.smartblocker.model.Call
import com.tarasovvp.smartblocker.model.Filter

object FilteredCallRepository {

    private val realDataBaseRepository = RealDataBaseRepository
    private val blockedCallDao = BlackListerApp.instance?.database?.blockedCallDao()

    suspend fun insertAllBlockedCalls(blockedCallList: ArrayList<FilteredCall>) {
        blockedCallDao?.deleteAllBlockCalls()
        blockedCallDao?.insertAllBlockedCalls(blockedCallList)
    }

    fun insertBlockedCall(filteredCall: FilteredCall) {
        if (BlackListerApp.instance?.isLoggedInUser().isTrue()) {
            realDataBaseRepository.insertBlockedCall(filteredCall) {
                blockedCallDao?.insertBlockedCall(filteredCall)
            }
        } else {
            blockedCallDao?.insertBlockedCall(filteredCall)
        }
    }

    suspend fun allBlockedCalls(): List<FilteredCall>? {
        return blockedCallDao?.allBlockedCalls()
    }

    suspend fun blockedCallsByNumber(number: String): List<FilteredCall>? {
        return blockedCallDao?.blockedCallsByNumber(number)
    }

    suspend fun blockedCallsByFilter(filter: Filter): List<FilteredCall>? {
        return blockedCallDao?.blockedCallsByFilter(filter)
    }

    fun deleteBlockedCalls(blockedCallList: List<Call>, result: () -> Unit) {
        if (BlackListerApp.instance?.isLoggedInUser().isTrue()) {
            realDataBaseRepository.deleteBlockedCallList(blockedCallList) {
                blockedCallDao?.deleteBlockCalls(blockedCallList.map { it.id })
                result.invoke()
            }
        } else {
            blockedCallDao?.deleteBlockCalls(blockedCallList.map { it.id })
            result.invoke()
        }
    }

}