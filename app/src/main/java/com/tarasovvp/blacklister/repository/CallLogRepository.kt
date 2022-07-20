package com.tarasovvp.blacklister.repository

import android.content.Context
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.extensions.callLogList
import com.tarasovvp.blacklister.extensions.toHashMapFromList
import com.tarasovvp.blacklister.model.CallLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CallLogRepository {

    private val dao = BlackListerApp.instance?.database?.callLogDao()

    fun insertCallLogs(callLogList: List<CallLog>) {
        dao?.insertAllCallLogs(callLogList)
    }

    fun getAllCallLogs(): List<CallLog>? {
        return dao?.allCallLogs()
    }

    suspend fun getSystemCallLogList(context: Context): ArrayList<CallLog> =
        withContext(
            Dispatchers.Default
        ) {
            context.callLogList()
        }


    suspend fun getHashMapFromCallLogList(callLogList: List<CallLog>): HashMap<String, List<CallLog>> =
        withContext(
            Dispatchers.Default
        ) {
            callLogList.toHashMapFromList()
        }

}