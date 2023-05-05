package com.tarasovvp.smartblocker.data.repositoryImpl

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.tarasovvp.smartblocker.domain.repository.AuthRepository
import com.tarasovvp.smartblocker.domain.sealed_classes.OperationResult
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val firebaseAuth: FirebaseAuth, private val googleSignInClient: GoogleSignInClient) : AuthRepository {

    override fun sendPasswordResetEmail(email: String, result: (OperationResult<Unit>) -> Unit) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                result.invoke(OperationResult.Success())
            }.addOnFailureListener { exception ->
                result.invoke(OperationResult.Failure(exception.localizedMessage))
            }
    }

    override fun signInWithEmailAndPassword(email: String, password: String, result: (OperationResult<Unit>) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                result.invoke(OperationResult.Success())
            }.addOnFailureListener { exception ->
                result.invoke(OperationResult.Failure(exception.localizedMessage))
            }
    }

    override fun signInWithGoogle(idToken: String, result: (OperationResult<Unit>) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener {
                result.invoke(OperationResult.Success())
            }.addOnFailureListener { exception ->
                result.invoke(OperationResult.Failure(exception.localizedMessage))
            }
    }

    override fun createUserWithEmailAndPassword(email: String, password: String, result: (OperationResult<Unit>) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                result.invoke(OperationResult.Success())
            }.addOnFailureListener { exception ->
                result.invoke(OperationResult.Failure(exception.localizedMessage))
            }
    }

    override fun changePassword(currentPassword: String, newPassword: String, result: (OperationResult<Unit>) -> Unit) {
        val user = firebaseAuth.currentUser
        val credential = EmailAuthProvider.getCredential(user?.email.orEmpty(), currentPassword)
        user?.reauthenticateAndRetrieveData(credential)
            ?.addOnCompleteListener {
                user.updatePassword(newPassword).addOnCompleteListener {
                    result.invoke(OperationResult.Success())
                }
            }?.addOnFailureListener { exception ->
                result.invoke(OperationResult.Failure(exception.localizedMessage))
            }
    }

    override fun deleteUser(result: (OperationResult<Unit>) -> Unit) {
        firebaseAuth.currentUser?.delete()
            ?.addOnCompleteListener {
                result.invoke(OperationResult.Success())
            }?.addOnFailureListener { exception ->
                result.invoke(OperationResult.Failure(exception.localizedMessage))
            }
    }

    override fun signOut(result: (OperationResult<Unit>) -> Unit) {
        googleSignInClient.signOut().addOnCompleteListener {
            firebaseAuth.signOut()
            result.invoke(OperationResult.Success())
        }.addOnFailureListener { exception ->
            result.invoke(OperationResult.Failure(exception.localizedMessage))
        }
    }
}