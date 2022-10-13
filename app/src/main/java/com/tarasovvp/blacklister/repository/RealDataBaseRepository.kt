package com.tarasovvp.blacklister.repository

import android.content.Intent
import com.google.firebase.database.FirebaseDatabase
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.constants.Constants
import com.tarasovvp.blacklister.constants.Constants.BLOCK_HIDDEN
import com.tarasovvp.blacklister.constants.Constants.EXCEPTION
import com.tarasovvp.blacklister.constants.Constants.FILTER_LIST
import com.tarasovvp.blacklister.constants.Constants.USERS
import com.tarasovvp.blacklister.constants.Constants.WHITE_LIST_PRIORITY
import com.tarasovvp.blacklister.extensions.isTrue
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
                        WHITE_LIST_PRIORITY -> currentUser.whiteListPriority =
                            snapshot.getValue(Boolean::class.java).isTrue()
                        FILTER_LIST -> {
                            snapshot.children.forEach { child ->
                                child.getValue(Filter::class.java)
                                    ?.let { currentUser.filterList.add(it) }
                            }
                        }
                    }
                }
                result.invoke(currentUser)
            }.addOnFailureListener {
                sendExceptionBroadCast(it.localizedMessage.orEmpty())
            }
    }

    fun insertFilter(whiteFilter: Filter, result: () -> Unit) {
        currentUserDatabase.child(FILTER_LIST).child(whiteFilter.filter).setValue(whiteFilter)
            .addOnCompleteListener { task ->
                if (task.isSuccessful.not()) return@addOnCompleteListener
                result.invoke()
            }.addOnFailureListener {
                sendExceptionBroadCast(it.localizedMessage.orEmpty())
            }
    }

    fun deleteFilterList(whiteFilterList: List<Filter>, result: () -> Unit) {
        currentUserDatabase.child(FILTER_LIST).get()
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