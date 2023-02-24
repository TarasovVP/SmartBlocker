package com.tarasovvp.smartblocker.repository.interfaces

import android.content.Context

import com.tarasovvp.smartblocker.models.Call
import com.tarasovvp.smartblocker.models.Filter
import com.tarasovvp.smartblocker.models.LogCall

interface CallRepository {

    suspend fun insertAllLogCalls(logCallList: List<LogCall>)

    suspend fun getAllLogCalls(): List<LogCall>

    suspend fun getAllCallsNumbers(): List<LogCall>

    suspend fun getQueryCallList(filter: Filter): List<LogCall>

    suspend fun getSystemLogCallList(context: Context, result: (Int, Int) -> Unit) : ArrayList<LogCall>

    suspend fun getHashMapFromCallList(logCallList: List<Call>): Map<String, List<Call>>
}