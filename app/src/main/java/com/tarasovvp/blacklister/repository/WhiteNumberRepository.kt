package com.tarasovvp.blacklister.repository

import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.toHashMapFromList
import com.tarasovvp.blacklister.model.Number
import com.tarasovvp.blacklister.model.WhiteNumber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object WhiteNumberRepository {

    private val whiteNumberDao = BlackListerApp.instance?.database?.whiteNumberDao()
    private val realDataBaseRepository = RealDataBaseRepository

    fun insertAllWhiteNumbers(whiteNumberList: ArrayList<WhiteNumber>) {
        whiteNumberDao?.deleteAllWhiteNumbers()
        whiteNumberDao?.insertAllWhiteNumbers(whiteNumberList)
    }

    fun allWhiteNumbers(): List<WhiteNumber>? {
        return whiteNumberDao?.allWhiteNumbers()
    }

    fun getWhiteNumber(number: String): WhiteNumber? {
        return whiteNumberDao?.getWhiteNumber(number)
    }

    fun insertWhiteNumber(whiteNumber: WhiteNumber, result: () -> Unit) {
        if (BlackListerApp.instance?.isLoggedInUser().isTrue()) {
            realDataBaseRepository.insertWhiteNumber(whiteNumber) {
                whiteNumberDao?.insertWhiteNumber(whiteNumber)
                result.invoke()
            }
        } else {
            whiteNumberDao?.insertWhiteNumber(whiteNumber)
            result.invoke()
        }
    }

    fun deleteWhiteNumber(whiteNumber: WhiteNumber, result: () -> Unit) {
        if (BlackListerApp.instance?.isLoggedInUser().isTrue()) {
            realDataBaseRepository.deleteWhiteNumber(whiteNumber) {
                whiteNumberDao?.delete(whiteNumber)
                result.invoke()
            }
        } else {
            whiteNumberDao?.delete(whiteNumber)
            result.invoke()
        }
    }

    fun deleteWhiteNumberList(whiteNumberList: List<WhiteNumber>, result: () -> Unit) {
        if (BlackListerApp.instance?.isLoggedInUser().isTrue()) {
            realDataBaseRepository.deleteWhiteNumberList(whiteNumberList) {
                whiteNumberList.forEach { whiteNumber ->
                    whiteNumberDao?.delete(whiteNumber)
                }
                result.invoke()
            }
        } else {
            whiteNumberList.forEach { whiteNumber ->
                whiteNumberDao?.delete(whiteNumber)
            }
            result.invoke()
        }
    }

    suspend fun getHashMapFromNumberList(whiteNumberList: List<Number>): HashMap<String, List<Number>> =
        withContext(
            Dispatchers.Default
        ) {
            whiteNumberList.toHashMapFromList()
        }

    fun getWhiteNumberList(whiteNumber: String): List<WhiteNumber>? {
        return whiteNumberDao?.queryWhiteNumberList(whiteNumber)
    }
}