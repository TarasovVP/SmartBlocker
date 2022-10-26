package com.tarasovvp.blacklister.ui.main.number_detail

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.blacklister.repository.FilterRepository
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.ui.base.BaseViewModel

class NumberDetailViewModel(application: Application) : BaseViewModel(application) {

    private val filterRepository = FilterRepository

    val filterListLiveData = MutableLiveData<ArrayList<BaseAdapter.NumberData>>()

    fun filterListWithNumber(number: String) {
        showProgress()
        launch {
            val blackNumberList = filterRepository.queryFilterList(number)
            blackNumberList?.let {
                filterListLiveData.postValue(ArrayList(it))
            }
            hideProgress()
        }
    }
}