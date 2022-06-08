package com.tarasovvp.blacklister.ui.main.settings.accountdetals

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.ui.base.BaseViewModel


class AccountDetailsViewModel(application: Application) : BaseViewModel(application) {
    val successLiveData = MutableLiveData<Boolean>()
    val successRenameUserLiveData = MutableLiveData<String>()
    val successChangePasswordLiveData = MutableLiveData<Boolean>()

    fun changePassword(password: String) {
        val user = FirebaseAuth.getInstance().currentUser
        val credential = EmailAuthProvider.getCredential(user?.email.orEmpty(), password)
        user?.reauthenticate(credential)
            ?.addOnCompleteListener { changePasswordTask ->
                if (changePasswordTask.isSuccessful) {
                    successChangePasswordLiveData.postValue(true)
                } else {
                    exceptionLiveData.postValue(changePasswordTask.exception?.localizedMessage)
                }
            }
    }

    fun deleteUser() {
        FirebaseAuth.getInstance().currentUser?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                successLiveData.postValue(true)
            } else {
                exceptionLiveData.postValue(task.exception?.localizedMessage)
            }
        }
    }

    fun renameUser(name: String) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(name).build()

        FirebaseAuth.getInstance().currentUser?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { updateUserTask ->
                if (updateUserTask.isSuccessful) {
                    successRenameUserLiveData.postValue(BlackListerApp.instance?.auth?.currentUser?.displayName)
                } else {
                    exceptionLiveData.postValue(updateUserTask.exception?.localizedMessage)
                }
            }
    }
}