package com.tarasovvp.blacklister.ui.main.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.tarasovvp.blacklister.BlackListerApp
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {
    val successLiveData = MutableLiveData<Boolean>()
    val successRenameUserLiveData = MutableLiveData<String>()
    val exceptionLiveData = MutableLiveData<String>()

    fun signOut() {
        viewModelScope.launch {
            BlackListerApp.instance?.auth?.signOut()
            successLiveData.postValue(true)
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