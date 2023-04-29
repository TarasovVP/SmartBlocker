package com.tarasovvp.smartblocker.data.repositoryImpl

import com.google.firebase.database.DatabaseReference
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.domain.models.entities.FilteredCall
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCK_HIDDEN
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.FILTERED_CALL_LIST
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.FILTER_LIST
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.REVIEWS
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.USERS
import com.tarasovvp.smartblocker.domain.models.entities.CurrentUser
import com.tarasovvp.smartblocker.domain.models.Review
import com.tarasovvp.smartblocker.utils.extensions.sendExceptionBroadCast
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import javax.inject.Inject

class RealDataBaseRepositoryImpl @Inject constructor(private val database: DatabaseReference, private val smartBlockerApp: SmartBlockerApp) :
    RealDataBaseRepository {

    override fun getCurrentUser(result: (CurrentUser?) -> Unit) {
        var currentUserDatabase = database.child(USERS).child(smartBlockerApp.auth?.currentUser?.uid.orEmpty())
        if (currentUserDatabase.key != smartBlockerApp.auth?.currentUser?.uid.orEmpty()) currentUserDatabase =
            database.child(USERS).child(smartBlockerApp.auth?.currentUser?.uid.orEmpty())
        currentUserDatabase.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful.not()) return@addOnCompleteListener
                val currentUser = CurrentUser()
                task.result.children.forEach { snapshot ->
                    when (snapshot.key) {
                        FILTER_LIST -> {
                            snapshot.children.forEach { child ->
                                child.getValue(Filter::class.java)?.let { currentUser.filterList.add(it) }
                            }
                        }
                        FILTERED_CALL_LIST -> {
                            snapshot.children.forEach { child ->
                                child.getValue(FilteredCall::class.java)?.let { currentUser.filteredCallList.add(it) }
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
        if (smartBlockerApp.checkNetworkUnAvailable()) return
        database.child(USERS).child(smartBlockerApp.auth?.currentUser?.uid.orEmpty()).child(FILTER_LIST).child(filter.filter).setValue(filter)
            .addOnCompleteListener { task ->
                if (task.isSuccessful.not()) return@addOnCompleteListener
                result.invoke()
            }.addOnFailureListener {
                it.localizedMessage.orEmpty().sendExceptionBroadCast()
            }
    }

    override fun deleteFilterList(filterList: List<Filter?>, result: () -> Unit) {
        if (smartBlockerApp.checkNetworkUnAvailable()) return
        database.child(USERS).child(smartBlockerApp.auth?.currentUser?.uid.orEmpty()).child(FILTER_LIST).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful.not()) return@addOnCompleteListener
                task.result.children.forEach { snapshot ->
                    if (filterList.map { it?.filter }.contains(snapshot.key)) snapshot.ref.removeValue()
                }
                result.invoke()
            }.addOnFailureListener {
                it.localizedMessage.orEmpty().sendExceptionBroadCast()
            }
    }

    override fun insertFilteredCall(filteredCall: FilteredCall, result: () -> Unit) {
        if (smartBlockerApp.checkNetworkUnAvailable()) return
        database.child(USERS).child(smartBlockerApp.auth?.currentUser?.uid.orEmpty()).child(FILTERED_CALL_LIST).child(filteredCall.callId.toString())
            .setValue(filteredCall)
            .addOnCompleteListener { task ->
                if (task.isSuccessful.not()) return@addOnCompleteListener
                result.invoke()
            }.addOnFailureListener {
                it.localizedMessage.orEmpty().sendExceptionBroadCast()
            }
    }

    override fun deleteFilteredCallList(filteredCallIdList: List<String>, result: () -> Unit) {
        if (smartBlockerApp.checkNetworkUnAvailable()) return
        database.child(USERS).child(smartBlockerApp.auth?.currentUser?.uid.orEmpty()).child(FILTERED_CALL_LIST).get()
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
        if (smartBlockerApp.checkNetworkUnAvailable()) return
        database.child(USERS).child(smartBlockerApp.auth?.currentUser?.uid.orEmpty()).child(BLOCK_HIDDEN).setValue(blockUnanimous)
            .addOnCompleteListener { task ->
                if (task.isSuccessful.not()) return@addOnCompleteListener
                result.invoke()
            }.addOnFailureListener {
                it.localizedMessage.orEmpty().sendExceptionBroadCast()
            }
    }

    override fun insertReview(review: Review, result: () -> Unit) {
        if (smartBlockerApp.checkNetworkUnAvailable()) return
        database.child(REVIEWS).child(review.time.toString()).setValue(review)
            .addOnCompleteListener { task ->
                if (task.isSuccessful.not()) return@addOnCompleteListener
                result.invoke()
            }.addOnFailureListener {
                it.localizedMessage.orEmpty().sendExceptionBroadCast()
            }
    }
}