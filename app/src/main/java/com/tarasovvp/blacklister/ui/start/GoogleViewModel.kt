package com.tarasovvp.blacklister.ui.start

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.GoogleAuthProvider
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.ui.base.BaseViewModel

open class GoogleViewModel(application: Application) : BaseViewModel(application) {

    val successSignInLiveData = MutableLiveData<Boolean>()

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