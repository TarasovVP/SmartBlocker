package com.tarasovvp.smartblocker.presentation.main.settings.settings_list

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.domain.entities.models.Review
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.SettingsListUseCase
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import com.tarasovvp.smartblocker.utils.extensions.isNetworkAvailable
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SettingsListViewModel @Inject constructor(
    private val application: Application,
    private val settingsListUseCase: SettingsListUseCase
) : BaseViewModel(application) {

    val appLanguageLiveData = MutableLiveData<String>()
    val successReviewLiveData = MutableLiveData<String>()

    fun getAppLanguage() {
        launch {
            settingsListUseCase.getAppLanguage().collect { appLang ->
                appLanguageLiveData.postValue(appLang ?: Locale.getDefault().language)
            }
        }
    }

    fun insertReview(review: Review) {
        if (application.isNetworkAvailable()) {
            showProgress()
            settingsListUseCase.insertReview(review) { result ->
                when (result) {
                    is Result.Success -> successReviewLiveData.postValue(review.message)
                    is Result.Failure -> exceptionLiveData.postValue(result.errorMessage)
                }
            }
            hideProgress()
        } else {
            exceptionLiveData.postValue(application.getString(R.string.app_network_unavailable_repeat))
        }
    }
}