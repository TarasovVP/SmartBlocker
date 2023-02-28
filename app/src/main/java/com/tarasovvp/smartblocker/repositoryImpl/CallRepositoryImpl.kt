package com.tarasovvp.smartblocker.repositoryImpl

import android.content.Context
import com.tarasovvp.smartblocker.database.dao.LogCallDao
import com.tarasovvp.smartblocker.extensions.isNull
import com.tarasovvp.smartblocker.extensions.orZero
import com.tarasovvp.smartblocker.extensions.systemLogCallList
import com.tarasovvp.smartblocker.models.Call
import com.tarasovvp.smartblocker.models.Filter
import com.tarasovvp.smartblocker.models.LogCall
import com.tarasovvp.smartblocker.repository.CallRepository
import com.tarasovvp.smartblocker.repository.FilterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CallRepositoryImpl @Inject constructor(private val callDao: LogCallDao, private val filterRepository: FilterRepository) : CallRepository {

    override suspend fun insertAllLogCalls(logCallList: List<LogCall>) {
        callDao.insertAllLogCalls(logCallList)
    }

    override suspend fun getAllLogCalls(): List<LogCall> =
        withContext(
            Dispatchers.Default
        ) {
            callDao.allLogCalls()
        }

    override suspend fun getAllCallsNumbers(): List<LogCall> {
        return callDao.allNumbersNotFromContacts().distinctBy { it.number }
    }

    override suspend fun getQueryCallList(filter: Filter): List<LogCall> {
        return callDao.queryCallList(filter.filter,
            filter.conditionType).distinctBy { it.number }.filter {
            it.filter.isNull() || it.filter == filter || (it.filter?.filter?.length.orZero() < (filter.filter).length
                    && it.number.indexOf(filter.createFilter()) < it.number.indexOf(it.filter?.filter.orEmpty()))
        }
    }

    override suspend fun getSystemLogCallList(context: Context, result: (Int, Int) -> Unit): ArrayList<LogCall> =
        withContext(
            Dispatchers.Default
        ) {
            context.systemLogCallList(filterRepository) { size, position ->
                result.invoke(size, position)
            }
        }

    override suspend fun getHashMapFromCallList(logCallList: List<Call>): Map<String, List<Call>> =
        withContext(
            Dispatchers.Default
        ) {
            logCallList.groupBy { it.dateFromCallDate().toString() }
        }
}