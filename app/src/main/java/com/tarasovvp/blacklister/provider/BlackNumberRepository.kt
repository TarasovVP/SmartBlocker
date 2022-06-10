package com.tarasovvp.blacklister.provider

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import com.google.gson.Gson
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.constants.Constants
import com.tarasovvp.blacklister.extensions.toHashMapFromList
import com.tarasovvp.blacklister.model.BlackNumber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface BlackNumberRepository {
    suspend fun insertAllBlackNumbers()
    suspend fun allBlackNumbers(): List<BlackNumber>?
    suspend fun getBlackNumber(blackNumber: String): BlackNumber?
    suspend fun insertBlackNumber(blackNumber: BlackNumber)
    suspend fun deleteBlackNumber(blackNumber: BlackNumber)
    suspend fun getHashMapFromBlackNumberList(blackNumberList: List<BlackNumber>): HashMap<String, List<BlackNumber>>
}

object BlackNumberRepositoryImpl : BlackNumberRepository {

    private val dao = BlackListerApp.instance?.database?.blackNumberDao()
    val database = FirebaseDatabase.getInstance(Constants.REALTIME_DATABASE)

    override suspend fun insertAllBlackNumbers() {
        dao?.deleteAllBlackNumbers()
        database.reference.child(FirebaseAuth.getInstance().currentUser?.uid.orEmpty()).get().addOnSuccessListener { snapshot ->
            val blackNumberList = snapshot.getValue<List<BlackNumber>>()
            Log.e("firebase", "Got value ${snapshot.value} blackNumberList ${Gson().toJson(blackNumberList)}")
            blackNumberList?.apply {
                Log.e("firebase", "insertAllBlackNumbers this ${Gson().toJson(this)}")
                dao?.insertAllBlackNumbers(this)
            }
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }

    override suspend fun allBlackNumbers(): List<BlackNumber>? {
        return dao?.getAllBlackNumbers()
    }

    override suspend fun getBlackNumber(blackNumber: String): BlackNumber? {
        return dao?.getBlackNumber(blackNumber)
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