package com.tarasovvp.blacklister.repository

import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.constants.Constants
import com.tarasovvp.blacklister.constants.Constants.BLACK_LIST
import com.tarasovvp.blacklister.constants.Constants.USERS
import com.tarasovvp.blacklister.constants.Constants.WHITE_LIST
import com.tarasovvp.blacklister.constants.Constants.WHITE_LIST_PRIORITY
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.model.BlackNumber
import com.tarasovvp.blacklister.model.CurrentUser
import com.tarasovvp.blacklister.model.WhiteNumber

object RealDataBaseRepository {

    val database = FirebaseDatabase.getInstance(Constants.REALTIME_DATABASE).reference
    private val currentUserDatabase =
        database.child(USERS).child(FirebaseAuth.getInstance().currentUser?.uid.orEmpty())

    fun getCurrentUser(result: (CurrentUser?) -> Unit) {
        currentUserDatabase.get()
            .addOnCompleteListener { task ->
                val currentUser = CurrentUser()
                task.result.children.forEach { snapshot ->
                    when (snapshot.key) {
                        WHITE_LIST_PRIORITY -> currentUser.isWhiteListPriority =
                            snapshot.getValue(Boolean::class.java).isTrue()
                        BLACK_LIST -> {
                            snapshot.children.forEach { child ->
                                child.getValue(BlackNumber::class.java)
                                    ?.let { currentUser.blackNumberList.add(it) }
                            }
                        }
                        WHITE_LIST -> {
                            snapshot.children.forEach { child ->
                                child.getValue(WhiteNumber::class.java)
                                    ?.let { currentUser.whiteNumberList.add(it) }
                            }
                        }
                    }
                }
                result.invoke(currentUser)
            }.addOnFailureListener {
                sendExceptionBroadCast(it.localizedMessage.orEmpty())
            }
    }

    fun insertBlackNumber(blackNumber: BlackNumber, result: () -> Unit) {
        currentUserDatabase.child(BLACK_LIST).child(blackNumber.number).setValue(blackNumber)
            .addOnCompleteListener {
                result.invoke()
            }.addOnFailureListener {
            sendExceptionBroadCast(it.localizedMessage.orEmpty())
        }
    }

    fun insertWhiteNumber(whiteNumber: WhiteNumber, result: () -> Unit) {
        currentUserDatabase.child(WHITE_LIST).child(whiteNumber.number).setValue(whiteNumber)
            .addOnCompleteListener {
                result.invoke()
            }.addOnFailureListener {
            sendExceptionBroadCast(it.localizedMessage.orEmpty())
        }
    }

    fun deleteWhiteNumber(whiteNumber: WhiteNumber, result: () -> Unit) {
        currentUserDatabase.child(WHITE_LIST).child(whiteNumber.number).removeValue()
            .addOnCompleteListener {
                result.invoke()
            }.addOnFailureListener {
                sendExceptionBroadCast(it.localizedMessage.orEmpty())
            }
    }

    fun deleteWhiteNumberList(whiteNumberList: List<WhiteNumber>, result: () -> Unit) {
        currentUserDatabase.child(WHITE_LIST).get()
            .addOnCompleteListener { task ->
                task.result.children.forEach { snapshot ->
                    if (whiteNumberList.map { it.number }.contains(snapshot.key)) snapshot.ref.removeValue()
                }
                result.invoke()
            }.addOnFailureListener {
                sendExceptionBroadCast(it.localizedMessage.orEmpty())
            }
    }

    fun deleteBlackNumber(blackNumber: BlackNumber, result: () -> Unit) {
        currentUserDatabase.child(BLACK_LIST).child(blackNumber.number).removeValue()
            .addOnCompleteListener {
                result.invoke()
            }.addOnFailureListener {
                sendExceptionBroadCast(it.localizedMessage.orEmpty())
            }
    }

    fun deleteBlackNumberList(blackNumberList: List<BlackNumber>, result: () -> Unit) {
        currentUserDatabase.child(BLACK_LIST).get()
            .addOnCompleteListener { task ->
                task.result.children.forEach { snapshot ->
                    if (blackNumberList.map { it.number }.contains(snapshot.key)) snapshot.ref.removeValue()
                }
                result.invoke()
            }.addOnFailureListener {
                sendExceptionBroadCast(it.localizedMessage.orEmpty())
            }
    }

    fun changeWhiteListPriority(whiteListPriority: Boolean, result: () -> Unit) {
        currentUserDatabase.child(WHITE_LIST_PRIORITY).setValue(whiteListPriority)
            .addOnCompleteListener {
                result.invoke()
            }.addOnFailureListener {
                sendExceptionBroadCast(it.localizedMessage.orEmpty())
            }
    }

    private fun sendExceptionBroadCast(exception: String) {
        val intent = Intent(Constants.EXCEPTION)
        intent.putExtra(Constants.EXCEPTION, exception)
        BlackListerApp.instance?.sendBroadcast(intent)
    }
}