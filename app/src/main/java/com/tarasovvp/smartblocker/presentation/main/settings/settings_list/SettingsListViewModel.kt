package com.tarasovvp.smartblocker.presentation.main.settings.settings_list

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.domain.models.Review
import com.tarasovvp.smartblocker.domain.usecase.SettingsListUseCase
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsListViewModel @Inject constructor(
    private val application: Application,
    private val settingsListUseCase: SettingsListUseCase
) : BaseViewModel(application) {

    val successReviewLiveData = MutableLiveData<String>()

    fun insertReview(review: Review) {
        if ((application as? SmartBlockerApp)?.isNetworkAvailable.isTrue()) {
            showProgress()
            settingsListUseCase.insertReview(review) {
                successReviewLiveData.postValue(review.message)
            }
            hideProgress()
        } else {
            exceptionLiveData.postValue(application.getString(R.string.app_network_unavailable_repeat))
        }
    }
}