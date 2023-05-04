package com.tarasovvp.smartblocker.domain.repository

import android.content.Context
import com.tarasovvp.smartblocker.domain.models.database_views.LogCallWithFilter
import com.tarasovvp.smartblocker.domain.models.entities.LogCall

interface LogCallRepository {

    suspend fun getSystemLogCallList(context: Context, result: (Int, Int) -> Unit) : List<LogCall>

    suspend fun insertAllLogCalls(logCallList: List<LogCall>)

    suspend fun getAllLogCallWithFilter(): List<LogCallWithFilter>

    suspend fun getLogCallWithFilterByFilter(filter: String): List<LogCallWithFilter>

    suspend fun allCallNumberWithFilter(): List<LogCallWithFilter>

}