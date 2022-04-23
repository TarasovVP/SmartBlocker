package com.tarasovvp.blacklister.ui.start

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.GoogleAuthProvider
import com.tarasovvp.blacklister.BlackListerApp
import kotlinx.coroutines.launch

open class GoogleViewModel(application: Application) : AndroidViewModel(application) {

    val successSignInLiveData = MutableLiveData<Boolean>()
    val exceptionLiveData = MutableLiveData<String>()

    fun firebaseAuthWithGoogle(idToken: String) {
        viewModelScope.launch {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            BlackListerApp.instance?.auth?.signInWithCredential(credential)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.e("signUserTAG",
                            "LoginViewModel firebaseAuthWithGoogle task.isSuccessful ${task.isSuccessful}")
                        successSignInLiveData.postValue(task.isSuccessful)
                    } else {
                        exceptionLiveData.postValue(task.exception.toString())
                        Log.e("signUserTAG",
                            "LoginViewModel firebaseAuthWithGoogle task.exception ${task.exception}")
                    }
                }
        }
    }

}