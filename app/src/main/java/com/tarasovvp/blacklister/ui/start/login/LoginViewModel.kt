package com.tarasovvp.blacklister.ui.start.login

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.GoogleAuthProvider
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.ui.base.BaseViewModel

class LoginViewModel(application: Application) : BaseViewModel(application) {

    val successPasswordResetLiveData = MutableLiveData<Boolean>()
    val successSignInLiveData = MutableLiveData<Boolean>()

    fun sendPasswordResetEmail(email: String) {
        BlackListerApp.instance?.auth?.sendPasswordResetEmail(email)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    successPasswordResetLiveData.postValue(true)
                } else {
                    exceptionLiveData.postValue(task.exception?.localizedMessage)
                }
            }
    }

    fun signInWithEmailAndPassword(email: String, password: String) {
        BlackListerApp.instance?.auth?.signInWithEmailAndPassword(email, password)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    successSignInLiveData.postValue(true)
                } else {
                    exceptionLiveData.postValue(task.exception?.localizedMessage)
                }

            }
    }

    fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        BlackListerApp.instance?.auth?.signInWithCredential(credential)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    successSignInLiveData.postValue(task.isSuccessful)
                } else {
                    exceptionLiveData.postValue(task.exception?.localizedMessage)
                }
            }
    }
}