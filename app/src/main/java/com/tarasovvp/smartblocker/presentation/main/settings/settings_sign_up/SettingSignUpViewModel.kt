package com.tarasovvp.smartblocker.presentation.main.settings.settings_sign_up

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.entities.db_entities.FilteredCall
import com.tarasovvp.smartblocker.domain.entities.models.CurrentUser
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.SettingsSignUpUseCase
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import com.tarasovvp.smartblocker.utils.extensions.isNetworkAvailable
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingSignUpViewModel @Inject constructor(
    private val application: Application,
    private val settingsSignUpUseCase: SettingsSignUpUseCase
) : BaseViewModel(application) {

    val filtersLiveData = MutableLiveData<List<Filter>>()
    val filteredCallsLiveData = MutableLiveData<List<FilteredCall>>()
    val blockHiddenLiveData = MutableLiveData<Boolean>()
    val successSignUpLiveData = MutableLiveData<Unit>()
    val createCurrentUserLiveData = MutableLiveData<Unit>()

    fun getAllFilters() {
        launch {
            filtersLiveData.postValue(settingsSignUpUseCase.getAllFilters())
        }
    }

    fun getAllFilteredCalls() {
        launch {
            filteredCallsLiveData.postValue(settingsSignUpUseCase.getAllFilteredCalls())
        }
    }

    fun getBlockHidden() {
        launch {
            settingsSignUpUseCase.getBlockHidden().collect { blockHidden ->
                blockHiddenLiveData.postValue(blockHidden.isTrue())
            }
        }
    }

    fun createUserWithEmailAndPassword(email: String, password: String) {
        if (application.isNetworkAvailable()) {
            showProgress()
            settingsSignUpUseCase.createUserWithEmailAndPassword(email, password) { operationResult ->
                when(operationResult) {
                    is Result.Success -> successSignUpLiveData.postValue(Unit)
                    is Result.Failure -> operationResult.errorMessage?.let { exceptionLiveData.postValue(it) }
                }
                hideProgress()
            }
        } else {
            exceptionLiveData.postValue(application.getString(R.string.app_network_unavailable_repeat))
        }
    }

    fun createCurrentUser(currentUser: CurrentUser) {
        Log.e("blockHiddenTAG", "SettingSignUpViewModel createCurrentUser currentUser $currentUser")
        if (application.isNetworkAvailable()) {
            showProgress()
            settingsSignUpUseCase.createCurrentUser(currentUser) { operationResult ->
                when(operationResult) {
                    is Result.Success -> createCurrentUserLiveData.postValue(Unit)
                    is Result.Failure -> operationResult.errorMessage?.let { exceptionLiveData.postValue(it) }
                }
                hideProgress()
            }
        } else {
            exceptionLiveData.postValue(application.getString(R.string.app_network_unavailable_repeat))
        }
    }
}