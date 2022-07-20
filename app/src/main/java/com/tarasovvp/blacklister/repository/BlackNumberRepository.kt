package com.tarasovvp.blacklister.repository

import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.extensions.isNotNull
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.model.BlackNumber

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

    fun getBlackNumber(number: String): BlackNumber? {
        return blackNumberDao?.getBlackNumber(number)
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

    fun deleteBlackNumberList(blackNumberList: List<BlackNumber>, result: () -> Unit) {
        if (BlackListerApp.instance?.isLoggedInUser().isTrue()) {
            realDataBaseRepository.deleteBlackNumberList(blackNumberList) {
                blackNumberList.forEach { blackNumber ->
                    blackNumberDao?.delete(blackNumber)
                }
                result.invoke()
            }
        } else {
            blackNumberList.forEach { blackNumber ->
                blackNumberDao?.delete(blackNumber)
            }
            result.invoke()
        }
    }

    fun getBlackNumberList(blackNumber: String): List<BlackNumber>? {
        return blackNumberDao?.getBlackNumberList(blackNumber)
    }

}