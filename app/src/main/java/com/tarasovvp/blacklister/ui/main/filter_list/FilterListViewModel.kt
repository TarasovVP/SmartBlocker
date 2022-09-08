package com.tarasovvp.blacklister.ui.main.filter_list

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.blacklister.model.BlackFilter
import com.tarasovvp.blacklister.model.Filter
import com.tarasovvp.blacklister.model.WhiteFilter
import com.tarasovvp.blacklister.repository.BlackFilterRepository
import com.tarasovvp.blacklister.repository.WhiteFilterRepository
import com.tarasovvp.blacklister.ui.base.BaseViewModel

class FilterListViewModel(application: Application) : BaseViewModel(application) {

    val filterListLiveData = MutableLiveData<List<Filter>?>()
    val successDeleteFilterLiveData = MutableLiveData<Boolean>()

    private val whiteFilterRepository = WhiteFilterRepository
    private val blackFilterRepository = BlackFilterRepository

    val filterHashMapLiveData = MutableLiveData<Map<String, List<Filter>>?>()

    fun getFilterList(isBlackList: Boolean) {
        launch {
            showProgress()
            val filterList = if (isBlackList) {
                blackFilterRepository.allBlackFilters()
            } else {
                whiteFilterRepository.allWhiteFilters()
            }
            filterListLiveData.postValue(filterList)
            hideProgress()
        }
    }

    fun getHashMapFromFilterList(filterList: List<Filter>) {
        launch {
            showProgress()
            filterHashMapLiveData.postValue(
                whiteFilterRepository.getHashMapFromFilterList(filterList)
            )
            hideProgress()
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun deleteFilterList(filterList: List<Filter>, isBlackList: Boolean) {
        showProgress()
        launch {
            if (isBlackList) {
                blackFilterRepository.deleteBlackFilterList(blackFilterList = filterList as List<BlackFilter>) {
                    successDeleteFilterLiveData.postValue(true)
                }
            } else {
                whiteFilterRepository.deleteWhiteFilterList(filterList as List<WhiteFilter>) {
                    successDeleteFilterLiveData.postValue(true)
                }
            }
            hideProgress()
        }
    }
}