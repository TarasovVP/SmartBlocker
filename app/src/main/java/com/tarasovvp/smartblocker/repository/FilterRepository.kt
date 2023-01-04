package com.tarasovvp.smartblocker.repository

import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.extensions.EMPTY
import com.tarasovvp.smartblocker.extensions.filteredFilterList
import com.tarasovvp.smartblocker.extensions.isTrue
import com.tarasovvp.smartblocker.models.Filter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object FilterRepository {

    private val realDataBaseRepository = RealDataBaseRepository
    private val filterDao = SmartBlockerApp.instance?.database?.filterDao()

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
        if (SmartBlockerApp.instance?.isLoggedInUser().isTrue()) {
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
        if (SmartBlockerApp.instance?.isLoggedInUser().isTrue()) {
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
        if (SmartBlockerApp.instance?.isLoggedInUser().isTrue()) {
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
        val filterList = filterDao?.allFilters()
        return filterList?.filteredFilterList(number)
    }

    suspend fun queryFilter(number: String): Filter? {
        val filterList = filterDao?.queryFullMatchFilterList(number)
        val maxLengthFilterList =
            filterList?.filter { filter -> filter.filter.length == filterList.maxByOrNull { it.filter.length }?.filter?.length }
        return if (maxLengthFilterList.orEmpty().size > 1) maxLengthFilterList.orEmpty()
            .minByOrNull {
                number.indexOf(it.filter)
            } else maxLengthFilterList.orEmpty().firstOrNull()
    }

}