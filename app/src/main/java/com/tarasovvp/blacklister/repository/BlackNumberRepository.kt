package com.tarasovvp.blacklister.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.constants.Constants
import com.tarasovvp.blacklister.constants.Constants.BLACK_NUMBER
import com.tarasovvp.blacklister.constants.Constants.USERS
import com.tarasovvp.blacklister.constants.Constants.WHITE_NUMBER
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.toHashMapFromList
import com.tarasovvp.blacklister.model.BlackNumber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object BlackNumberRepository {

    private val blackNumberDao = BlackListerApp.instance?.database?.blackNumberDao()
    private val whiteNumberDao = BlackListerApp.instance?.database?.whiteNumberDao()
    val database = FirebaseDatabase.getInstance(Constants.REALTIME_DATABASE).reference

    suspend fun insertAllBlackNumbers(result: () -> Unit) {
        blackNumberDao?.deleteAllBlackNumbers()
        database.child(USERS).child(FirebaseAuth.getInstance().currentUser?.uid.orEmpty())
            .child(BLACK_NUMBER).get().addOnSuccessListener { snapshot ->
                val blackNumberList = arrayListOf<BlackNumber>()
                snapshot.children.forEach {
                    it.getValue<BlackNumber>()?.let { blackNumber ->
                        blackNumberList.add(blackNumber)
                    }
                }
                blackNumberList.apply {
                    blackNumberDao?.insertAllBlackNumbers(this)
                }
                result.invoke()
            }.addOnFailureListener {
                //TODO implement error message

            }
    }

    suspend fun allBlackNumbers(): List<BlackNumber>? {
        return blackNumberDao?.getAllBlackNumbers()
    }

    suspend fun blackNumbersRemoteCount(
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
                }

                override fun onCancelled(error: DatabaseError) {
                    //TODO implement error message
                    error.message
                }
            })
    }

    suspend fun getBlackNumber(blackNumber: String): BlackNumber? {
        return blackNumberDao?.getBlackNumber(blackNumber)
    }

    suspend fun checkWhiteNumber(blackNumber: BlackNumber, result: () -> Unit) {
        if (BlackListerApp.instance?.isLoggedInUser().isTrue()) {
            database.child(USERS).child(FirebaseAuth.getInstance().currentUser?.uid.orEmpty())
                .child(WHITE_NUMBER).child(blackNumber.blackNumber).get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        //TODO implemention
                    }
                    result.invoke()
                }
        } else {
            whiteNumberDao?.getWhiteNumberList(blackNumber.blackNumber)?.forEach { number ->
                if (blackNumber.start) number.start = false
                if (blackNumber.contain) number.contain = false
                if (blackNumber.end) number.end = false
                whiteNumberDao.insertWhiteNumber(number)
            }
            result.invoke()
        }
    }

    suspend fun insertBlackNumber(blackNumber: BlackNumber, result: () -> Unit) {
        if (BlackListerApp.instance?.isLoggedInUser().isTrue()) {
            database.child(USERS).child(FirebaseAuth.getInstance().currentUser?.uid.orEmpty())
                .child(BLACK_NUMBER).child(blackNumber.blackNumber).setValue(blackNumber)
                .addOnCompleteListener {
                    blackNumberDao?.insertBlackNumber(blackNumber)
                    result.invoke()
                }
        } else {
            blackNumberDao?.insertBlackNumber(blackNumber)
            result.invoke()
        }
    }

    suspend fun deleteBlackNumber(blackNumber: BlackNumber, result: () -> Unit) {
        if (BlackListerApp.instance?.isLoggedInUser().isTrue()) {
            database.child(USERS).child(FirebaseAuth.getInstance().currentUser?.uid.orEmpty())
                .child(BLACK_NUMBER).child(blackNumber.blackNumber).removeValue()
                .addOnCompleteListener {
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