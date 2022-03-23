package com.example.blacklister.provider

import com.example.blacklister.BlackListerApp
import com.example.blacklister.extensions.toHashMapFromList
import com.example.blacklister.model.CallLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface CallLogRepository {
    suspend fun insertCallLogs(callLogList: List<CallLog>)
    suspend fun getAllCallLogs(): List<CallLog>?
    suspend fun updateCallLog(callLog: CallLog)
    suspend fun deleteAllCallLogs()
    suspend fun getHashMapFromCallLogList(callLogList: List<CallLog>): HashMap<String, List<CallLog>>
}

object CallLogRepositoryImpl : CallLogRepository {

    private val dao = BlackListerApp.instance?.database?.callLogDao()

    override suspend fun insertCallLogs(callLogList: List<CallLog>) {
        dao?.insertAllCallLogs(callLogList)
    }

    override suspend fun getAllCallLogs(): List<CallLog>? {
        return dao?.allCallLogs()
    }

    override suspend fun updateCallLog(callLog: CallLog) {
        dao?.updateCallLog(callLog)
    }

    override suspend fun deleteAllCallLogs() {
        dao?.deleteAllCallLogs()
    }

    override suspend fun getHashMapFromCallLogList(callLogList: List<CallLog>): HashMap<String, List<CallLog>> =
        withContext(
            Dispatchers.Default
        ) {
            callLogList.toHashMapFromList()
        }

}