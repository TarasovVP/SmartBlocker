package com.tarasovvp.smartblocker.repository.implementations

import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.database.dao.FilteredCallDao
import com.tarasovvp.smartblocker.extensions.isTrue
import com.tarasovvp.smartblocker.models.Call
import com.tarasovvp.smartblocker.models.FilteredCall
import com.tarasovvp.smartblocker.repository.interfaces.FilteredCallRepository
import com.tarasovvp.smartblocker.repository.interfaces.RealDataBaseRepository
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

    override suspend fun filteredCallsByFilter(filter: String): List<FilteredCall> {
        return filteredCallDao.filteredCallsByFilter(filter)
    }

    override suspend fun filteredCallsByNumber(number: String): List<FilteredCall> {
        return filteredCallDao.filteredCallsByNumber(number)
    }

    override fun deleteFilteredCalls(filteredCallList: List<Call>, result: () -> Unit) {
        if (SmartBlockerApp.instance?.isLoggedInUser().isTrue()) {
            realDataBaseRepository.deleteFilteredCallList(filteredCallList) {
                filteredCallDao.deleteFilteredCalls(filteredCallList.map { it.callId })
                result.invoke()
            }
        } else {
            filteredCallDao.deleteFilteredCalls(filteredCallList.map { it.callId })
            result.invoke()
        }
    }

}