package com.tarasovvp.smartblocker.repository

import android.content.Context
import com.tarasovvp.smartblocker.models.*

interface LogCallRepository {

    suspend fun insertAllLogCalls(logCallList: List<LogCall>)

    suspend fun getAllLogCallWithFilter(): List<LogCallWithFilter>

    suspend fun allCallNumberWithFilter(): List<LogCallWithFilter>

    suspend fun getQueryCallList(filter: String): List<LogCallWithFilter>

    suspend fun getSystemLogCallList(context: Context, result: (Int, Int) -> Unit) : ArrayList<LogCall>

    suspend fun getHashMapFromCallList(logCallList: List<CallWithFilter>): Map<String, List<CallWithFilter>>
}