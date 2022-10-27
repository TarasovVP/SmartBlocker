package com.tarasovvp.blacklister.ui.number_data.number_data_detail

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.blacklister.repository.FilterRepository
import com.tarasovvp.blacklister.ui.base.BaseViewModel
import com.tarasovvp.blacklister.ui.number_data.NumberData

class NumberDataDetailViewModel(application: Application) : BaseViewModel(application) {

    private val filterRepository = FilterRepository

    val filterListLiveData = MutableLiveData<ArrayList<NumberData>>()

    fun filterListWithNumber(number: String) {
        showProgress()
        launch {
            val filterList = filterRepository.queryFilterList(number)
            filterList?.let {
                filterListLiveData.postValue(ArrayList(it))
            }
            hideProgress()
        }
    }
}