package com.tarasovvp.smartblocker.data.repositoryImpl

import com.tarasovvp.smartblocker.data.database.dao.FilteredCallDao
import com.tarasovvp.smartblocker.domain.entities.db_entities.FilteredCall
import com.tarasovvp.smartblocker.domain.entities.db_views.CallWithFilter
import com.tarasovvp.smartblocker.domain.repository.FilteredCallRepository
import javax.inject.Inject

class FilteredCallRepositoryImpl @Inject constructor(
    private val filteredCallDao: FilteredCallDao
) : FilteredCallRepository {

    override suspend fun insertAllFilteredCalls(filteredCallList: List<FilteredCall>) =
        filteredCallDao.insertAllFilteredCalls(filteredCallList)

    override suspend fun insertFilteredCall(filteredCall: FilteredCall) =
        filteredCallDao.insertFilteredCall(filteredCall)

    override suspend fun allFilteredCalls(): List<FilteredCall> =
        filteredCallDao.allFilteredCalls()

    override suspend fun filteredCallsByFilter(filter: String): List<CallWithFilter> =
        filteredCallDao.filteredCallsByFilter(filter)

    override suspend fun filteredCallsByNumber(number: String): List<CallWithFilter> =
        filteredCallDao.filteredCallsByNumber(number)

    override suspend fun deleteFilteredCalls(filteredCallIdList: List<Int>) =
        filteredCallDao.deleteFilteredCalls(filteredCallIdList)
}