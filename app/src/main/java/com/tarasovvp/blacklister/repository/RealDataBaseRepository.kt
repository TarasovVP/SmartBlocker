package com.tarasovvp.blacklister.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.tarasovvp.blacklister.constants.Constants
import com.tarasovvp.blacklister.constants.Constants.BLACK_NUMBER
import com.tarasovvp.blacklister.constants.Constants.USERS
import com.tarasovvp.blacklister.constants.Constants.WHITE_LIST_PRIORITY
import com.tarasovvp.blacklister.constants.Constants.WHITE_NUMBER
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.model.BlackNumber
import com.tarasovvp.blacklister.model.WhiteNumber

object RealDataBaseRepository {

    val database = FirebaseDatabase.getInstance(Constants.REALTIME_DATABASE).reference
    val currentUserDatabase =
        database.child(USERS).child(FirebaseAuth.getInstance().currentUser?.uid.orEmpty())

    suspend fun blackNumbersRemoteCount(
        blackNumber: String,
        result: (ArrayList<BlackNumber?>) -> Unit,
    ) {
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

    suspend fun getWhiteNumbers(result: (ArrayList<WhiteNumber>) -> Unit) {
        currentUserDatabase.child(WHITE_NUMBER).get().addOnSuccessListener { snapshot ->
            val whiteNumberList = arrayListOf<WhiteNumber>()
            snapshot.children.forEach {
                it.getValue<WhiteNumber>()?.let { whiteNumber ->
                    whiteNumberList.add(whiteNumber)
                }
            }
            result.invoke(whiteNumberList)
        }.addOnFailureListener {
            //TODO implement error message
        }
    }

    suspend fun getBlackNumbers(result: (ArrayList<BlackNumber>) -> Unit) {
        currentUserDatabase.child(BLACK_NUMBER).get().addOnSuccessListener { snapshot ->
            val blackNumberList = arrayListOf<BlackNumber>()
            snapshot.children.forEach {
                it.getValue<BlackNumber>()?.let { blackNumber ->
                    blackNumberList.add(blackNumber)
                }
            }
            result.invoke(blackNumberList)
        }.addOnFailureListener {
            //TODO implement error message
        }
    }

    suspend fun insertBlackNumber(blackNumber: BlackNumber, result: () -> Unit) {
        currentUserDatabase.child(BLACK_NUMBER).setValue(blackNumber).addOnCompleteListener {
            result.invoke()
        }.addOnFailureListener {
            //TODO implement error message
        }
    }

    suspend fun insertWhiteNumber(whiteNumber: WhiteNumber, result: () -> Unit) {
        currentUserDatabase.child(WHITE_NUMBER).setValue(whiteNumber).addOnCompleteListener {
            result.invoke()
        }.addOnFailureListener {
            //TODO implement error message
        }
    }

    suspend fun deleteWhiteNumber(whiteNumber: WhiteNumber, result: () -> Unit) {
        currentUserDatabase.child(WHITE_NUMBER).child(whiteNumber.whiteNumber).removeValue()
            .addOnCompleteListener {
                result.invoke()
            }.addOnFailureListener {
                //TODO implement error message
            }
    }

    suspend fun deleteBlackNumber(blackNumber: BlackNumber, result: () -> Unit) {
        currentUserDatabase.child(BLACK_NUMBER).child(blackNumber.blackNumber).removeValue()
            .addOnCompleteListener {
                result.invoke()
            }.addOnFailureListener {
                //TODO implement error message
            }
    }

    suspend fun getWhiteListPriority(result: (Boolean) -> Unit) {
        currentUserDatabase.child(WHITE_LIST_PRIORITY).get()
            .addOnCompleteListener {
                result.invoke(it.result.getValue(Boolean::class.java).isTrue())
            }.addOnFailureListener {
                //TODO implement error message
            }
    }

    suspend fun changeWhiteListPriority(whiteListPriority: Boolean, result: () -> Unit) {
        currentUserDatabase.child(WHITE_LIST_PRIORITY).setValue(whiteListPriority)
            .addOnCompleteListener {
                result.invoke()
            }.addOnFailureListener {
                //TODO implement error message
            }
    }
}