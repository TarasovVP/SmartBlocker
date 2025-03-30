package com.tarasovvp.smartblocker.domain.repository

import android.content.Context
import com.tarasovvp.smartblocker.domain.entities.dbentities.LogCall
import com.tarasovvp.smartblocker.domain.entities.dbviews.CallWithFilter

interface LogCallRepository {
    suspend fun getSystemLogCallList(
        context: Context,
        country: String,
        result: (Int, Int) -> Unit,
    ): List<LogCall>

    suspend fun insertAllLogCalls(logCallList: List<LogCall>)

    suspend fun allCallWithFilters(): List<CallWithFilter>

    suspend fun allCallWithFiltersByFilter(filter: String): List<CallWithFilter>
}
