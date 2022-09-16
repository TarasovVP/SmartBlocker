package com.tarasovvp.blacklister.repository

import android.content.Intent
import com.google.firebase.database.FirebaseDatabase
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.constants.Constants
import com.tarasovvp.blacklister.constants.Constants.BLACK_LIST
import com.tarasovvp.blacklister.constants.Constants.BLOCK_HIDDEN
import com.tarasovvp.blacklister.constants.Constants.EXCEPTION
import com.tarasovvp.blacklister.constants.Constants.USERS
import com.tarasovvp.blacklister.constants.Constants.WHITE_LIST
import com.tarasovvp.blacklister.constants.Constants.WHITE_LIST_PRIORITY
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.model.BlackFilter
import com.tarasovvp.blacklister.model.CurrentUser
import com.tarasovvp.blacklister.model.WhiteFilter

object RealDataBaseRepository {

    var database = FirebaseDatabase.getInstance(Constants.REALTIME_DATABASE).reference
    private var currentUserDatabase =
        database.child(USERS).child(BlackListerApp.instance?.auth?.currentUser?.uid.orEmpty())

    fun getCurrentUser(result: (CurrentUser?) -> Unit) {
        if (currentUserDatabase.key == USERS) currentUserDatabase = currentUserDatabase.child(BlackListerApp.instance?.auth?.currentUser?.uid.orEmpty())
        currentUserDatabase.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful.not()) return@addOnCompleteListener
                val currentUser = CurrentUser()
                task.result.children.forEach { snapshot ->
                    when (snapshot.key) {
                        WHITE_LIST_PRIORITY -> currentUser.isWhiteListPriority =
                            snapshot.getValue(Boolean::class.java).isTrue()
                        BLACK_LIST -> {
                            snapshot.children.forEach { child ->
                                child.getValue(BlackFilter::class.java)
                                    ?.let { currentUser.blackFilterList.add(it) }
                            }
                        }
                        WHITE_LIST -> {
                            snapshot.children.forEach { child ->
                                child.getValue(WhiteFilter::class.java)
                                    ?.let { currentUser.whiteFilterList.add(it) }
                            }
                        }
                    }
                }
                result.invoke(currentUser)
            }.addOnFailureListener {
                sendExceptionBroadCast(it.localizedMessage.orEmpty())
            }
    }

    fun insertBlackFilter(blackFilter: BlackFilter, result: () -> Unit) {
        currentUserDatabase.child(BLACK_LIST).child(blackFilter.filter).setValue(blackFilter)
            .addOnCompleteListener { task ->
                if (task.isSuccessful.not()) return@addOnCompleteListener
                result.invoke()
            }.addOnFailureListener {
                sendExceptionBroadCast(it.localizedMessage.orEmpty())
            }
    }

    fun insertWhiteFilter(whiteFilter: WhiteFilter, result: () -> Unit) {
        currentUserDatabase.child(WHITE_LIST).child(whiteFilter.filter).setValue(whiteFilter)
            .addOnCompleteListener { task ->
                if (task.isSuccessful.not()) return@addOnCompleteListener
                result.invoke()
            }.addOnFailureListener {
                sendExceptionBroadCast(it.localizedMessage.orEmpty())
            }
    }

    fun deleteWhiteFilter(whiteFilter: WhiteFilter, result: () -> Unit) {
        currentUserDatabase.child(WHITE_LIST).child(whiteFilter.filter).removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful.not()) return@addOnCompleteListener
                result.invoke()
            }.addOnFailureListener {
                sendExceptionBroadCast(it.localizedMessage.orEmpty())
            }
    }

    fun deleteWhiteFilterList(whiteFilterList: List<WhiteFilter>, result: () -> Unit) {
        currentUserDatabase.child(WHITE_LIST).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful.not()) return@addOnCompleteListener
                task.result.children.forEach { snapshot ->
                    if (whiteFilterList.map { it.filter }
                            .contains(snapshot.key)) snapshot.ref.removeValue()
                }
                result.invoke()
            }.addOnFailureListener {
                sendExceptionBroadCast(it.localizedMessage.orEmpty())
            }
    }

    fun deleteBlackFilter(blackFilter: BlackFilter, result: () -> Unit) {
        currentUserDatabase.child(BLACK_LIST).child(blackFilter.filter).removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful.not()) return@addOnCompleteListener
                result.invoke()
            }.addOnFailureListener {
                sendExceptionBroadCast(it.localizedMessage.orEmpty())
            }
    }

    fun deleteBlackFilterList(blackFilterList: List<BlackFilter>, result: () -> Unit) {
        currentUserDatabase.child(BLACK_LIST).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful.not()) return@addOnCompleteListener
                task.result.children.forEach { snapshot ->
                    if (blackFilterList.map { it.filter }
                            .contains(snapshot.key)) snapshot.ref.removeValue()
                }
                result.invoke()
            }.addOnFailureListener {
                sendExceptionBroadCast(it.localizedMessage.orEmpty())
            }
    }

    fun changeWhiteListPriority(whiteListPriority: Boolean, result: () -> Unit) {
        currentUserDatabase.child(WHITE_LIST_PRIORITY).setValue(whiteListPriority)
            .addOnCompleteListener { task ->
                if (task.isSuccessful.not()) return@addOnCompleteListener
                result.invoke()
            }.addOnFailureListener {
                sendExceptionBroadCast(it.localizedMessage.orEmpty())
            }
    }

    fun changeBlockHidden(blockUnanimous: Boolean, result: () -> Unit) {
        currentUserDatabase.child(BLOCK_HIDDEN).setValue(blockUnanimous)
            .addOnCompleteListener { task ->
                if (task.isSuccessful.not()) return@addOnCompleteListener
                result.invoke()
            }.addOnFailureListener {
                sendExceptionBroadCast(it.localizedMessage.orEmpty())
            }
    }

    private fun sendExceptionBroadCast(exception: String) {
        val intent = Intent(EXCEPTION)
        intent.putExtra(EXCEPTION, exception)
        BlackListerApp.instance?.sendBroadcast(intent)
    }
}