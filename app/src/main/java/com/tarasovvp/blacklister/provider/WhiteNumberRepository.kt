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
import com.tarasovvp.blacklister.constants.Constants.USERS
import com.tarasovvp.blacklister.extensions.toHashMapFromList
import com.tarasovvp.blacklister.model.WhiteNumber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface WhiteNumberRepository {
    suspend fun insertAllWhiteNumbers()
    suspend fun whiteNumbersRemoteCount(
        whiteNumber: String,
        result: (ArrayList<WhiteNumber?>) -> Unit,
    )

    suspend fun allWhiteNumbers(): List<WhiteNumber>?
    suspend fun getWhiteNumber(whiteNumber: String): WhiteNumber?
    suspend fun insertWhiteNumber(whiteNumber: WhiteNumber, result: () -> Unit)
    suspend fun deleteWhiteNumber(whiteNumber: WhiteNumber, result: () -> Unit)
    suspend fun getHashMapFromWhiteNumberList(whiteNumberList: List<WhiteNumber>): HashMap<String, List<WhiteNumber>>
}

object WhiteNumberRepositoryImpl : WhiteNumberRepository {

    private val dao = BlackListerApp.instance?.database?.whiteNumberDao()
    val database = FirebaseDatabase.getInstance(Constants.REALTIME_DATABASE).reference

    override suspend fun insertAllWhiteNumbers() {
        dao?.deleteAllWhiteNumbers()
        database.child(USERS).child(FirebaseAuth.getInstance().currentUser?.uid.orEmpty()).get()
            .addOnSuccessListener { snapshot ->
                val whiteNumberList = arrayListOf<WhiteNumber>()
                snapshot.children.forEach {
                    it.getValue<WhiteNumber>()?.let { whiteNumber ->
                        whiteNumberList.add(whiteNumber)
                    }
                }
                Log.e("firebase",
                    "Got value ${snapshot.value} blackNumberList ${Gson().toJson(whiteNumberList)}")
                whiteNumberList.apply {
                    Log.e("firebase", "insertAllBlackNumbers this ${Gson().toJson(this)}")
                    dao?.insertAllWhiteNumbers(this)
                }
            }.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
            }
    }

    override suspend fun allWhiteNumbers(): List<WhiteNumber>? {
        return dao?.getAllWhiteNumbers()
    }

    override suspend fun whiteNumbersRemoteCount(
        whiteNumber: String,
        result: (ArrayList<WhiteNumber?>) -> Unit,
    ) {
        val test =
            database.child(USERS).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val blackNumberList = arrayListOf<WhiteNumber?>()
                    val blackNumberObject =
                        snapshot.getValue<HashMap<String, HashMap<String, WhiteNumber>>>()
                    blackNumberObject?.values?.forEach {
                        it.values.forEach { number ->
                            if (number.whiteNumber == whiteNumber) {
                                blackNumberList.add(number)
                            }
                        }
                    }
                    result.invoke(blackNumberList)
                    Log.e("firebase",
                        "blackNumbersRemoteCount children blackNumberObject values ${
                            Gson().toJson(blackNumberObject?.values)
                        }    blackNumberList ${Gson().toJson(blackNumberList)}")
                    /*snapshot.children.forEach { dataSnapshot ->
                        val blackNumberObject = dataSnapshot.children
                        blackNumberObject.forEach { bNumberObject ->
                            val obj = bNumberObject.getValue<BlackNumber>()
                            Log.e("firebase", "blackNumbersRemoteCount obj ${Gson().toJson(obj)}   }")
                        }
                        Log.e("firebase", "blackNumbersRemoteCount children blackNumberObject ${Gson().toJson(blackNumberObject)}   }")
                    }*/

                    //blackNumberList?.let { result.invoke(blackNumberList.values as List<BlackNumber>) }
                    Log.e("firebase",
                        "blackNumbersRemoteCount blackNumberList ${Gson().toJson(blackNumberList)} }")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("firebase", "blackNumbersRemoteCount error ${Gson().toJson(error)}")
                }
            })
        Log.e("firebase", "blackNumbersRemoteCount test ${Gson().toJson(test)}")
    }

    override suspend fun getWhiteNumber(whiteNumber: String): WhiteNumber? {
        return dao?.getWhiteNumber(whiteNumber)
    }

    override suspend fun insertWhiteNumber(whiteNumber: WhiteNumber, result: () -> Unit) {
        database.child(USERS).child(FirebaseAuth.getInstance().currentUser?.uid.orEmpty())
            .child(whiteNumber.whiteNumber).setValue(whiteNumber).addOnCompleteListener {
                dao?.insertWhiteNumber(whiteNumber)
                result.invoke()
            }
    }

    override suspend fun deleteWhiteNumber(whiteNumber: WhiteNumber, result: () -> Unit) {
        database.child(USERS).child(FirebaseAuth.getInstance().currentUser?.uid.orEmpty())
            .child(whiteNumber.whiteNumber).removeValue().addOnCompleteListener {
                dao?.delete(whiteNumber)
                result.invoke()
            }
    }

    override suspend fun getHashMapFromWhiteNumberList(whiteNumberList: List<WhiteNumber>): HashMap<String, List<WhiteNumber>> =
        withContext(
            Dispatchers.Default
        ) {
            whiteNumberList.toHashMapFromList()
        }
}