package com.example.blacklister.provider

import androidx.lifecycle.LiveData
import com.example.blacklister.model.CallLog
import com.example.blacklister.BlackListerApp

interface CallLogRepository {
    suspend fun insertCallLogs(callLogList: List<CallLog>)
    fun subscribeToCallLogs(): LiveData<List<CallLog>>?
    suspend fun updateCallLog(callLog: CallLog)
    suspend fun deleteAllCallLogs()
}

object CallLogRepositoryImpl : CallLogRepository {

    private val dao = BlackListerApp.instance?.database?.callLogDao()

    override suspend fun insertCallLogs(callLogList: List<CallLog>) {
        dao?.insertAllCallLogs(callLogList)
    }

    override fun subscribeToCallLogs(): LiveData<List<CallLog>>? {
        return dao?.getAllCallLogs()
    }

    override suspend fun updateCallLog(callLog: CallLog) {
        dao?.updateCallLog(callLog)
    }

    override suspend fun deleteAllCallLogs() {
        dao?.deleteAllCallLogs()
    }

}