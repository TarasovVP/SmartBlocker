package com.tarasovvp.blacklister.repository

import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.extensions.EMPTY
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.model.Filter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object FilterRepository {

    private val realDataBaseRepository = RealDataBaseRepository
    private val filterDao = BlackListerApp.instance?.database?.filterDao()

    suspend fun getHashMapFromFilterList(filterList: List<Filter>): Map<String, List<Filter>> =
        withContext(Dispatchers.Default) {
            filterList.groupBy { if (it.filter.isNotEmpty()) it.filter[0].toString() else String.EMPTY }
        }

    fun insertAllFilters(filterList: ArrayList<Filter>) {
        filterDao?.deleteAllFilters()
        filterDao?.insertAllFilters(filterList)
    }

    fun allFilters(filterType: Int): List<Filter>? {
        return filterDao?.allFilters(filterType)
    }

    fun getFilter(filter: Filter): Filter? {
        return filterDao?.getFilter(filter.filter, filter.conditionType, filter.filterType)
    }

    fun insertFilter(filter: Filter, result: () -> Unit) {
        if (BlackListerApp.instance?.isLoggedInUser().isTrue()) {
            realDataBaseRepository.insertFilter(filter) {
                filterDao?.insertFilter(filter)
                result.invoke()
            }
        } else {
            filterDao?.insertFilter(filter)
            result.invoke()
        }
    }

    fun deleteWhiteFilter(filter: Filter, result: () -> Unit) {
        if (BlackListerApp.instance?.isLoggedInUser().isTrue()) {
            realDataBaseRepository.deleteFilter(filter) {
                filterDao?.delete(filter)
                result.invoke()
            }
        } else {
            filterDao?.delete(filter)
            result.invoke()
        }
    }

    fun deleteFilterList(filterList: List<Filter>, result: () -> Unit) {
        if (BlackListerApp.instance?.isLoggedInUser().isTrue()) {
            realDataBaseRepository.deleteFilterList(filterList) {
                filterList.forEach { filter ->
                    filterDao?.delete(filter)
                }
                result.invoke()
            }
        } else {
            filterList.forEach { whiteFilter ->
                filterDao?.delete(whiteFilter)
            }
            result.invoke()
        }
    }

    fun getFilterList(phone: String): List<Filter>? {
        return filterDao?.queryFilterList(phone)
    }

}