package com.tarasovvp.blacklister.provider

import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.model.BlockedCall
import com.tarasovvp.blacklister.model.CallLog

interface BlockedCallRepository {
    suspend fun insertBlockedCall(blockedCall: BlockedCall)
    suspend fun allBlockedCalls(): List<BlockedCall>?
    suspend fun deleteAllCallLogs()
}

object BlockedCallRepositoryImpl : BlockedCallRepository {

    private val dao = BlackListerApp.instance?.database?.blockedCallDao()

    override suspend fun insertBlockedCall(blockedCall: BlockedCall) {
        dao?.insertBlockedCall(blockedCall)
    }

    override suspend fun allBlockedCalls(): List<BlockedCall>? {
        return dao?.allBlockedCalls()
    }

    override suspend fun deleteAllCallLogs() {
        dao?.deleteAllCallLogs()
    }
}