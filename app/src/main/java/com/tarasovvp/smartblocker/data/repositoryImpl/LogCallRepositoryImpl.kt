package com.tarasovvp.smartblocker.data.repositoryImpl

import android.content.Context
import com.tarasovvp.smartblocker.data.database.dao.LogCallDao
import com.tarasovvp.smartblocker.domain.entities.db_entities.LogCall
import com.tarasovvp.smartblocker.domain.entities.db_views.CallWithFilter
import com.tarasovvp.smartblocker.utils.extensions.systemLogCallList
import com.tarasovvp.smartblocker.domain.repository.LogCallRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LogCallRepositoryImpl @Inject constructor(private val logCallDao: LogCallDao) :
    LogCallRepository {

    override suspend fun getSystemLogCallList(context: Context, result: (Int, Int) -> Unit): List<LogCall> =
        withContext(Dispatchers.Default) {
            context.systemLogCallList { size, position ->
                result.invoke(size, position)
            }
        }

    override suspend fun insertAllLogCalls(logCallList: List<LogCall>) =
        logCallDao.insertAllLogCalls(logCallList)

    override suspend fun allCallWithFilters(): List<CallWithFilter> =
        logCallDao.allCallWithFilters()

    override suspend fun allDistinctCallsWithFilter() =
        logCallDao.allDistinctCallsWithFilter()

    override suspend fun allCallWithFiltersByFilter(filter: String) =
        logCallDao.allCallWithFiltersByFilter(filter)
}