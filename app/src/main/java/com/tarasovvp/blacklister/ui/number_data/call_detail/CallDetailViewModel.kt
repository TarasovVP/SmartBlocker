package com.tarasovvp.blacklister.ui.number_data.call_detail

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.blacklister.repository.FilterRepository
import com.tarasovvp.blacklister.ui.base.BaseViewModel
import com.tarasovvp.blacklister.ui.number_data.NumberData

class CallDetailViewModel(application: Application) : BaseViewModel(application) {

    private val filterRepository = FilterRepository

    val filterListLiveData = MutableLiveData<ArrayList<NumberData>>()

    fun filterListWithCall(phone: String) {
        showProgress()
        launch {
            val blackNumberList = filterRepository.queryFilterList(phone)
            blackNumberList?.let {
                filterListLiveData.postValue(ArrayList(it))
            }
            hideProgress()
        }
    }
}