package com.tarasovvp.smartblocker.domain.repository

import android.content.Context
import com.tarasovvp.smartblocker.domain.entities.db_entities.LogCall
import com.tarasovvp.smartblocker.domain.entities.db_views.CallWithFilter

interface LogCallRepository {

    suspend fun getSystemLogCallList(context: Context, result: (Int, Int) -> Unit) : List<LogCall>

    suspend fun insertAllLogCalls(logCallList: List<LogCall>)

    suspend fun getAllCallWithFilter(): List<CallWithFilter>

    suspend fun allCallWithFilterByFilter(filter: String): List<CallWithFilter>

    suspend fun allDistinctCallWithFilter(): List<CallWithFilter>

}