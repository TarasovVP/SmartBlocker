package com.tarasovvp.smartblocker.data.repositoryImpl

import android.content.Context
import com.tarasovvp.smartblocker.data.database.dao.LogCallDao
import com.tarasovvp.smartblocker.domain.models.database_views.LogCallWithFilter
import com.tarasovvp.smartblocker.domain.models.entities.CallWithFilter
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.domain.models.entities.LogCall
import com.tarasovvp.smartblocker.utils.extensions.systemLogCallList
import com.tarasovvp.smartblocker.domain.repository.LogCallRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LogCallRepositoryImpl @Inject constructor(private val logCallDao: LogCallDao) :
    LogCallRepository {

    override suspend fun setFilterToLogCall(filterList: List<Filter>, callList: List<LogCall>, result: (Int, Int) -> Unit): List<LogCall> =
        withContext(
            Dispatchers.Default
        ) {
            callList.onEachIndexed { index, logCall ->
                logCall.filter = filterList.filter { filter ->
                    (logCall.phoneNumberValue() == filter.filter && filter.isTypeFull())
                            || (logCall.phoneNumberValue().startsWith(filter.filter) && filter.isTypeStart())
                            || (logCall.phoneNumberValue().contains(filter.filter) && filter.isTypeContain())
                }.sortedWith(compareByDescending<Filter> { it.filter.length }.thenBy { logCall.phoneNumberValue().indexOf(it.filter) })
                    .firstOrNull()?.filter.orEmpty()
                result.invoke(callList.size, index)
            }
        }

    override suspend fun insertAllLogCalls(logCallList: List<LogCall>) {
        logCallDao.insertAllLogCalls(logCallList)
    }

    override suspend fun getAllLogCallWithFilter(): List<LogCallWithFilter> =
        withContext(
            Dispatchers.Default
        ) {
            logCallDao.allLogCallWithFilter()
        }

    override suspend fun allCallNumberWithFilter(): List<LogCallWithFilter> {
        return logCallDao.allCallNumberWithFilter().distinctBy { it.call?.number }
    }

    override suspend fun getLogCallWithFilterByFilter(filter: String) = logCallDao.queryCallList(filter).distinctBy { it.call?.number }


    override suspend fun getSystemLogCallList(context: Context, result: (Int, Int) -> Unit): List<LogCall> =
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