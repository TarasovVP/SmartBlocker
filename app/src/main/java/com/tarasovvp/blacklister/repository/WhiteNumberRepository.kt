package com.tarasovvp.blacklister.repository

import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.toHashMapFromList
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

    suspend fun insertWhiteNumber(whiteNumber: WhiteNumber, result: () -> Unit) {
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

    suspend fun deleteWhiteNumber(whiteNumber: WhiteNumber, result: () -> Unit) {
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

    suspend fun getHashMapFromWhiteNumberList(whiteNumberList: List<WhiteNumber>): HashMap<String, List<WhiteNumber>> =
        withContext(
            Dispatchers.Default
        ) {
            whiteNumberList.toHashMapFromList()
        }

    fun getWhiteNumberList(whiteNumber: String): List<WhiteNumber>? {
        return dao?.getWhiteNumberList(whiteNumber)
    }
}