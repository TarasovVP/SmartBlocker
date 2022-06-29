package com.tarasovvp.blacklister.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.constants.Constants
import com.tarasovvp.blacklister.constants.Constants.USERS
import com.tarasovvp.blacklister.constants.Constants.WHITE_NUMBER
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.toHashMapFromList
import com.tarasovvp.blacklister.model.WhiteNumber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object WhiteNumberRepository {

    private val dao = BlackListerApp.instance?.database?.whiteNumberDao()
    val database = FirebaseDatabase.getInstance(Constants.REALTIME_DATABASE).reference

    suspend fun insertAllWhiteNumbers(result: () -> Unit) {
        dao?.deleteAllWhiteNumbers()
        database.child(USERS).child(FirebaseAuth.getInstance().currentUser?.uid.orEmpty())
            .child(WHITE_NUMBER).get()
            .addOnSuccessListener { snapshot ->
                val whiteNumberList = arrayListOf<WhiteNumber>()
                snapshot.children.forEach {
                    it.getValue<WhiteNumber>()?.let { whiteNumber ->
                        whiteNumberList.add(whiteNumber)
                    }
                }
                whiteNumberList.apply {
                    dao?.insertAllWhiteNumbers(this)
                }
                result.invoke()
            }.addOnFailureListener {
                //TODO implement error message

            }
    }

    suspend fun allWhiteNumbers(): List<WhiteNumber>? {
        return dao?.getAllWhiteNumbers()
    }

    suspend fun whiteNumbersRemoteCount(
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
                }

                override fun onCancelled(error: DatabaseError) {
                    //TODO implement error message

                }
            })
    }

    suspend fun getWhiteNumber(whiteNumber: String): WhiteNumber? {
        return dao?.getWhiteNumber(whiteNumber)
    }

    suspend fun checkWhiteNumber(whiteNumber: WhiteNumber, result: () -> Unit) {
        if (BlackListerApp.instance?.isLoggedInUser().isTrue()) {
            database.child(USERS).child(FirebaseAuth.getInstance().currentUser?.uid.orEmpty())
                .child(WHITE_NUMBER)
                .child(whiteNumber.whiteNumber).setValue(whiteNumber).addOnCompleteListener {
                    dao?.insertWhiteNumber(whiteNumber)
                    result.invoke()
                }
        } else {
            dao?.insertWhiteNumber(whiteNumber)
            result.invoke()
        }
    }

    suspend fun insertWhiteNumber(whiteNumber: WhiteNumber, result: () -> Unit) {
        if (BlackListerApp.instance?.isLoggedInUser().isTrue()) {
            database.child(USERS).child(FirebaseAuth.getInstance().currentUser?.uid.orEmpty())
                .child(WHITE_NUMBER)
                .child(whiteNumber.whiteNumber).setValue(whiteNumber).addOnCompleteListener {
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