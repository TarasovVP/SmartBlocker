package com.tarasovvp.blacklister.ui.start.signup

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.ui.base.BaseViewModel

class SignUpViewModel(application: Application) : BaseViewModel(application) {

    val successSignInLiveData = MutableLiveData<Boolean>()

    fun createUserWithEmailAndPassword(email: String, password: String, name: String) {
        BlackListerApp.instance?.auth?.createUserWithEmailAndPassword(email, password)
            ?.addOnCompleteListener { createUserTask ->
                if (createUserTask.isSuccessful) {
                    updateUser(createUserTask.result?.user, name)
                } else {
                    exceptionLiveData.postValue(createUserTask.exception?.localizedMessage)
                }
            }
    }

    private fun updateUser(currentUSer: FirebaseUser?, name: String) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(name).build()

        currentUSer?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { updateUserTask ->
                if (updateUserTask.isSuccessful) {
                    successSignInLiveData.postValue(true)
                } else {
                    exceptionLiveData.postValue(updateUserTask.exception?.localizedMessage)
                }
            }
    }
}