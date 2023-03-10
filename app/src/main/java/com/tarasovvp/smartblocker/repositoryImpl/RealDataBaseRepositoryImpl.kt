package com.tarasovvp.smartblocker.repositoryImpl

import com.google.firebase.database.DatabaseReference
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.constants.Constants.BLOCK_HIDDEN
import com.tarasovvp.smartblocker.constants.Constants.FILTERED_CALL_LIST
import com.tarasovvp.smartblocker.constants.Constants.FILTER_LIST
import com.tarasovvp.smartblocker.constants.Constants.REVIEWS
import com.tarasovvp.smartblocker.constants.Constants.USERS
import com.tarasovvp.smartblocker.extensions.isTrue
import com.tarasovvp.smartblocker.extensions.sendExceptionBroadCast
import com.tarasovvp.smartblocker.models.*
import com.tarasovvp.smartblocker.repository.RealDataBaseRepository
import javax.inject.Inject

class RealDataBaseRepositoryImpl @Inject constructor(private val database: DatabaseReference) :
    RealDataBaseRepository {

    private var currentUserDatabase =
        database.child(USERS).child(SmartBlockerApp.instance?.auth?.currentUser?.uid.orEmpty())
    private var reviewsDatabase = database.child(REVIEWS)

    override fun getCurrentUser(result: (CurrentUser?) -> Unit) {
        if (currentUserDatabase.key != SmartBlockerApp.instance?.auth?.currentUser?.uid.orEmpty()) currentUserDatabase =
            database.child(USERS).child(SmartBlockerApp.instance?.auth?.currentUser?.uid.orEmpty())
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
                        FILTERED_CALL_LIST -> {
                            snapshot.children.forEach { child ->
                                child.getValue(FilteredCall::class.java)
                                    ?.let { currentUser.filteredCallList.add(it) }
                            }
                        }
                    }
                }
                result.invoke(currentUser)
            }.addOnFailureListener {
                it.localizedMessage.orEmpty().sendExceptionBroadCast()
            }
    }

    override fun insertFilter(filter: Filter, result: () -> Unit) {
        if (SmartBlockerApp.instance?.checkNetworkAvailable().isTrue()) return
        currentUserDatabase.child(FILTER_LIST).child(filter.filter).setValue(filter)
            .addOnCompleteListener { task ->
                if (task.isSuccessful.not()) return@addOnCompleteListener
                result.invoke()
            }.addOnFailureListener {
                it.localizedMessage.orEmpty().sendExceptionBroadCast()
            }
    }

    override fun deleteFilterList(filterList: List<Filter?>, result: () -> Unit) {
        if (SmartBlockerApp.instance?.checkNetworkAvailable().isTrue()) return
        currentUserDatabase.child(FILTER_LIST).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful.not()) return@addOnCompleteListener
                task.result.children.forEach { snapshot ->
                    if (filterList.map { it?.filter }
                            .contains(snapshot.key)) snapshot.ref.removeValue()
                }
                result.invoke()
            }.addOnFailureListener {
                it.localizedMessage.orEmpty().sendExceptionBroadCast()
            }
    }

    override fun insertFilteredCall(filteredCall: FilteredCall, result: () -> Unit) {
        if (SmartBlockerApp.instance?.checkNetworkAvailable().isTrue()) return
        currentUserDatabase.child(FILTERED_CALL_LIST).child(filteredCall.callId.toString())
            .setValue(filteredCall)
            .addOnCompleteListener { task ->
                if (task.isSuccessful.not()) return@addOnCompleteListener
                result.invoke()
            }.addOnFailureListener {
                it.localizedMessage.orEmpty().sendExceptionBroadCast()
            }
    }

    override fun deleteFilteredCallList(filteredCallIdList: List<String>, result: () -> Unit) {
        if (SmartBlockerApp.instance?.checkNetworkAvailable().isTrue()) return
        currentUserDatabase.child(FILTERED_CALL_LIST).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful.not()) return@addOnCompleteListener
                task.result.children.forEach { snapshot ->
                    if (filteredCallIdList.contains(snapshot.key)) snapshot.ref.removeValue()
                }
                result.invoke()
            }.addOnFailureListener {
               it.localizedMessage.orEmpty().sendExceptionBroadCast()
            }
    }

    override fun changeBlockHidden(blockUnanimous: Boolean, result: () -> Unit) {
        if (SmartBlockerApp.instance?.checkNetworkAvailable().isTrue()) return
        currentUserDatabase.child(BLOCK_HIDDEN).setValue(blockUnanimous)
            .addOnCompleteListener { task ->
                if (task.isSuccessful.not()) return@addOnCompleteListener
                result.invoke()
            }.addOnFailureListener {
                it.localizedMessage.orEmpty().sendExceptionBroadCast()
            }
    }

    override fun insertReview(review: Review, result: () -> Unit) {
        if (SmartBlockerApp.instance?.checkNetworkAvailable().isTrue()) return
        reviewsDatabase.child(review.time.toString()).setValue(review)
            .addOnCompleteListener { task ->
                if (task.isSuccessful.not()) return@addOnCompleteListener
                result.invoke()
            }.addOnFailureListener {
                it.localizedMessage.orEmpty().sendExceptionBroadCast()
            }
    }
}