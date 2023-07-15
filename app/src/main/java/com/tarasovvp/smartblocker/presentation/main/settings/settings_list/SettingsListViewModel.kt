package com.tarasovvp.smartblocker.presentation.main.settings.settings_list

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.domain.entities.models.Feedback
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.SettingsListUseCase
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import com.tarasovvp.smartblocker.utils.extensions.isNetworkAvailable
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SettingsListViewModel @Inject constructor(
    private val application: Application,
    private val settingsListUseCase: SettingsListUseCase
) : BaseViewModel(application) {

    val appLanguageLiveData = MutableLiveData<String>()
    val successFeedbackLiveData = MutableLiveData<String>()

    fun getAppLanguage() {
        launch {
            settingsListUseCase.getAppLanguage().collect { appLang ->
                appLanguageLiveData.postValue(appLang ?: Locale.getDefault().language)
            }
        }
    }

    fun insertFeedback(feedback: Feedback) {
        if (application.isNetworkAvailable()) {
            showProgress()
            settingsListUseCase.insertFeedback(feedback) { result ->
                when (result) {
                    is Result.Success -> successFeedbackLiveData.postValue(feedback.message)
                    is Result.Failure -> exceptionLiveData.postValue(result.errorMessage.orEmpty())
                }
            }
            hideProgress()
        } else {
            exceptionLiveData.postValue(application.getString(R.string.app_network_unavailable_repeat))
        }
    }
}