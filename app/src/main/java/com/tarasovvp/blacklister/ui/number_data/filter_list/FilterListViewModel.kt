package com.tarasovvp.blacklister.ui.number_data.filter_list

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.blacklister.constants.Constants.BLACK_FILTER
import com.tarasovvp.blacklister.constants.Constants.WHITE_FILTER
import com.tarasovvp.blacklister.model.Filter
import com.tarasovvp.blacklister.repository.FilterRepository
import com.tarasovvp.blacklister.ui.base.BaseViewModel

class FilterListViewModel(application: Application) : BaseViewModel(application) {

    val filterListLiveData = MutableLiveData<ArrayList<Filter>?>()
    val successDeleteFilterLiveData = MutableLiveData<Boolean>()

    private val filterRepository = FilterRepository

    val filterHashMapLiveData = MutableLiveData<Map<String, List<Filter>>?>()

    fun getFilterList(isBlackList: Boolean) {
        showProgress()
        launch {
            val filterArrayList =
                filterRepository.allFiltersByType(if (isBlackList) BLACK_FILTER else WHITE_FILTER) as ArrayList
            filterListLiveData.postValue(filterArrayList)
            hideProgress()
        }
    }

    fun getHashMapFromFilterList(filterList: List<Filter>) {
        launch {
            showProgress()
            filterHashMapLiveData.postValue(
                filterRepository.getHashMapFromFilterList(filterList)
            )
            hideProgress()
        }
    }

    fun deleteFilterList(filterList: List<Filter>) {
        showProgress()
        launch {
            filterRepository.deleteFilterList(filterList) {
                successDeleteFilterLiveData.postValue(true)
            }
            hideProgress()
        }
    }
}