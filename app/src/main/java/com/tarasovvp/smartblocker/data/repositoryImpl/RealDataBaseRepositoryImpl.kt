package com.tarasovvp.smartblocker.data.repositoryImpl

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.domain.models.entities.FilteredCall
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCK_HIDDEN
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.FILTERED_CALL_LIST
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.FILTER_LIST
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.REVIEWS
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.USERS
import com.tarasovvp.smartblocker.domain.models.entities.CurrentUser
import com.tarasovvp.smartblocker.domain.models.Review
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.sealed_classes.OperationResult
import javax.inject.Inject

class RealDataBaseRepositoryImpl @Inject constructor(private val firebaseDatabase: FirebaseDatabase, private val firebaseAuth: FirebaseAuth) :
    RealDataBaseRepository {

    override fun getCurrentUser(result: (OperationResult<CurrentUser>) -> Unit) {
        var currentUserDatabase = firebaseDatabase.reference.child(USERS).child(firebaseAuth.currentUser?.uid.orEmpty())
        if (currentUserDatabase.key != firebaseAuth.currentUser?.uid.orEmpty()) currentUserDatabase =
            firebaseDatabase.reference.child(USERS).child(firebaseAuth.currentUser?.uid.orEmpty())
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
                result.invoke(OperationResult.Success(currentUser))
            }.addOnFailureListener { exception ->
                result.invoke(OperationResult.Failure(exception.localizedMessage))
            }
    }

    override fun insertFilter(filter: Filter, result: (OperationResult<Unit>) -> Unit) {
        firebaseDatabase.reference.child(USERS).child(firebaseAuth.currentUser?.uid.orEmpty()).child(FILTER_LIST).child(filter.filter).setValue(filter)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) result.invoke(OperationResult.Success())
            }.addOnFailureListener { exception ->
                result.invoke(OperationResult.Failure(exception.localizedMessage))
            }
    }

    override fun deleteFilterList(filterList: List<Filter?>, result: (OperationResult<Unit>) -> Unit) {
        firebaseDatabase.reference.child(USERS).child(firebaseAuth.currentUser?.uid.orEmpty()).child(FILTER_LIST).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result.children.forEach { snapshot ->
                        if (filterList.map { it?.filter }.contains(snapshot.key)) {
                            snapshot.ref.removeValue()
                        }
                    }
                    result.invoke(OperationResult.Success())
                } else {
                    result.invoke(OperationResult.Failure(task.exception?.localizedMessage))
                }
            }.addOnFailureListener { exception ->
                result.invoke(OperationResult.Failure(exception.localizedMessage))
            }
    }

    override fun insertFilteredCall(filteredCall: FilteredCall, result: (OperationResult<Unit>) -> Unit) {
        firebaseDatabase.reference.child(USERS).child(firebaseAuth.currentUser?.uid.orEmpty()).child(FILTERED_CALL_LIST).child(filteredCall.callId.toString()).setValue(filteredCall)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) result.invoke(OperationResult.Success())
            }.addOnFailureListener { exception ->
                result.invoke(OperationResult.Failure(exception.localizedMessage))
            }
    }

    override fun deleteFilteredCallList(filteredCallIdList: List<String>, result: (OperationResult<Unit>) -> Unit) {
        firebaseDatabase.reference.child(USERS).child(firebaseAuth.currentUser?.uid.orEmpty()).child(FILTERED_CALL_LIST).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result.children.forEach { snapshot ->
                        if (filteredCallIdList.contains(snapshot.key)) snapshot.ref.removeValue()
                    }
                    result.invoke(OperationResult.Success())
                } else {
                    result.invoke(OperationResult.Failure(task.exception?.localizedMessage))
                }
            }.addOnFailureListener { exception ->
                result.invoke(OperationResult.Failure(exception.localizedMessage))
            }
    }

    override fun changeBlockHidden(blockUnanimous: Boolean, result: (OperationResult<Unit>) -> Unit) {
        firebaseDatabase.reference.child(USERS).child(firebaseAuth.currentUser?.uid.orEmpty()).child(BLOCK_HIDDEN).setValue(blockUnanimous)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) result.invoke(OperationResult.Success())
            }.addOnFailureListener { exception ->
                result.invoke(OperationResult.Failure(exception.localizedMessage))
            }
    }

    override fun insertReview(review: Review, result: (OperationResult<Unit>) -> Unit) {
        firebaseDatabase.reference.child(REVIEWS).child(review.time.toString()).setValue(review)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) result.invoke(OperationResult.Success())
            }.addOnFailureListener { exception ->
                result.invoke(OperationResult.Failure(exception.localizedMessage))
            }
    }
}