package com.tarasovvp.blacklister.ui.number_data.blocker_calls_detail

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.blacklister.model.Filter
import com.tarasovvp.blacklister.repository.BlockedCallRepository
import com.tarasovvp.blacklister.ui.base.BaseViewModel
import com.tarasovvp.blacklister.model.NumberData

class BlockerCallsDetailViewModel(application: Application) : BaseViewModel(application) {

    private val blockedCallRepository = BlockedCallRepository

    val callListLiveData = MutableLiveData<ArrayList<NumberData>>()

    fun blockedCallsByNumber(number: String) {
        showProgress()
        launch {
            val blackNumberList = blockedCallRepository.blockedCallsByNumber(number)
            blackNumberList?.let { blockedCalls ->
                callListLiveData.postValue(ArrayList(blockedCalls.sortedByDescending { it.callDate }))
            }
            hideProgress()
        }
    }

    fun blockedCallsByFilter(filter: Filter) {
        showProgress()
        launch {
            val blackNumberList = blockedCallRepository.blockedCallsByFilter(filter)
            blackNumberList?.let { blockedCalls ->
                callListLiveData.postValue(ArrayList(blockedCalls.sortedByDescending { it.callDate }))
            }
            hideProgress()
        }
    }
}