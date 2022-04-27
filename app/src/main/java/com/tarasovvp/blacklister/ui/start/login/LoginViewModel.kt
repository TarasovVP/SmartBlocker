package com.tarasovvp.blacklister.ui.start.login

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.ui.start.GoogleViewModel

class LoginViewModel(application: Application) : GoogleViewModel(application) {

    val successPasswordResetLiveData = MutableLiveData<Boolean>()

    fun sendPasswordResetEmail(email: String) {
        BlackListerApp.instance?.auth?.sendPasswordResetEmail(email)
            ?.addOnCompleteListener() { task ->
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

}