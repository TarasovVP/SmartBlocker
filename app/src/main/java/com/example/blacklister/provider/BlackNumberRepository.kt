package com.example.blacklister.provider

import androidx.lifecycle.LiveData
import com.example.blacklister.model.BlackNumber
import com.example.blacklister.ui.BlackListerApp

interface BlackNumberRepository {
    suspend fun allBlackNumbers(): List<BlackNumber>?
    suspend fun insertBlackNumber(blackNumber: BlackNumber)
    suspend fun deleteBlackNumber(blackNumber: BlackNumber)
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

}