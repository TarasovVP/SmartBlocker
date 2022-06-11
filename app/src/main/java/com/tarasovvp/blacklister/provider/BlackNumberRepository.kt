package com.tarasovvp.blacklister.provider

import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.gson.Gson
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.constants.Constants
import com.tarasovvp.blacklister.constants.Constants.USERS
import com.tarasovvp.blacklister.extensions.toHashMapFromList
import com.tarasovvp.blacklister.model.BlackNumber
import com.tarasovvp.blacklister.model.CallLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface BlackNumberRepository {
    suspend fun insertAllBlackNumbers()
    suspend fun blackNumbersRemoteCount(blackNumber: String, result: (Long) -> Unit)
    suspend fun allBlackNumbers(): List<BlackNumber>?
    suspend fun getBlackNumber(blackNumber: String): BlackNumber?
    suspend fun insertBlackNumber(blackNumber: BlackNumber, result: () -> Unit)
    suspend fun deleteBlackNumber(blackNumber: BlackNumber, result: () -> Unit)
    suspend fun getHashMapFromBlackNumberList(blackNumberList: List<BlackNumber>): HashMap<String, List<BlackNumber>>
}

object BlackNumberRepositoryImpl : BlackNumberRepository {

    private val dao = BlackListerApp.instance?.database?.blackNumberDao()
    val database = FirebaseDatabase.getInstance(Constants.REALTIME_DATABASE).reference

    override suspend fun insertAllBlackNumbers() {
        dao?.deleteAllBlackNumbers()
        database.child(USERS).child(FirebaseAuth.getInstance().currentUser?.uid.orEmpty()).get().addOnSuccessListener { snapshot ->
            val blackNumberList = arrayListOf<BlackNumber>()
            snapshot.children.forEach {
                it.getValue<BlackNumber>()?.let { blackNumber ->
                    blackNumberList.add(blackNumber)
                }
            }
            Log.e("firebase", "Got value ${snapshot.value} blackNumberList ${Gson().toJson(blackNumberList)}")
            blackNumberList.apply {
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

    override suspend fun blackNumbersRemoteCount(blackNumber: String, result: (Long) -> Unit) {
        val test = database.child(USERS).orderByChild(blackNumber).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                result.invoke(snapshot.childrenCount)
                Log.e("firebase", "blackNumbersRemoteCount childrenCount ${Gson().toJson(snapshot.childrenCount)}")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("firebase", "blackNumbersRemoteCount error ${Gson().toJson(error)}")
            }
        })
        Log.e("firebase", "blackNumbersRemoteCount test ${Gson().toJson(test)}")
    }

    override suspend fun getBlackNumber(blackNumber: String): BlackNumber? {
        return dao?.getBlackNumber(blackNumber)
    }

    override suspend fun insertBlackNumber(blackNumber: BlackNumber, result: () -> Unit) {
        database.child(USERS).child(FirebaseAuth.getInstance().currentUser?.uid.orEmpty()).child(blackNumber.blackNumber).setValue(blackNumber).addOnCompleteListener {
            dao?.insertBlackNumber(blackNumber)
            result.invoke()
        }
    }

    override suspend fun deleteBlackNumber(blackNumber: BlackNumber, result: () -> Unit) {
        database.child(USERS).child(FirebaseAuth.getInstance().currentUser?.uid.orEmpty()).child(blackNumber.blackNumber).removeValue().addOnCompleteListener {
            dao?.delete(blackNumber)
            result.invoke()
        }
    }

    override suspend fun getHashMapFromBlackNumberList(blackNumberList: List<BlackNumber>): HashMap<String, List<BlackNumber>> =
        withContext(
            Dispatchers.Default
        ) {
            blackNumberList.toHashMapFromList()
        }
}