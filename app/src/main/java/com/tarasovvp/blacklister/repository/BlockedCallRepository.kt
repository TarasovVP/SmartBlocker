package com.tarasovvp.blacklister.repository

import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.model.BlockedCall
import com.tarasovvp.blacklister.model.Call
import com.tarasovvp.blacklister.model.Filter

object BlockedCallRepository {

    private val realDataBaseRepository = RealDataBaseRepository
    private val blockedCallDao = BlackListerApp.instance?.database?.blockedCallDao()

    suspend fun insertAllBlockedCalls(blockedCallList: ArrayList<BlockedCall>) {
        blockedCallDao?.deleteAllBlockCalls()
        blockedCallDao?.insertAllBlockedCalls(blockedCallList)
    }

    fun insertBlockedCall(blockedCall: BlockedCall) {
        if (BlackListerApp.instance?.isLoggedInUser().isTrue()) {
            realDataBaseRepository.insertBlockedCall(blockedCall) {
                blockedCallDao?.insertBlockedCall(blockedCall)
            }
        } else {
            blockedCallDao?.insertBlockedCall(blockedCall)
        }
    }

    suspend fun allBlockedCalls(): List<BlockedCall>? {
        return blockedCallDao?.allBlockedCalls()
    }

    suspend fun blockedCallsByNumber(number: String): List<BlockedCall>? {
        return blockedCallDao?.blockedCallsByNumber(number)
    }

    suspend fun blockedCallsByFilter(filter: Filter): List<BlockedCall>? {
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