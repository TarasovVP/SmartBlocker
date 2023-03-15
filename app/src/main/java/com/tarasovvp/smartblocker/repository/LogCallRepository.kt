package com.tarasovvp.smartblocker.repository

import android.content.Context
import com.tarasovvp.smartblocker.database.database_views.LogCallWithFilter
import com.tarasovvp.smartblocker.database.entities.CallWithFilter
import com.tarasovvp.smartblocker.database.entities.LogCall

interface LogCallRepository {

    suspend fun insertAllLogCalls(logCallList: List<LogCall>)

    suspend fun getAllLogCallWithFilter(): List<LogCallWithFilter>

    suspend fun getLogCallWithFilterByFilter(filter: String): List<LogCallWithFilter>

    suspend fun allCallNumberWithFilter(): List<LogCallWithFilter>

    suspend fun getSystemLogCallList(context: Context, result: (Int, Int) -> Unit) : ArrayList<LogCall>

    suspend fun getHashMapFromCallList(logCallList: List<CallWithFilter>): Map<String, List<CallWithFilter>>
}