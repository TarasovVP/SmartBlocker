package com.tarasovvp.smartblocker.presentation.main.settings.settings_blocker

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.domain.usecase.SettingsBlockerUseCase
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import com.tarasovvp.smartblocker.utils.extensions.isNotNull
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsBlockerViewModel @Inject constructor(
    private val application: Application,
    private val settingsBlockerUseCase: SettingsBlockerUseCase,
    private val firebaseAuth: FirebaseAuth
) : BaseViewModel(application) {

    val successBlockHiddenLiveData = MutableLiveData<Boolean>()

    fun changeBlockHidden(blockHidden: Boolean) {
        showProgress()
        if (firebaseAuth.currentUser.isNotNull()) {
            if ((application as? SmartBlockerApp)?.isNetworkAvailable.isTrue()) {
                settingsBlockerUseCase.changeBlockHidden(blockHidden) {
                    successBlockHiddenLiveData.postValue(blockHidden.not())
                }
            } else {
                exceptionLiveData.postValue(application.getString(R.string.app_network_unavailable_repeat))
            }
        } else {
            successBlockHiddenLiveData.postValue(blockHidden)
        }
        hideProgress()
    }
}