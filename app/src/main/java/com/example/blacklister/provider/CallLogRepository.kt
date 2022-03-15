package com.example.blacklister.provider

import androidx.lifecycle.LiveData
import com.example.blacklister.model.CallLog
import com.example.blacklister.BlackListerApp

interface CallLogRepository {
    suspend fun insertCallLogs(callLogList: List<CallLog>)
    fun subscribeToCallLogs(): LiveData<List<CallLog>>?
    fun updateCallLog(callLog: CallLog)
}

object CallLogRepositoryImpl : CallLogRepository {

    private val dao = BlackListerApp.instance?.database?.callLogDao()

    override suspend fun insertCallLogs(callLogList: List<CallLog>) {
        dao?.insertAllCallLogs(callLogList)
    }

    override fun subscribeToCallLogs(): LiveData<List<CallLog>>? {
        return dao?.getAllCallLogs()
    }

    override fun updateCallLog(callLog: CallLog) {
        dao?.updateCallLog(callLog)
    }


}