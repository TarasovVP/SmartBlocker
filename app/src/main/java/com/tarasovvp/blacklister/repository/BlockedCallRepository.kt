package com.tarasovvp.blacklister.repository

import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.model.BlockedCall

object BlockedCallRepository {

    private val dao = BlackListerApp.instance?.database?.blockedCallDao()

    suspend fun insertBlockedCall(blockedCall: BlockedCall) {
        dao?.insertBlockedCall(blockedCall)
    }

    suspend fun allBlockedCalls(): List<BlockedCall>? {
        return dao?.allBlockedCalls()
    }

    suspend fun deleteAllCallLogs() {
        dao?.deleteAllCallLogs()
    }
}