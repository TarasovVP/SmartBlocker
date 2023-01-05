package com.tarasovvp.smartblocker.ui.settings.settings_list

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.models.Review
import com.tarasovvp.smartblocker.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.ui.base.BaseViewModel

class SettingsListViewModel(application: Application) : BaseViewModel(application) {

    private val realDataBaseRepository = RealDataBaseRepository

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