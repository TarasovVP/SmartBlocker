package com.tarasovvp.smartblocker.repositoryImpl

import android.content.Context
import com.tarasovvp.smartblocker.database.dao.LogCallDao
import com.tarasovvp.smartblocker.extensions.systemLogCallList
import com.tarasovvp.smartblocker.models.*
import com.tarasovvp.smartblocker.repository.LogCallRepository
import com.tarasovvp.smartblocker.repository.FilterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LogCallRepositoryImpl @Inject constructor(private val callDao: LogCallDao, private val filterRepository: FilterRepository) : LogCallRepository {

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
            context.systemLogCallList(filterRepository) { size, position ->
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