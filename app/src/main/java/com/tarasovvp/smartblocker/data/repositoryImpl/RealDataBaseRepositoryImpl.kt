package com.tarasovvp.smartblocker.data.repositoryImpl

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.entities.db_entities.FilteredCall
import com.tarasovvp.smartblocker.domain.entities.models.CurrentUser
import com.tarasovvp.smartblocker.domain.entities.models.Feedback
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCK_HIDDEN
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCK_TURN_ON
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.COUNTRY_CODE
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.FEEDBACK
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.FILTERED_CALL_LIST
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.FILTER_LIST
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.PRIVACY_POLICY
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.REVIEW_VOTE
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.USERS
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import javax.inject.Inject

class RealDataBaseRepositoryImpl
    @Inject
    constructor(
        private val firebaseDatabase: FirebaseDatabase,
        private val firebaseAuth: FirebaseAuth,
    ) :
    RealDataBaseRepository {
        override fun createCurrentUser(
            currentUser: CurrentUser,
            result: (Result<Unit>) -> Unit,
        ) {
            val currentUserMap = hashMapOf<String, Any>()
            currentUser.filterList.forEach { filter ->
                currentUserMap["$FILTER_LIST/${filter.filter}"] = filter
            }
            currentUser.filteredCallList.forEach { filteredCall ->
                currentUserMap["$FILTERED_CALL_LIST/${filteredCall.callId}"] = filteredCall
            }
            currentUserMap[BLOCK_TURN_ON] = currentUser.isBlockerTurnOn
            currentUserMap[BLOCK_HIDDEN] = currentUser.isBlockHidden
            currentUserMap[COUNTRY_CODE] = currentUser.countryCode
            firebaseDatabase.reference.child(USERS).child(firebaseAuth.currentUser?.uid.orEmpty())
                .setValue(currentUser)
                .addOnSuccessListener {
                    result.invoke(Result.Success())
                }.addOnFailureListener { exception ->
                    result.invoke(Result.Failure(exception.localizedMessage))
                }
        }

        override fun updateCurrentUser(
            currentUser: CurrentUser,
            result: (Result<Unit>) -> Unit,
        ) {
            val updatesMap = hashMapOf<String, Any>()
            currentUser.filterList.forEach { filter ->
                updatesMap["$FILTER_LIST/${filter.filter}"] = filter
            }
            currentUser.filteredCallList.forEach { filteredCall ->
                updatesMap["$FILTERED_CALL_LIST/${filteredCall.callId}"] = filteredCall
            }
            updatesMap[BLOCK_TURN_ON] = currentUser.isBlockerTurnOn
            updatesMap[BLOCK_HIDDEN] = currentUser.isBlockHidden
            updatesMap[COUNTRY_CODE] = currentUser.countryCode
            firebaseDatabase.reference.child(USERS).child(firebaseAuth.currentUser?.uid.orEmpty())
                .updateChildren(updatesMap)
                .addOnSuccessListener {
                    result.invoke(Result.Success())
                }.addOnFailureListener { exception ->
                    result.invoke(Result.Failure(exception.localizedMessage))
                }
        }

        override fun getCurrentUser(result: (Result<CurrentUser>) -> Unit) {
            var currentUserDatabase =
                firebaseDatabase.reference.child(USERS).child(firebaseAuth.currentUser?.uid.orEmpty())
            if (currentUserDatabase.key != firebaseAuth.currentUser?.uid.orEmpty()) {
                currentUserDatabase =
                    firebaseDatabase.reference.child(USERS).child(firebaseAuth.currentUser?.uid.orEmpty())
            }
            currentUserDatabase.get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val currentUser = CurrentUser()
                        task.result.children.forEach { snapshot ->
                            when (snapshot.key) {
                                FILTER_LIST ->
                                    snapshot.children.forEach { child ->
                                        child.getValue(Filter::class.java)
                                            ?.let { currentUser.filterList.add(it) }
                                    }

                                FILTERED_CALL_LIST ->
                                    snapshot.children.forEach { child ->
                                        child.getValue(FilteredCall::class.java)
                                            ?.let { currentUser.filteredCallList.add(it) }
                                    }

                                BLOCK_TURN_ON ->
                                    currentUser.isBlockerTurnOn =
                                        snapshot.getValue(Boolean::class.java).isTrue()

                                BLOCK_HIDDEN ->
                                    currentUser.isBlockHidden =
                                        snapshot.getValue(Boolean::class.java).isTrue()

                                COUNTRY_CODE ->
                                    currentUser.countryCode =
                                        snapshot.getValue(CountryCode::class.java) ?: CountryCode()

                                REVIEW_VOTE ->
                                    currentUser.isReviewVoted =
                                        snapshot.getValue(Boolean::class.java).isTrue()
                            }
                        }
                        result.invoke(Result.Success(currentUser))
                    }
                }.addOnFailureListener { exception ->
                    result.invoke(Result.Failure(exception.localizedMessage))
                }
        }

        override fun deleteCurrentUser(result: (Result<Unit>) -> Unit) {
            firebaseDatabase.reference.child(USERS).child(firebaseAuth.currentUser?.uid.orEmpty())
                .removeValue()
                .addOnSuccessListener {
                    result.invoke(Result.Success())
                }.addOnFailureListener { exception ->
                    result.invoke(Result.Failure(exception.localizedMessage))
                }
        }

        override fun insertFilter(
            filter: Filter,
            result: (Result<Unit>) -> Unit,
        ) {
            firebaseDatabase.reference.child(USERS).child(firebaseAuth.currentUser?.uid.orEmpty())
                .child(FILTER_LIST).child(filter.filter).setValue(filter)
                .addOnSuccessListener {
                    result.invoke(Result.Success())
                }.addOnFailureListener { exception ->
                    result.invoke(Result.Failure(exception.localizedMessage))
                }
        }

        override fun deleteFilterList(
            filterList: List<Filter?>,
            result: (Result<Unit>) -> Unit,
        ) {
            firebaseDatabase.reference.child(USERS).child(firebaseAuth.currentUser?.uid.orEmpty())
                .child(FILTER_LIST).get()
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

        override fun insertFilteredCall(
            filteredCall: FilteredCall,
            result: (Result<Unit>) -> Unit,
        ) {
            firebaseDatabase.reference.child(USERS).child(firebaseAuth.currentUser?.uid.orEmpty())
                .child(FILTERED_CALL_LIST).child(filteredCall.callId.toString()).setValue(filteredCall)
                .addOnSuccessListener {
                    result.invoke(Result.Success())
                }.addOnFailureListener { exception ->
                    result.invoke(Result.Failure(exception.localizedMessage))
                }
        }

        override fun deleteFilteredCallList(
            filteredCallIdList: List<String>,
            result: (Result<Unit>) -> Unit,
        ) {
            firebaseDatabase.reference.child(USERS).child(firebaseAuth.currentUser?.uid.orEmpty())
                .child(FILTERED_CALL_LIST).get()
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

        override fun changeBlockTurnOn(
            blockTurnOn: Boolean,
            result: (Result<Unit>) -> Unit,
        ) {
            firebaseDatabase.reference.child(USERS).child(firebaseAuth.currentUser?.uid.orEmpty())
                .child(BLOCK_TURN_ON).setValue(blockTurnOn)
                .addOnSuccessListener {
                    result.invoke(Result.Success())
                }.addOnFailureListener { exception ->
                    result.invoke(Result.Failure(exception.localizedMessage))
                }
        }

        override fun changeBlockHidden(
            blockHidden: Boolean,
            result: (Result<Unit>) -> Unit,
        ) {
            firebaseDatabase.reference.child(USERS).child(firebaseAuth.currentUser?.uid.orEmpty())
                .child(BLOCK_HIDDEN).setValue(blockHidden)
                .addOnSuccessListener {
                    result.invoke(Result.Success())
                }.addOnFailureListener { exception ->
                    result.invoke(Result.Failure(exception.localizedMessage))
                }
        }

        override fun changeCountryCode(
            countryCode: CountryCode,
            result: (Result<Unit>) -> Unit,
        ) {
            Log.e("filteredCallTAG", "CallReceiver changeCountryCode countryCode $countryCode")
            firebaseDatabase.reference.child(USERS).child(firebaseAuth.currentUser?.uid.orEmpty())
                .child(COUNTRY_CODE).setValue(countryCode)
                .addOnSuccessListener {
                    result.invoke(Result.Success())
                }.addOnFailureListener { exception ->
                    result.invoke(Result.Failure(exception.localizedMessage))
                }
        }

        override fun setReviewVoted(result: (Result<Unit>) -> Unit) {
            firebaseDatabase.reference.child(USERS).child(firebaseAuth.currentUser?.uid.orEmpty())
                .child(REVIEW_VOTE).setValue(true)
                .addOnSuccessListener {
                    result.invoke(Result.Success())
                }.addOnFailureListener { exception ->
                    result.invoke(Result.Failure(exception.localizedMessage))
                }
        }

        override fun getPrivacyPolicy(
            appLang: String,
            result: (Result<String>) -> Unit,
        ) {
            firebaseDatabase.reference.child(PRIVACY_POLICY).child(appLang).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) result.invoke(Result.Success(task.result.value as? String))
                }.addOnFailureListener { exception ->
                    result.invoke(Result.Failure(exception.localizedMessage))
                }
        }

        override fun insertFeedback(
            feedback: Feedback,
            result: (Result<Unit>) -> Unit,
        ) {
            firebaseDatabase.reference.child(FEEDBACK).child(feedback.time.toString())
                .setValue(feedback)
                .addOnSuccessListener {
                    result.invoke(Result.Success())
                }.addOnFailureListener { exception ->
                    result.invoke(Result.Failure(exception.localizedMessage))
                }
        }
    }
