package com.tarasovvp.smartblocker.repositories

import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.data.database.dao.FilteredCallDao
import com.tarasovvp.smartblocker.domain.models.database_views.FilteredCallWithFilter
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.domain.models.entities.FilteredCall
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import com.tarasovvp.smartblocker.domain.repository.FilteredCallRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import javax.inject.Inject

@RunWith(MockitoJUnitRunner::class)
class FilteredCallRepositoryTest @Inject constructor(
    private val filteredCallDao: FilteredCallDao,
    private val realDataBaseRepository: RealDataBaseRepository
) {

    suspend fun setFilterToFilteredCall(filterList: List<Filter>, callList: List<FilteredCall>, result: (Int, Int) -> Unit): List<FilteredCall> =
        withContext(
            Dispatchers.Default
        ) {
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

    suspend fun insertAllFilteredCalls(filteredCallList: List<FilteredCall>) {
        filteredCallDao.deleteAllFilteredCalls()
        filteredCallDao.insertAllFilteredCalls(filteredCallList)
    }

    suspend fun insertFilteredCall(filteredCall: FilteredCall) {
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

    suspend fun allFilteredCallWithFilter(): List<FilteredCallWithFilter> {
        return filteredCallDao.allFilteredCallWithFilter()
    }

    suspend fun filteredCallsByFilter(filter: String): List<FilteredCallWithFilter> {
        return filteredCallDao.filteredCallsByFilter(filter)
    }

    suspend fun filteredCallsByNumber(number: String): List<FilteredCallWithFilter> {
        return filteredCallDao.filteredCallsByNumber(number)
    }

    suspend fun deleteFilteredCalls(filteredCallIdList: List<Int>, result: () -> Unit) {
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