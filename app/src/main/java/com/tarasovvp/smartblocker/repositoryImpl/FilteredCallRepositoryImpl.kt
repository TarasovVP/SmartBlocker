package com.tarasovvp.smartblocker.repositoryImpl

import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.database.dao.FilteredCallDao
import com.tarasovvp.smartblocker.extensions.isTrue
import com.tarasovvp.smartblocker.database.entities.FilteredCall
import com.tarasovvp.smartblocker.database.database_views.FilteredCallWithFilter
import com.tarasovvp.smartblocker.repository.FilteredCallRepository
import com.tarasovvp.smartblocker.repository.RealDataBaseRepository
import javax.inject.Inject

class FilteredCallRepositoryImpl @Inject constructor(
    private val filteredCallDao: FilteredCallDao,
    private val realDataBaseRepository: RealDataBaseRepository
) : FilteredCallRepository {

    override suspend fun insertAllFilteredCalls(filteredCallList: ArrayList<FilteredCall>) {
        filteredCallDao.deleteAllFilteredCalls()
        filteredCallDao.insertAllFilteredCalls(filteredCallList)
    }

    override fun insertFilteredCall(filteredCall: FilteredCall) {
        if (SmartBlockerApp.instance?.isLoggedInUser().isTrue()) {
            realDataBaseRepository.insertFilteredCall(filteredCall) {
                filteredCallDao.insertFilteredCall(filteredCall)
            }
        } else {
            filteredCallDao.insertFilteredCall(filteredCall)
        }
    }

    override suspend fun allFilteredCalls(): List<FilteredCall> {
        return filteredCallDao.allFilteredCalls()
    }

    override suspend fun allFilteredCallWithFilter(): List<FilteredCallWithFilter> {
        return filteredCallDao.allFilteredCallWithFilter()
    }

    override suspend fun filteredCallsByFilter(filter: String): List<FilteredCallWithFilter> {
        return filteredCallDao.filteredCallsByFilter(filter)
    }

    override suspend fun filteredCallsByNumber(number: String): List<FilteredCallWithFilter> {
        return filteredCallDao.filteredCallsByNumber(number)
    }

    override fun deleteFilteredCalls(filteredCallIdList: List<Int>, result: () -> Unit) {
        if (SmartBlockerApp.instance?.isLoggedInUser().isTrue()) {
            realDataBaseRepository.deleteFilteredCallList(filteredCallIdList.map { it.toString() }) {
                filteredCallDao.deleteFilteredCalls(filteredCallIdList)
                result.invoke()
            }
        } else {
            filteredCallDao.deleteFilteredCalls(filteredCallIdList)
            result.invoke()
        }
    }

}