package com.tarasovvp.smartblocker.presentation.main.settings.settings_list

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.domain.models.Review
import com.tarasovvp.smartblocker.domain.usecase.settings.settings_list.SettingsListUseCase
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsListViewModel @Inject constructor(
    application: Application,
    private val settingsListUseCase: SettingsListUseCase
) : BaseViewModel(application) {

    val successReviewLiveData = MutableLiveData<String>()

    fun insertReview(review: Review) {
        showProgress()
        launch {
            settingsListUseCase.insertReview(review) {
                successReviewLiveData.postValue(review.message)
            }
            hideProgress()
        }
    }
}