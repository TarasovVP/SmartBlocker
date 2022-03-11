package com.example.blacklister.provider

import androidx.lifecycle.LiveData
import com.example.blacklister.model.CallLog
import com.example.blacklister.ui.BlackListerApp

interface CallLogRepository {
    suspend fun inasertCallLogs(callLogList: List<CallLog>)
    fun subscribeToCallLogs(): LiveData<List<CallLog>>?
    fun updateCallLog(callLog: CallLog)
}

object CallLogRepositoryImpl : CallLogRepository {

    private val dao = BlackListerApp.instance?.database?.callLogDao()

    override suspend fun inasertCallLogs(callLogList: List<CallLog>) {
        dao?.insertAllCallLogs(callLogList)
    }

    override fun subscribeToCallLogs(): LiveData<List<CallLog>>? {
        return dao?.getAllCallLogs()
    }

    override fun updateCallLog(callLog: CallLog) {
        dao?.updateCallLog(callLog)
    }


}