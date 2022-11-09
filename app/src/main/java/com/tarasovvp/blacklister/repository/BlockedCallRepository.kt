package com.tarasovvp.blacklister.repository

import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.model.BlockedCall
import com.tarasovvp.blacklister.model.Call
import com.tarasovvp.blacklister.model.Filter

object BlockedCallRepository {

    private val dao = BlackListerApp.instance?.database?.blockedCallDao()

    suspend fun insertBlockedCall(blockedCall: BlockedCall?) {
        dao?.insertBlockedCall(blockedCall)
    }

    suspend fun allBlockedCalls(): List<BlockedCall>? {
        return dao?.allBlockedCalls()
    }

    suspend fun blockedCallsByNumber(number: String): List<BlockedCall>? {
        return dao?.blockedCallsByNumber(number)
    }

    suspend fun blockedCallsByFilter(filter: Filter): List<BlockedCall>? {
        return dao?.blockedCallsByFilter(filter)
    }

    suspend fun deleteBlockedCalls(callList: List<Call>) {
        dao?.deleteBlockCalls(callList.map { it.id })
    }

}