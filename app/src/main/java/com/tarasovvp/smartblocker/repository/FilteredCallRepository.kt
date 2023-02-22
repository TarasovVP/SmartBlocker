package com.tarasovvp.smartblocker.repository

import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.database.dao.FilteredCallDao
import com.tarasovvp.smartblocker.extensions.isTrue
import com.tarasovvp.smartblocker.models.Call
import com.tarasovvp.smartblocker.models.FilteredCall
import javax.inject.Inject

class FilteredCallRepository @Inject constructor(
    private val filteredCallDao: FilteredCallDao,
    private val realDataBaseRepository: RealDataBaseRepository
) {

    suspend fun insertAllFilteredCalls(filteredCallList: ArrayList<FilteredCall>) {
        filteredCallDao.deleteAllFilteredCalls()
        filteredCallDao.insertAllFilteredCalls(filteredCallList)
    }

    fun insertFilteredCall(filteredCall: FilteredCall) {
        if (SmartBlockerApp.instance?.isLoggedInUser().isTrue()) {
            realDataBaseRepository.insertFilteredCall(filteredCall) {
                filteredCallDao.insertFilteredCall(filteredCall)
            }
        } else {
            filteredCallDao.insertFilteredCall(filteredCall)
        }
    }

    suspend fun allFilteredCalls(): List<FilteredCall> {
        return filteredCallDao.allFilteredCalls()
    }

    suspend fun filteredCallsByFilter(filter: String): List<FilteredCall> {
        return filteredCallDao.filteredCallsByFilter(filter)
    }

    suspend fun filteredCallsByNumber(number: String): List<FilteredCall> {
        return filteredCallDao.filteredCallsByNumber(number)
    }

    fun deleteFilteredCalls(filteredCallList: List<Call>, result: () -> Unit) {
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