package com.tarasovvp.blacklister.repository

import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.model.BlockedCall
import com.tarasovvp.blacklister.model.Call

object BlockedCallRepository {

    private val dao = BlackListerApp.instance?.database?.blockedCallDao()

    fun insertBlockedCall(blockedCall: BlockedCall?) {
        dao?.insertBlockedCall(blockedCall)
    }

    fun allBlockedCalls(): List<BlockedCall>? {
        return dao?.allBlockedCalls()
    }

    fun deleteBlockedCalls(callList: List<Call>) {
        dao?.deleteBlockCalls(callList.map { it.id })
    }

}