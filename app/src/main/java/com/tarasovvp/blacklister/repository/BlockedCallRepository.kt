package com.tarasovvp.blacklister.repository

import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.model.BlockedCall

object BlockedCallRepository {

    private val dao = BlackListerApp.instance?.database?.blockedCallDao()

    fun insertBlockedCall(blockedCall: BlockedCall) {
        dao?.insertBlockedCall(blockedCall)
    }

    fun allBlockedCalls(): List<BlockedCall>? {
        return dao?.allBlockedCalls()
    }

    fun deleteAllCallLogs() {
        dao?.deleteAllCallLogs()
    }
}