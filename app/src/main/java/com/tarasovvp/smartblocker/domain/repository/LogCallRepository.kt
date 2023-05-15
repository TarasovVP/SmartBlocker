package com.tarasovvp.smartblocker.domain.repository

import android.content.Context
import com.tarasovvp.smartblocker.domain.entities.db_entities.LogCall
import com.tarasovvp.smartblocker.domain.entities.db_views.CallWithFilter

interface LogCallRepository {

    suspend fun getSystemLogCallList(context: Context, country: String, result: (Int, Int) -> Unit) : List<LogCall>

    suspend fun insertAllLogCalls(logCallList: List<LogCall>)

    suspend fun allCallWithFilters(): List<CallWithFilter>

    suspend fun allCallWithFiltersByFilter(filter: String): List<CallWithFilter>

    suspend fun allDistinctCallsWithFilter(): List<CallWithFilter>

}