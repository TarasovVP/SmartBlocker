package com.tarasovvp.blacklister.repository

import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.model.Filter
import com.tarasovvp.blacklister.model.WhiteFilter

object WhiteFilterRepository : FilterRepository() {

    private val whiteFilterDao = BlackListerApp.instance?.database?.whiteFilterDao()

    fun insertAllWhiteFilters(whiteFilterList: ArrayList<WhiteFilter>) {
        whiteFilterDao?.deleteAllWhiteFilters()
        whiteFilterDao?.insertAllWhiteFilters(whiteFilterList)
    }

    fun allWhiteFilters(): List<WhiteFilter>? {
        return whiteFilterDao?.allWhiteFilters()
    }

    fun getWhiteFilter(filter: Filter): WhiteFilter? {
        return whiteFilterDao?.getWhiteFilter(filter.filter, filter.type)
    }

    fun insertWhiteFilter(whiteFilter: WhiteFilter, result: () -> Unit) {
        if (BlackListerApp.instance?.isLoggedInUser().isTrue()) {
            realDataBaseRepository.insertWhiteFilter(whiteFilter) {
                whiteFilterDao?.insertWhiteFilter(whiteFilter)
                result.invoke()
            }
        } else {
            whiteFilterDao?.insertWhiteFilter(whiteFilter)
            result.invoke()
        }
    }

    fun deleteWhiteFilter(whiteFilter: WhiteFilter, result: () -> Unit) {
        if (BlackListerApp.instance?.isLoggedInUser().isTrue()) {
            realDataBaseRepository.deleteWhiteFilter(whiteFilter) {
                whiteFilterDao?.delete(whiteFilter)
                result.invoke()
            }
        } else {
            whiteFilterDao?.delete(whiteFilter)
            result.invoke()
        }
    }

    fun deleteWhiteFilterList(whiteFilterList: List<WhiteFilter>, result: () -> Unit) {
        if (BlackListerApp.instance?.isLoggedInUser().isTrue()) {
            realDataBaseRepository.deleteWhiteFilterList(whiteFilterList) {
                whiteFilterList.forEach { whiteFilter ->
                    whiteFilterDao?.delete(whiteFilter)
                }
                result.invoke()
            }
        } else {
            whiteFilterList.forEach { whiteFilter ->
                whiteFilterDao?.delete(whiteFilter)
            }
            result.invoke()
        }
    }

    fun getWhiteFilterList(phone: String): List<WhiteFilter>? {
        return whiteFilterDao?.queryWhiteFilterList(phone)
    }
}