package com.tarasovvp.blacklister.repository

import android.content.Context
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.extensions.systemLogCallList
import com.tarasovvp.blacklister.model.Call
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.model.Filter
import com.tarasovvp.blacklister.model.LogCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CallRepository {

    private val callDao = BlackListerApp.instance?.database?.logCallDao()

    suspend fun insertAllLogCalls(logCallList: List<LogCall>) {
        callDao?.insertAllLogCalls(logCallList)
    }

    suspend fun getAllLogCalls(): List<LogCall>? =
        withContext(
            Dispatchers.Default
        ) {
            callDao?.allLogCalls()
        }

    suspend fun getAllCallsNumbers(): List<LogCall>? {
        return callDao?.allNumbersNotFromContacts()?.distinctBy { it.number }
    }

    suspend fun getQueryCallList(filter: Filter): List<LogCall>? {
        return callDao?.queryCallList(filter.filter, filter.conditionType)?.distinctBy { it.number }
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