package com.tarasovvp.blacklister.ui.main.filter_detail

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.blacklister.model.Filter
import com.tarasovvp.blacklister.repository.ContactRepository
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.ui.base.BaseViewModel

class FilterDetailViewModel(application: Application) : BaseViewModel(application) {

    private val contactRepository = ContactRepository

    val contactListLiveData = MutableLiveData<ArrayList<BaseAdapter.MainData>>()

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
}