package com.tarasovvp.smartblocker.repository

import android.content.Intent
import com.google.firebase.database.FirebaseDatabase
import com.tarasovvp.smartblocker.BlackListerApp
import com.tarasovvp.smartblocker.constants.Constants
import com.tarasovvp.smartblocker.constants.Constants.BLOCKED_CALL_LIST
import com.tarasovvp.smartblocker.constants.Constants.BLOCK_HIDDEN
import com.tarasovvp.smartblocker.constants.Constants.EXCEPTION
import com.tarasovvp.smartblocker.constants.Constants.FILTER_LIST
import com.tarasovvp.smartblocker.constants.Constants.USERS
import com.tarasovvp.smartblocker.models.FilteredCall
import com.tarasovvp.smartblocker.models.Call
import com.tarasovvp.smartblocker.models.CurrentUser
import com.tarasovvp.smartblocker.models.Filter

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
                                child.getValue(FilteredCall::class.java)
                                    ?.let { currentUser.filteredCallList.add(it) }
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

    fun insertFilteredCall(filteredCall: FilteredCall, result: () -> Unit) {
        currentUserDatabase.child(BLOCKED_CALL_LIST).child(filteredCall.number).setValue(filteredCall)
            .addOnCompleteListener { task ->
                if (task.isSuccessful.not()) return@addOnCompleteListener
                result.invoke()
            }.addOnFailureListener {
                sendExceptionBroadCast(it.localizedMessage.orEmpty())
            }
    }

    fun deleteFilteredCallList(filteredCallList: List<Call>, result: () -> Unit) {
        currentUserDatabase.child(FILTER_LIST).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful.not()) return@addOnCompleteListener
                task.result.children.forEach { snapshot ->
                    if (filteredCallList.map { it.number }
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