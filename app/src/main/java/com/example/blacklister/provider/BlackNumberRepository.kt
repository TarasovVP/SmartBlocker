package com.example.blacklister.provider

import com.example.blacklister.BlackListerApp
import com.example.blacklister.extensions.toHashMapFromList
import com.example.blacklister.model.BlackNumber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface BlackNumberRepository {
    suspend fun allBlackNumbers(): List<BlackNumber>?
    suspend fun insertBlackNumber(blackNumber: BlackNumber)
    suspend fun deleteBlackNumber(blackNumber: BlackNumber)
    suspend fun getHashMapFromBlackNumberList(blackNumberList: List<BlackNumber>): HashMap<String, List<BlackNumber>>
}

object BlackNumberRepositoryImpl : BlackNumberRepository {

    private val dao = BlackListerApp.instance?.database?.blackNumberDao()

    override suspend fun allBlackNumbers(): List<BlackNumber>? {
        return dao?.getAllBlackNumbers()
    }

    override suspend fun insertBlackNumber(blackNumber: BlackNumber) {
        dao?.insertBlackNumber(blackNumber)
    }

    override suspend fun deleteBlackNumber(blackNumber: BlackNumber) {
        dao?.delete(blackNumber)
    }

    override suspend fun getHashMapFromBlackNumberList(blackNumberList: List<BlackNumber>): HashMap<String, List<BlackNumber>> =
        withContext(
            Dispatchers.Default
        ) {
            blackNumberList.toHashMapFromList()
        }
}