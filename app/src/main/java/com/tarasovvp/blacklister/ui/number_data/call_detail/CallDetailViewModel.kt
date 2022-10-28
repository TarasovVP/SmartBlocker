package com.tarasovvp.blacklister.ui.number_data.call_detail

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.blacklister.repository.BlockedCallRepository
import com.tarasovvp.blacklister.ui.base.BaseViewModel
import com.tarasovvp.blacklister.ui.number_data.NumberData

class CallDetailViewModel(application: Application) : BaseViewModel(application) {

    private val blockedCallRepository = BlockedCallRepository

    val callListLiveData = MutableLiveData<ArrayList<NumberData>>()

    fun blockedCallsByNumber(number: String) {
        showProgress()
        launch {
            val blackNumberList = blockedCallRepository.blockedCallsByNumber(number)
            blackNumberList?.let {
                callListLiveData.postValue(ArrayList(it))
            }
            hideProgress()
        }
    }

    fun blockedCallsByFilter(filter: String) {
        showProgress()
        launch {
            val blackNumberList = blockedCallRepository.blockedCallsByFilter(filter)
            blackNumberList?.let {
                callListLiveData.postValue(ArrayList(it))
            }
            hideProgress()
        }
    }
}