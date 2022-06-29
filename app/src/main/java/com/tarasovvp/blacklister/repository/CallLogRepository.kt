package com.tarasovvp.blacklister.repository

import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.extensions.toHashMapFromList
import com.tarasovvp.blacklister.model.CallLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CallLogRepository {

    private val dao = BlackListerApp.instance?.database?.callLogDao()

    suspend fun insertCallLogs(callLogList: List<CallLog>) {
        dao?.insertAllCallLogs(callLogList)
    }

    suspend fun getAllCallLogs(): List<CallLog>? {
        return dao?.allCallLogs()
    }

    suspend fun updateCallLog(callLog: CallLog) {
        dao?.updateCallLog(callLog)
    }

    suspend fun deleteAllCallLogs() {
        dao?.deleteAllCallLogs()
    }

    suspend fun getHashMapFromCallLogList(callLogList: List<CallLog>): HashMap<String, List<CallLog>> =
        withContext(
            Dispatchers.Default
        ) {
            callLogList.toHashMapFromList()
        }

}