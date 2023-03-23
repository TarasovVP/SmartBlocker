package com.tarasovvp.smartblocker.domain.repository

import android.content.Context
import com.tarasovvp.smartblocker.data.database.database_views.LogCallWithFilter
import com.tarasovvp.smartblocker.data.database.entities.CallWithFilter
import com.tarasovvp.smartblocker.data.database.entities.Filter
import com.tarasovvp.smartblocker.data.database.entities.LogCall

interface LogCallRepository {

    suspend fun setFilterToLogCall(filterList: ArrayList<Filter>?, callList: List<LogCall>, result: (Int, Int) -> Unit): List<LogCall>

    suspend fun insertAllLogCalls(logCallList: List<LogCall>)

    suspend fun getAllLogCallWithFilter(): List<LogCallWithFilter>

    suspend fun getLogCallWithFilterByFilter(filter: String): List<LogCallWithFilter>

    suspend fun allCallNumberWithFilter(): List<LogCallWithFilter>

    suspend fun getSystemLogCallList(context: Context, result: (Int, Int) -> Unit) : ArrayList<LogCall>

    suspend fun getHashMapFromCallList(logCallList: List<CallWithFilter>): Map<String, List<CallWithFilter>>
}