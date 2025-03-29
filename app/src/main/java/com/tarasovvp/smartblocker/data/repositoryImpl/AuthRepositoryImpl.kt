package com.tarasovvp.smartblocker.data.repositoryImpl

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.tarasovvp.smartblocker.domain.repository.AuthRepository
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import javax.inject.Inject

class AuthRepositoryImpl
    @Inject
    constructor(
        private val firebaseAuth: FirebaseAuth,
        private val googleSignInClient: GoogleSignInClient,
    ) : AuthRepository {
        override fun sendPasswordResetEmail(
            email: String,
            result: (Result<Unit>) -> Unit,
        ) {
            firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    result.invoke(Result.Success())
                }.addOnFailureListener { exception ->
                    result.invoke(Result.Failure(exception.localizedMessage))
                }
        }

        override fun fetchSignInMethodsForEmail(
            email: String,
            result: (Result<List<String>>) -> Unit,
        ) {
            firebaseAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        result.invoke(Result.Success(task.result?.signInMethods))
                    }
                }.addOnFailureListener { exception ->
                    result.invoke(Result.Failure(exception.localizedMessage))
                }
        }

        override fun signInWithEmailAndPassword(
            email: String,
            password: String,
            result: (Result<Unit>) -> Unit,
        ) {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    result.invoke(Result.Success())
                }.addOnFailureListener { exception ->
                    result.invoke(Result.Failure(exception.localizedMessage))
                }
        }

        override fun signInWithGoogle(
            idToken: String,
            result: (Result<Unit>) -> Unit,
        ) {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener {
                    result.invoke(Result.Success())
                }.addOnFailureListener { exception ->
                    result.invoke(Result.Failure(exception.localizedMessage))
                }
        }

        override fun signInAnonymously(result: (Result<Unit>) -> Unit) {
            firebaseAuth.signInAnonymously()
                .addOnSuccessListener {
                    result.invoke(Result.Success())
                }.addOnFailureListener { exception ->
                    result.invoke(Result.Failure(exception.localizedMessage))
                }
        }

        override fun createUserWithEmailAndPassword(
            email: String,
            password: String,
            result: (Result<String>) -> Unit,
        ) {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) result.invoke(Result.Success(task.result.user?.uid.orEmpty()))
                }.addOnFailureListener { exception ->
                    result.invoke(Result.Failure(exception.localizedMessage))
                }
        }

        override fun changePassword(
            currentPassword: String,
            newPassword: String,
            result: (Result<Unit>) -> Unit,
        ) {
            val user = firebaseAuth.currentUser
            val credential = EmailAuthProvider.getCredential(user?.email.orEmpty(), currentPassword)
            user?.reauthenticateAndRetrieveData(credential)
                ?.addOnSuccessListener {
                    user.updatePassword(newPassword).addOnSuccessListener {
                        result.invoke(Result.Success())
                    }.addOnFailureListener { exception ->
                        result.invoke(Result.Failure(exception.localizedMessage))
                    }
                }?.addOnFailureListener { exception ->
                    result.invoke(Result.Failure(exception.localizedMessage))
                }
        }

        override fun reAuthenticate(
            authCredential: AuthCredential,
            result: (Result<Unit>) -> Unit,
        ) {
            firebaseAuth.currentUser?.reauthenticate(authCredential)
                ?.addOnSuccessListener {
                    result.invoke(Result.Success())
                }?.addOnFailureListener { exception ->
                    result.invoke(Result.Failure(exception.localizedMessage))
                }
        }

        override fun deleteUser(result: (Result<Unit>) -> Unit) {
            firebaseAuth.currentUser?.delete()
                ?.addOnSuccessListener {
                    signOut(result)
                }?.addOnFailureListener { exception ->
                    result.invoke(Result.Failure(exception.localizedMessage))
                }
        }

        override fun signOut(result: (Result<Unit>) -> Unit) {
            googleSignInClient.signOut().addOnSuccessListener {
                firebaseAuth.signOut()
                result.invoke(Result.Success())
            }.addOnFailureListener { exception ->
                result.invoke(Result.Failure(exception.localizedMessage))
            }
        }
    }
