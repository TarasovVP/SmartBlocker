package com.tarasovvp.smartblocker.data.repositoryImpl

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.entities.db_entities.FilteredCall
import com.tarasovvp.smartblocker.domain.entities.models.CurrentUser
import com.tarasovvp.smartblocker.domain.entities.models.Review
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCK_HIDDEN
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.FILTERED_CALL_LIST
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.FILTER_LIST
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.PRIVACY_POLICY
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.REVIEWS
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.USERS
import com.tarasovvp.smartblocker.utils.extensions.isNotNull
import javax.inject.Inject

class RealDataBaseRepositoryImpl @Inject constructor(private val firebaseDatabase: FirebaseDatabase, private val firebaseAuth: FirebaseAuth) :
    RealDataBaseRepository {

    override fun createCurrentUser(currentUser: CurrentUser, result: (Result<Unit>) -> Unit) {
        val currentUserMap = hashMapOf<String, Any>()
        currentUser.filterList.forEach { filter ->
            currentUserMap["$FILTER_LIST/${filter.filter}"] = filter
        }
        currentUser.filteredCallList.forEach { filteredCall ->
            currentUserMap["$FILTERED_CALL_LIST/${filteredCall.callId}"] = filteredCall
        }
        currentUserMap[BLOCK_HIDDEN] = currentUser.isBlockHidden
        firebaseDatabase.reference.child(USERS).child(firebaseAuth.currentUser?.uid.orEmpty()).setValue(currentUser)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) result.invoke(Result.Success())
            }.addOnFailureListener { exception ->
                result.invoke(Result.Failure(exception.localizedMessage))
            }
    }

    override fun updateCurrentUser(currentUser: CurrentUser, result: (Result<Unit>) -> Unit) {
        val updatesMap = hashMapOf<String, Any>()
        currentUser.filterList.forEach { filter ->
            updatesMap["$FILTER_LIST/${filter.filter}"] = filter
        }
        currentUser.filteredCallList.forEach { filteredCall ->
            updatesMap["$FILTERED_CALL_LIST/${filteredCall.callId}"] = filteredCall
        }
        updatesMap[BLOCK_HIDDEN] = currentUser.isBlockHidden
        firebaseDatabase.reference.child(USERS).child(firebaseAuth.currentUser?.uid.orEmpty()).updateChildren(updatesMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) result.invoke(Result.Success())
            }.addOnFailureListener { exception ->
                result.invoke(Result.Failure(exception.localizedMessage))
            }
    }

    override fun getCurrentUser(result: (Result<CurrentUser>) -> Unit) {
        var currentUserDatabase = firebaseDatabase.reference.child(USERS).child(firebaseAuth.currentUser?.uid.orEmpty())
        if (currentUserDatabase.key != firebaseAuth.currentUser?.uid.orEmpty()) currentUserDatabase =
            firebaseDatabase.reference.child(USERS).child(firebaseAuth.currentUser?.uid.orEmpty())
        currentUserDatabase.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
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
                            BLOCK_HIDDEN -> {
                                currentUser.isBlockHidden = snapshot.getValue(Boolean::class.java).isNotNull()
                            }
                        }
                    }
                    result.invoke(Result.Success(currentUser))
                }
            }.addOnFailureListener { exception ->
                result.invoke(Result.Failure(exception.localizedMessage))
            }
    }

    override fun deleteCurrentUser(result: (Result<Unit>) -> Unit) {
        firebaseDatabase.reference.child(USERS).child(firebaseAuth.currentUser?.uid.orEmpty()).removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) result.invoke(Result.Success())
            }.addOnFailureListener { exception ->
                Log.e("deleteAccTAG","RealDataBaseRepositoryImpl deleteCurrentUser exception ${exception.localizedMessage}")
                result.invoke(Result.Failure(exception.localizedMessage))
            }
    }

    override fun insertFilter(filter: Filter, result: (Result<Unit>) -> Unit) {
        firebaseDatabase.reference.child(USERS).child(firebaseAuth.currentUser?.uid.orEmpty()).child(FILTER_LIST).child(filter.filter).setValue(filter)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) result.invoke(Result.Success())
            }.addOnFailureListener { exception ->
                result.invoke(Result.Failure(exception.localizedMessage))
            }
    }

    override fun deleteFilterList(filterList: List<Filter?>, result: (Result<Unit>) -> Unit) {
        firebaseDatabase.reference.child(USERS).child(firebaseAuth.currentUser?.uid.orEmpty()).child(FILTER_LIST).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result.children.forEach { snapshot ->
                        if (filterList.map { it?.filter }.contains(snapshot.key)) {
                            snapshot.ref.removeValue()
                        }
                    }
                    result.invoke(Result.Success())
                }
            }.addOnFailureListener { exception ->
                result.invoke(Result.Failure(exception.localizedMessage))
            }
    }

    override fun insertFilteredCall(filteredCall: FilteredCall, result: (Result<Unit>) -> Unit) {
        firebaseDatabase.reference.child(USERS).child(firebaseAuth.currentUser?.uid.orEmpty()).child(FILTERED_CALL_LIST).child(filteredCall.callId.toString()).setValue(filteredCall)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) result.invoke(Result.Success())
            }.addOnFailureListener { exception ->
                result.invoke(Result.Failure(exception.localizedMessage))
            }
    }

    override fun deleteFilteredCallList(filteredCallIdList: List<String>, result: (Result<Unit>) -> Unit) {
        firebaseDatabase.reference.child(USERS).child(firebaseAuth.currentUser?.uid.orEmpty()).child(FILTERED_CALL_LIST).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result.children.forEach { snapshot ->
                        if (filteredCallIdList.contains(snapshot.key)) snapshot.ref.removeValue()
                    }
                    result.invoke(Result.Success())
                }
            }.addOnFailureListener { exception ->
                result.invoke(Result.Failure(exception.localizedMessage))
            }
    }

    override fun changeBlockHidden(blockUnanimous: Boolean, result: (Result<Unit>) -> Unit) {
        firebaseDatabase.reference.child(USERS).child(firebaseAuth.currentUser?.uid.orEmpty()).child(BLOCK_HIDDEN).setValue(blockUnanimous)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) result.invoke(Result.Success())
            }.addOnFailureListener { exception ->
                result.invoke(Result.Failure(exception.localizedMessage))
            }
    }

    override fun getPrivacyPolicy(appLang: String, result: (Result<String>) -> Unit) {
        firebaseDatabase.reference.child(PRIVACY_POLICY).child(appLang).get()
            .addOnCompleteListener { task ->
                result.invoke(Result.Success(task.result.value.toString()))
            }.addOnFailureListener { exception ->
                result.invoke(Result.Failure(exception.localizedMessage))
            }
    }

    override fun insertReview(review: Review, result: (Result<Unit>) -> Unit) {
        firebaseDatabase.reference.child(REVIEWS).child(review.time.toString()).setValue(review)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) result.invoke(Result.Success())
            }.addOnFailureListener { exception ->
                result.invoke(Result.Failure(exception.localizedMessage))
            }
    }
}