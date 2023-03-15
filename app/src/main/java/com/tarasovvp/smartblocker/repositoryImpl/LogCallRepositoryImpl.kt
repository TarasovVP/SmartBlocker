package com.tarasovvp.smartblocker.repositoryImpl

import android.content.Context
import com.tarasovvp.smartblocker.database.dao.LogCallDao
import com.tarasovvp.smartblocker.database.database_views.LogCallWithFilter
import com.tarasovvp.smartblocker.database.entities.CallWithFilter
import com.tarasovvp.smartblocker.database.entities.Filter
import com.tarasovvp.smartblocker.database.entities.LogCall
import com.tarasovvp.smartblocker.extensions.systemLogCallList
import com.tarasovvp.smartblocker.repository.LogCallRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LogCallRepositoryImpl @Inject constructor(private val callDao: LogCallDao) : LogCallRepository {

    override suspend fun setFilterToLogCall(filterList: ArrayList<Filter>?, callList: List<LogCall>, result: (Int, Int) -> Unit): List<LogCall> =
        withContext(
            Dispatchers.Default
        ) {
            callList.onEachIndexed { index, logCall ->
                logCall.filter = filterList?.filter { filter ->
                    (logCall.phoneNumberValue() == filter.filter && filter.isTypeFull())
                            || (logCall.phoneNumberValue().startsWith(filter.filter) && filter.isTypeStart())
                            || (logCall.phoneNumberValue().contains(filter.filter) && filter.isTypeContain())
                }?.sortedWith(compareByDescending<Filter> { it.filter.length }.thenBy { logCall.phoneNumberValue().indexOf(it.filter) })?.firstOrNull()?.filter.orEmpty()
                result.invoke(callList.size, index)
            }
        }

    override suspend fun insertAllLogCalls(logCallList: List<LogCall>) {
        callDao.insertAllLogCalls(logCallList)
    }

    override suspend fun getAllLogCallWithFilter(): List<LogCallWithFilter> =
        withContext(
            Dispatchers.Default
        ) {
            callDao.allLogCallWithFilter()
        }

    override suspend fun allCallNumberWithFilter(): List<LogCallWithFilter> {
        return callDao.allCallNumberWithFilter().distinctBy { it.call?.number }
    }

    override suspend fun getLogCallWithFilterByFilter(filter: String) = callDao.queryCallList(filter).distinctBy { it.call?.number }


    override suspend fun getSystemLogCallList(context: Context, result: (Int, Int) -> Unit): ArrayList<LogCall> =
        withContext(
            Dispatchers.Default
        ) {
            context.systemLogCallList { size, position ->
                result.invoke(size, position)
            }
        }

    override suspend fun getHashMapFromCallList(logCallList: List<CallWithFilter>): Map<String, List<CallWithFilter>> =
        withContext(
            Dispatchers.Default
        ) {
            logCallList.groupBy { it.call?.dateFromCallDate().toString() }
        }
}