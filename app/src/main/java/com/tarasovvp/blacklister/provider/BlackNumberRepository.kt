package com.tarasovvp.blacklister.provider

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.gson.Gson
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.constants.Constants
import com.tarasovvp.blacklister.constants.Constants.BLACK_NUMBER
import com.tarasovvp.blacklister.constants.Constants.USERS
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.toHashMapFromList
import com.tarasovvp.blacklister.model.BlackNumber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface BlackNumberRepository {
    suspend fun insertAllBlackNumbers(result: () -> Unit)
    suspend fun blackNumbersRemoteCount(
        blackNumber: String,
        result: (ArrayList<BlackNumber?>) -> Unit,
    )

    suspend fun allBlackNumbers(): List<BlackNumber>?
    suspend fun getBlackNumber(blackNumber: String): BlackNumber?
    suspend fun insertBlackNumber(blackNumber: BlackNumber, result: () -> Unit)
    suspend fun deleteBlackNumber(blackNumber: BlackNumber, result: () -> Unit)
    suspend fun getHashMapFromBlackNumberList(blackNumberList: List<BlackNumber>): HashMap<String, List<BlackNumber>>
    fun getBlackNumberList(blackNumber: String): List<BlackNumber>?
}

object BlackNumberRepositoryImpl : BlackNumberRepository {

    private val dao = BlackListerApp.instance?.database?.blackNumberDao()
    val database = FirebaseDatabase.getInstance(Constants.REALTIME_DATABASE).reference

    override suspend fun insertAllBlackNumbers(result: () -> Unit) {
        dao?.deleteAllBlackNumbers()
        database.child(USERS).child(FirebaseAuth.getInstance().currentUser?.uid.orEmpty())
            .child(BLACK_NUMBER).get().addOnSuccessListener { snapshot ->
                val blackNumberList = arrayListOf<BlackNumber>()
                snapshot.children.forEach {
                    it.getValue<BlackNumber>()?.let { blackNumber ->
                        blackNumberList.add(blackNumber)
                    }
                }
                Log.e("firebase",
                    "Got value ${snapshot.value} blackNumberList ${Gson().toJson(blackNumberList)}")
                blackNumberList.apply {
                    Log.e("firebase", "insertAllBlackNumbers this ${Gson().toJson(this)}")
                    dao?.insertAllBlackNumbers(this)
                }
                result.invoke()
            }.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
            }
    }

    override suspend fun allBlackNumbers(): List<BlackNumber>? {
        return dao?.getAllBlackNumbers()
    }

    override suspend fun blackNumbersRemoteCount(
        blackNumber: String,
        result: (ArrayList<BlackNumber?>) -> Unit,
    ) {
        val test =
            database.child(USERS).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val blackNumberList = arrayListOf<BlackNumber?>()
                    val blackNumberObject =
                        snapshot.getValue<HashMap<String, HashMap<String, HashMap<String, BlackNumber>>>>()
                    blackNumberObject?.values?.forEach {
                        it.values.forEach { numberType ->
                            numberType.values.forEach { number ->
                                if (number.blackNumber == blackNumber) {
                                    blackNumberList.add(number)
                                }
                            }
                        }
                    }
                    result.invoke(blackNumberList)
                    Log.e("firebase",
                        "blackNumbersRemoteCount children blackNumberObject values ${
                            Gson().toJson(blackNumberObject?.values)
                        }    blackNumberList ${Gson().toJson(blackNumberList)}")
                    Log.e("firebase",
                        "blackNumbersRemoteCount blackNumberList ${Gson().toJson(blackNumberList)} }")
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
        if (BlackListerApp.instance?.isLoggedInUser().isTrue()) {
            database.child(USERS).child(FirebaseAuth.getInstance().currentUser?.uid.orEmpty())
                .child(BLACK_NUMBER).child(blackNumber.blackNumber).setValue(blackNumber)
                .addOnCompleteListener {
                    dao?.insertBlackNumber(blackNumber)
                    result.invoke()
                }
        } else {
            dao?.insertBlackNumber(blackNumber)
            result.invoke()
        }
    }

    override suspend fun deleteBlackNumber(blackNumber: BlackNumber, result: () -> Unit) {
        if (BlackListerApp.instance?.isLoggedInUser().isTrue()) {
            database.child(USERS).child(FirebaseAuth.getInstance().currentUser?.uid.orEmpty())
                .child(BLACK_NUMBER).child(blackNumber.blackNumber).removeValue()
                .addOnCompleteListener {
                    dao?.delete(blackNumber)
                    result.invoke()
                }
        } else {
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

    override fun getBlackNumberList(blackNumber: String): List<BlackNumber>? {
        return dao?.getBlackNumberList(blackNumber)
    }

}