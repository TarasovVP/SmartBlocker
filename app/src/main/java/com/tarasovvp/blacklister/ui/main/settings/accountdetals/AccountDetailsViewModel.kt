package com.tarasovvp.blacklister.ui.main.settings.accountdetals

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.blacklister.repository.AuthRepository
import com.tarasovvp.blacklister.ui.base.BaseViewModel

class AccountDetailsViewModel(application: Application) : BaseViewModel(application) {

    private val authRepository = AuthRepository

    val successLiveData = MutableLiveData<Boolean>()
    val successRenameUserLiveData = MutableLiveData<String>()
    val successChangePasswordLiveData = MutableLiveData<Boolean>()

    fun changePassword(password: String) {
        authRepository.changePassword(password) {
            successChangePasswordLiveData.postValue(true)
        }
    }

    fun deleteUser() {
        authRepository.deleteUser {
            successLiveData.postValue(true)
        }
    }

    fun renameUser(name: String) {
        authRepository.renameUser(name) { displayName ->
            successRenameUserLiveData.postValue(displayName)
        }
    }
}