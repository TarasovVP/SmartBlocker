package com.tarasovvp.blacklister.repository

import android.content.Intent
import com.google.firebase.database.FirebaseDatabase
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.constants.Constants
import com.tarasovvp.blacklister.constants.Constants.BLOCKED_CALL_LIST
import com.tarasovvp.blacklister.constants.Constants.BLOCK_HIDDEN
import com.tarasovvp.blacklister.constants.Constants.EXCEPTION
import com.tarasovvp.blacklister.constants.Constants.FILTER_LIST
import com.tarasovvp.blacklister.constants.Constants.USERS
import com.tarasovvp.blacklister.model.BlockedCall
import com.tarasovvp.blacklister.model.Call
import com.tarasovvp.blacklister.model.CurrentUser
import com.tarasovvp.blacklister.model.Filter

object RealDataBaseRepository {

    var database = FirebaseDatabase.getInstance(Constants.REALTIME_DATABASE).reference
    private var currentUserDatabase =
        database.child(USERS).child(BlackListerApp.instance?.auth?.currentUser?.uid.orEmpty())

    fun getCurrentUser(result: (CurrentUser?) -> Unit) {
        if (currentUserDatabase.key == USERS) currentUserDatabase =
            currentUserDatabase.child(BlackListerApp.instance?.auth?.currentUser?.uid.orEmpty())
        currentUserDatabase.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful.not()) return@addOnCompleteListener
                val currentUser = CurrentUser()
                task.result.children.forEach { snapshot ->
                    when (snapshot.key) {
                        FILTER_LIST -> {
                            snapshot.children.forEach { child ->
                                child.getValue(Filter::class.java)
                                    ?.let { currentUser.filterList.add(it) }
                            }
                        }
                        BLOCKED_CALL_LIST -> {
                            snapshot.children.forEach { child ->
                                child.getValue(BlockedCall::class.java)
                                    ?.let { currentUser.blockedCallList.add(it) }
                            }
                        }
                    }
                }
                result.invoke(currentUser)
            }.addOnFailureListener {
                sendExceptionBroadCast(it.localizedMessage.orEmpty())
            }
    }

    fun insertFilter(filter: Filter, result: () -> Unit) {
        currentUserDatabase.child(FILTER_LIST).child(filter.filter).setValue(filter)
            .addOnCompleteListener { task ->
                if (task.isSuccessful.not()) return@addOnCompleteListener
                result.invoke()
            }.addOnFailureListener {
                sendExceptionBroadCast(it.localizedMessage.orEmpty())
            }
    }

    fun deleteFilterList(filterList: List<Filter>, result: () -> Unit) {
        currentUserDatabase.child(FILTER_LIST).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful.not()) return@addOnCompleteListener
                task.result.children.forEach { snapshot ->
                    if (filterList.map { it.filter }
                            .contains(snapshot.key)) snapshot.ref.removeValue()
                }
                result.invoke()
            }.addOnFailureListener {
                sendExceptionBroadCast(it.localizedMessage.orEmpty())
            }
    }

    fun insertBlockedCall(blockedCall: BlockedCall, result: () -> Unit) {
        currentUserDatabase.child(BLOCKED_CALL_LIST).child(blockedCall.number).setValue(blockedCall)
            .addOnCompleteListener { task ->
                if (task.isSuccessful.not()) return@addOnCompleteListener
                result.invoke()
            }.addOnFailureListener {
                sendExceptionBroadCast(it.localizedMessage.orEmpty())
            }
    }

    fun deleteBlockedCallList(blockedCallList: List<Call>, result: () -> Unit) {
        currentUserDatabase.child(FILTER_LIST).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful.not()) return@addOnCompleteListener
                task.result.children.forEach { snapshot ->
                    if (blockedCallList.map { it.number }
                            .contains(snapshot.key)) snapshot.ref.removeValue()
                }
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