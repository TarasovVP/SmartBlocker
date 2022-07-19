package com.tarasovvp.blacklister.repository

import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.toHashMapFromList
import com.tarasovvp.blacklister.model.Number
import com.tarasovvp.blacklister.model.WhiteNumber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object WhiteNumberRepository {

    private val dao = BlackListerApp.instance?.database?.whiteNumberDao()
    private val realDataBaseRepository = RealDataBaseRepository

    fun insertAllWhiteNumbers(whiteNumberList: ArrayList<WhiteNumber>) {
        dao?.deleteAllWhiteNumbers()
        dao?.insertAllWhiteNumbers(whiteNumberList)
    }

    fun allWhiteNumbers(): List<WhiteNumber>? {
        return dao?.getAllWhiteNumbers()
    }

    fun insertWhiteNumber(whiteNumber: WhiteNumber, result: () -> Unit) {
        if (BlackListerApp.instance?.isLoggedInUser().isTrue()) {
            realDataBaseRepository.insertWhiteNumber(whiteNumber) {
                dao?.insertWhiteNumber(whiteNumber)
                result.invoke()
            }
        } else {
            dao?.insertWhiteNumber(whiteNumber)
            result.invoke()
        }
    }

    fun deleteWhiteNumber(whiteNumber: WhiteNumber, result: () -> Unit) {
        if (BlackListerApp.instance?.isLoggedInUser().isTrue()) {
            realDataBaseRepository.deleteWhiteNumber(whiteNumber) {
                dao?.delete(whiteNumber)
                result.invoke()
            }
        } else {
            dao?.delete(whiteNumber)
            result.invoke()
        }
    }

    fun deleteWhiteNumberList(whiteNumberList: List<WhiteNumber>, result: () -> Unit) {
        if (BlackListerApp.instance?.isLoggedInUser().isTrue()) {
            realDataBaseRepository.deleteWhiteNumberList(whiteNumberList) {
                whiteNumberList.forEach { whiteNumber ->
                    dao?.delete(whiteNumber)
                }
                result.invoke()
            }
        } else {
            whiteNumberList.forEach { whiteNumber ->
                dao?.delete(whiteNumber)
            }
            result.invoke()
        }
    }

    suspend fun getHashMapFromWhiteNumberList(whiteNumberList: List<WhiteNumber>): HashMap<String, List<WhiteNumber>> =
        withContext(
            Dispatchers.Default
        ) {
            whiteNumberList.toHashMapFromList()
        }

    suspend fun getHashMapFromNumberList(whiteNumberList: List<Number>): HashMap<String, List<Number>> =
        withContext(
            Dispatchers.Default
        ) {
            whiteNumberList.toHashMapFromList()
        }

    fun getWhiteNumberList(whiteNumber: String): List<WhiteNumber>? {
        return dao?.getWhiteNumberList(whiteNumber)
    }
}