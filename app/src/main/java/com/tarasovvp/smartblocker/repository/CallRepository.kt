package com.tarasovvp.smartblocker.repository

import android.content.Context
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.extensions.isNull
import com.tarasovvp.smartblocker.extensions.orZero
import com.tarasovvp.smartblocker.extensions.systemLogCallList
import com.tarasovvp.smartblocker.models.Call
import com.tarasovvp.smartblocker.models.Filter
import com.tarasovvp.smartblocker.models.LogCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CallRepository {

    private val callDao = SmartBlockerApp.instance?.database?.logCallDao()

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
        return callDao?.queryCallList(filter.filter,
            filter.conditionType)?.distinctBy { it.number }?.filter {
            it.filter.isNull() || it.filter == filter || (it.filter?.filter?.length.orZero() < (filter.filter).length
                    && it.number.indexOf(filter.addFilter()) < it.number.indexOf(it.filter?.filter.orEmpty()))
        }
    }

    suspend fun getSystemLogCallList(
        context: Context,
        result: (Int, Int) -> Unit,
    ): ArrayList<LogCall> =
        withContext(
            Dispatchers.Default
        ) {
            context.systemLogCallList { size, position ->
                result.invoke(size, position)
            }
        }

    suspend fun getHashMapFromCallList(logCallList: List<Call>): Map<String, List<Call>> =
        withContext(
            Dispatchers.Default
        ) {
            logCallList.groupBy { it.dateFromCallDate().toString() }
        }
}