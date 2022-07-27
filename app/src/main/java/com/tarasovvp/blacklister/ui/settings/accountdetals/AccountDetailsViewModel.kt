package com.tarasovvp.blacklister.ui.settings.accountdetals

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
        showProgress()
        authRepository.changePassword(password) {
            successChangePasswordLiveData.postValue(true)
            hideProgress()
        }
    }

    fun deleteUser() {
        showProgress()
        authRepository.deleteUser {
            successLiveData.postValue(true)
            hideProgress()
        }
    }

    fun renameUser(name: String) {
        showProgress()
        authRepository.renameUser(name) { displayName ->
            successRenameUserLiveData.postValue(displayName)
            hideProgress()
        }
    }
}