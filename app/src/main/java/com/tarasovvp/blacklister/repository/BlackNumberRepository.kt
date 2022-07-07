package com.tarasovvp.blacklister.repository

import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.toHashMapFromList
import com.tarasovvp.blacklister.model.BlackNumber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object BlackNumberRepository {

    private val blackNumberDao = BlackListerApp.instance?.database?.blackNumberDao()
    private val realDataBaseRepository = RealDataBaseRepository

    fun insertAllBlackNumbers(blackNumberList: ArrayList<BlackNumber>) {
        blackNumberDao?.deleteAllBlackNumbers()
        blackNumberDao?.insertAllBlackNumbers(blackNumberList)
    }

    fun allBlackNumbers(): List<BlackNumber>? {
        return blackNumberDao?.getAllBlackNumbers()
    }

    fun insertBlackNumber(blackNumber: BlackNumber, result: () -> Unit) {
        if (BlackListerApp.instance?.isLoggedInUser().isTrue()) {
            realDataBaseRepository.insertBlackNumber(blackNumber) {
                blackNumberDao?.insertBlackNumber(blackNumber)
                result.invoke()
            }
        } else {
            blackNumberDao?.insertBlackNumber(blackNumber)
            result.invoke()
        }
    }

    fun deleteBlackNumber(blackNumber: BlackNumber, result: () -> Unit) {
        if (BlackListerApp.instance?.isLoggedInUser().isTrue()) {
            realDataBaseRepository.deleteBlackNumber(blackNumber) {
                blackNumberDao?.delete(blackNumber)
                result.invoke()
            }
        } else {
            blackNumberDao?.delete(blackNumber)
            result.invoke()
        }
    }

    suspend fun getHashMapFromBlackNumberList(blackNumberList: List<BlackNumber>): HashMap<String, List<BlackNumber>> =
        withContext(
            Dispatchers.Default
        ) {
            blackNumberList.toHashMapFromList()
        }

    fun getBlackNumberList(blackNumber: String): List<BlackNumber>? {
        return blackNumberDao?.getBlackNumberList(blackNumber)
    }

}