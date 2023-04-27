package com.tarasovvp.smartblocker.data.repositoryImpl

import com.tarasovvp.smartblocker.data.database.dao.FilteredCallDao
import com.tarasovvp.smartblocker.domain.models.database_views.FilteredCallWithFilter
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.domain.models.entities.FilteredCall
import com.tarasovvp.smartblocker.domain.repository.FilteredCallRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FilteredCallRepositoryImpl @Inject constructor(
    private val filteredCallDao: FilteredCallDao
) : FilteredCallRepository {

    override suspend fun setFilterToFilteredCall(filterList: List<Filter>, callList: List<FilteredCall>, result: (Int, Int) -> Unit): List<FilteredCall> =
        withContext(Dispatchers.Default) {
            callList.onEachIndexed { index, filteredCall ->
                filteredCall.filter = filterList.filter { filter ->
                    (filteredCall.number == filter.filter && filter.isTypeFull())
                            || (filteredCall.number.startsWith(filter.filter) && filter.isTypeStart())
                            || (filteredCall.number.contains(filter.filter) && filter.isTypeContain())
                }.sortedWith(compareByDescending<Filter> { it.filter.length }.thenBy { filteredCall.number.indexOf(it.filter) })
                    .firstOrNull()?.filter.orEmpty()
                result.invoke(callList.size, index)
            }
        }

    override suspend fun insertAllFilteredCalls(filteredCallList: List<FilteredCall>) {
        filteredCallDao.deleteAllFilteredCalls()
        filteredCallDao.insertAllFilteredCalls(filteredCallList)
    }

    override suspend fun insertFilteredCall(filteredCall: FilteredCall) {
        filteredCallDao.insertFilteredCall(filteredCall)
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

    override suspend fun deleteFilteredCalls(filteredCallIdList: List<Int>) {
        filteredCallDao.deleteFilteredCalls(filteredCallIdList)
    }

}