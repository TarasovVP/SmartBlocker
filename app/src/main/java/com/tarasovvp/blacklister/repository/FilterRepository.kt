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
            filterList.groupBy { filter ->
                if (filter.addFilter().any { it.isLetter() || it.isDigit() }) filter.addFilter()
                    .filter { it.isLetter() || it.isDigit() }[0].toString() else String.EMPTY
            }
        }

    suspend fun insertAllFilters(filterList: ArrayList<Filter>) {
        filterDao?.deleteAllFilters()
        filterDao?.insertAllFilters(filterList)
    }

    suspend fun allFilters(): List<Filter>? {
        return filterDao?.allFilters()
    }

    suspend fun allFiltersByType(filterType: Int): List<Filter>? {
        return filterDao?.allFiltersByType(filterType)
    }

    suspend fun getFilter(filter: Filter): Filter? {
        return filterDao?.getFilter(filter.addFilter(), filter.conditionType)
    }

    fun updateFilter(filter: Filter, result: () -> Unit) {
        if (BlackListerApp.instance?.isLoggedInUser().isTrue()) {
            realDataBaseRepository.insertFilter(filter) {
                filterDao?.updateFilter(filter)
                result.invoke()
            }
        } else {
            filterDao?.updateFilter(filter)
            result.invoke()
        }
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

    suspend fun queryFilterList(number: String): List<Filter>? {
        return filterDao?.queryFilterList(number)?.sortedByDescending { it.filter.length }
    }

}