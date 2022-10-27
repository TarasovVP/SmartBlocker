package com.tarasovvp.blacklister.ui.number_data.filter_detail

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.blacklister.model.Filter
import com.tarasovvp.blacklister.repository.ContactRepository
import com.tarasovvp.blacklister.repository.FilterRepository
import com.tarasovvp.blacklister.ui.base.BaseViewModel
import com.tarasovvp.blacklister.ui.number_data.NumberData

class FilterDetailViewModel(application: Application) : BaseViewModel(application) {

    private val contactRepository = ContactRepository
    private val filterRepository = FilterRepository

    val contactListLiveData = MutableLiveData<ArrayList<NumberData>>()
    val filterActionLiveData = MutableLiveData<Filter>()

    fun getQueryContactList(filter: Filter) {
        Log.e("filterAddTAG", "AddViewModel checkContactListByFilter filter $filter")
        showProgress()
        launch {
            contactRepository.getQueryContactList(filter)?.let {
                contactListLiveData.postValue(ArrayList(it))
            }
            Log.e("filterAddTAG", "AddViewModel queryContactListLiveData $filter")
            hideProgress()
        }
    }

    fun deleteFilter(filter: Filter) {
        showProgress()
        launch {
            Log.e("filterAddTAG",
                "AddViewModel deleteFilter filter $filter")
            filterRepository.deleteFilterList(listOf(filter)) {
                filterActionLiveData.postValue(filter)
            }
            hideProgress()
        }
    }

    fun updateFilter(filter: Filter) {
        showProgress()
        launch {
            filterRepository.updateFilter(filter) {
                filterActionLiveData.postValue(filter)
            }
            hideProgress()
        }
    }
}