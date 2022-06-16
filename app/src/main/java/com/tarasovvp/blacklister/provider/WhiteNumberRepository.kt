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
import com.tarasovvp.blacklister.constants.Constants.WHITE_NUMBER
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.toHashMapFromList
import com.tarasovvp.blacklister.model.WhiteNumber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface WhiteNumberRepository {
    suspend fun insertAllWhiteNumbers(result: () -> Unit)
    suspend fun whiteNumbersRemoteCount(
        whiteNumber: String,
        result: (ArrayList<WhiteNumber?>) -> Unit,
    )

    suspend fun allWhiteNumbers(): List<WhiteNumber>?
    suspend fun getWhiteNumber(whiteNumber: String): WhiteNumber?
    suspend fun checkWhiteNumber(whiteNumber: WhiteNumber, result: () -> Unit)
    suspend fun insertWhiteNumber(whiteNumber: WhiteNumber, result: () -> Unit)
    suspend fun deleteWhiteNumber(whiteNumber: WhiteNumber, result: () -> Unit)
    suspend fun getHashMapFromWhiteNumberList(whiteNumberList: List<WhiteNumber>): HashMap<String, List<WhiteNumber>>
    fun getWhiteNumberList(whiteNumber: String): List<WhiteNumber>?
}

object WhiteNumberRepositoryImpl : WhiteNumberRepository {

    private val dao = BlackListerApp.instance?.database?.whiteNumberDao()
    val database = FirebaseDatabase.getInstance(Constants.REALTIME_DATABASE).reference

    override suspend fun insertAllWhiteNumbers(result: () -> Unit) {
        dao?.deleteAllWhiteNumbers()
        database.child(USERS).child(FirebaseAuth.getInstance().currentUser?.uid.orEmpty())
            .child(WHITE_NUMBER).get()
            .addOnSuccessListener { snapshot ->
                val whiteNumberList = arrayListOf<WhiteNumber>()
                snapshot.children.forEach {
                    it.getValue<WhiteNumber>()?.let { whiteNumber ->
                        whiteNumberList.add(whiteNumber)
                        Log.e("firebase",
                            "WhiteNumberRepository it.getValue<WhiteNumber> whiteNumber ${
                                Gson().toJson(whiteNumber)
                            } value ${Gson().toJson(snapshot.value)}")
                    }
                }
                Log.e("firebase",
                    "Got value ${snapshot.value} whiteNumberList ${Gson().toJson(whiteNumberList)}")
                whiteNumberList.apply {
                    Log.e("firebase", "insertAllWhiteNumbers this ${Gson().toJson(this)}")
                    dao?.insertAllWhiteNumbers(this)
                }
                result.invoke()
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
                    val whiteNumberList = arrayListOf<WhiteNumber?>()
                    val blackNumberObject =
                        snapshot.getValue<HashMap<String, HashMap<String, WhiteNumber>>>()
                    blackNumberObject?.values?.forEach {
                        it.values.forEach { number ->
                            if (number.whiteNumber == whiteNumber) {
                                whiteNumberList.add(number)
                            }
                        }
                    }
                    result.invoke(whiteNumberList)
                    Log.e("firebase",
                        "blackNumbersRemoteCount children blackNumberObject values ${
                            Gson().toJson(blackNumberObject?.values)
                        }    blackNumberList ${Gson().toJson(whiteNumberList)}")
                    Log.e("firebase",
                        "whiteNumbersRemoteCount whiteNumberList ${Gson().toJson(whiteNumberList)} }")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("firebase", "whiteNumbersRemoteCount error ${Gson().toJson(error)}")
                }
            })
        Log.e("firebase", "whiteNumbersRemoteCount test ${Gson().toJson(test)}")
    }

    override suspend fun getWhiteNumber(whiteNumber: String): WhiteNumber? {
        return dao?.getWhiteNumber(whiteNumber)
    }

    override suspend fun checkWhiteNumber(whiteNumber: WhiteNumber, result: () -> Unit) {
        if (BlackListerApp.instance?.isLoggedInUser().isTrue()) {
            database.child(USERS).child(FirebaseAuth.getInstance().currentUser?.uid.orEmpty())
                .child(WHITE_NUMBER)
                .child(whiteNumber.whiteNumber).setValue(whiteNumber).addOnCompleteListener {
                    dao?.insertWhiteNumber(whiteNumber)
                    Log.e("insertTAG",
                        "WhiteNumberRepositoryImpl checkWhiteNumber isLoggedInUser() getAllWhiteNumbers ${
                            Gson().toJson(dao?.getAllWhiteNumbers())
                        }")
                    result.invoke()
                }
        } else {
            dao?.insertWhiteNumber(whiteNumber)
            Log.e("insertTAG",
                "WhiteNumberRepositoryImpl checkWhiteNumber notLoggedUser getAllWhiteNumbers ${
                    Gson().toJson(dao?.getAllWhiteNumbers())
                }")
            result.invoke()
        }
    }

    override suspend fun insertWhiteNumber(whiteNumber: WhiteNumber, result: () -> Unit) {
        if (BlackListerApp.instance?.isLoggedInUser().isTrue()) {
            database.child(USERS).child(FirebaseAuth.getInstance().currentUser?.uid.orEmpty())
                .child(WHITE_NUMBER)
                .child(whiteNumber.whiteNumber).setValue(whiteNumber).addOnCompleteListener {
                    dao?.insertWhiteNumber(whiteNumber)
                    Log.e("insertTAG",
                        "WhiteNumberRepositoryImpl insertWhiteNumber isLoggedInUser() getAllWhiteNumbers ${
                            Gson().toJson(dao?.getAllWhiteNumbers())
                        }")
                    result.invoke()
                }
        } else {
            dao?.insertWhiteNumber(whiteNumber)
            Log.e("insertTAG",
                "WhiteNumberRepositoryImpl insertWhiteNumber notLoggedUser getAllWhiteNumbers ${
                    Gson().toJson(dao?.getAllWhiteNumbers())
                }")
            result.invoke()
        }
    }

    override suspend fun deleteWhiteNumber(whiteNumber: WhiteNumber, result: () -> Unit) {
        if (BlackListerApp.instance?.isLoggedInUser().isTrue()) {
            database.child(USERS).child(FirebaseAuth.getInstance().currentUser?.uid.orEmpty())
                .child(WHITE_NUMBER)
                .child(whiteNumber.whiteNumber).removeValue().addOnCompleteListener {
                    dao?.delete(whiteNumber)
                    result.invoke()
                }
        } else {
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

    override fun getWhiteNumberList(whiteNumber: String): List<WhiteNumber>? {
        return dao?.getWhiteNumberList(whiteNumber)
    }
}