package com.tarasovvp.smartblocker.ui.main.settings.settings_list

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.models.Review
import com.tarasovvp.smartblocker.repository.interfaces.RealDataBaseRepository
import com.tarasovvp.smartblocker.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsListViewModel @Inject constructor(
    application: Application,
    private val realDataBaseRepository: RealDataBaseRepository
) : BaseViewModel(application) {

    val successReviewLiveData = MutableLiveData<String>()

    fun insertReview(review: Review) {
        showProgress()
        launch {
            realDataBaseRepository.insertReview(review) {
                successReviewLiveData.postValue(review.message)
            }
            hideProgress()
        }
    }
}