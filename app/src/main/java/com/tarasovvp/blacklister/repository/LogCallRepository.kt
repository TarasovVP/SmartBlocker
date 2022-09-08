package com.tarasovvp.blacklister.repository

import android.content.Context
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.extensions.systemLogCallList
import com.tarasovvp.blacklister.model.Call
import com.tarasovvp.blacklister.model.LogCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object LogCallRepository {

    private val dao = BlackListerApp.instance?.database?.logCallDao()

    fun insertAllLogCalls(logCallList: List<LogCall>) {
        dao?.insertAllLogCalls(logCallList)
    }

    suspend fun getAllLogCalls(): List<LogCall>? =
        withContext(
            Dispatchers.Default
        ) {
            dao?.allLogCalls()
        }

    suspend fun getSystemLogCallList(context: Context): ArrayList<LogCall> =
        withContext(
            Dispatchers.Default
        ) {
            context.systemLogCallList()
        }


    suspend fun getHashMapFromCallList(logCallList: List<Call>): Map<String, List<Call>> =
        withContext(
            Dispatchers.Default
        ) {
            logCallList.groupBy { it.dateFromTime().toString() }
        }

}