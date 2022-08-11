package com.tarasovvp.blacklister.repository

import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.model.BlackFilter

object BlackFilterRepository: FilterRepository() {

    private val blackFilterDao = BlackListerApp.instance?.database?.blackFilterDao()

    fun insertAllBlackFilters(blackFilterList: ArrayList<BlackFilter>) {
        blackFilterDao?.deleteAllBlackFilters()
        blackFilterDao?.insertAllBlackFilters(blackFilterList)
    }

    fun allBlackFilters(): List<BlackFilter>? {
        return blackFilterDao?.allBlackFilters()
    }

    fun getBlackFilter(phone: String): BlackFilter? {
        return blackFilterDao?.getBlackFilter(phone)
    }

    fun insertBlackFilter(blackFilter: BlackFilter, result: () -> Unit) {
        if (BlackListerApp.instance?.isLoggedInUser().isTrue()) {
            realDataBaseRepository.insertBlackFilter(blackFilter) {
                blackFilterDao?.insertBlackFilter(blackFilter)
                result.invoke()
            }
        } else {
            blackFilterDao?.insertBlackFilter(blackFilter)
            result.invoke()
        }
    }

    fun deleteBlackFilter(blackFilter: BlackFilter, result: () -> Unit) {
        if (BlackListerApp.instance?.isLoggedInUser().isTrue()) {
            realDataBaseRepository.deleteBlackFilter(blackFilter) {
                blackFilterDao?.delete(blackFilter)
                result.invoke()
            }
        } else {
            blackFilterDao?.delete(blackFilter)
            result.invoke()
        }
    }

    fun deleteBlackFilterList(blackFilterList: List<BlackFilter>, result: () -> Unit) {
        if (BlackListerApp.instance?.isLoggedInUser().isTrue()) {
            realDataBaseRepository.deleteBlackFilterList(blackFilterList) {
                blackFilterList.forEach { blackFilter ->
                    blackFilterDao?.delete(blackFilter)
                }
                result.invoke()
            }
        } else {
            blackFilterList.forEach { blackFilter ->
                blackFilterDao?.delete(blackFilter)
            }
            result.invoke()
        }
    }

    fun getBlackFilterList(phone: String): List<BlackFilter>? {
        return blackFilterDao?.queryBlackFilterList(phone)
    }

}