package com.tarasovvp.blacklister.ui.main.contact_detail

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.blacklister.repository.FilterRepository
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.ui.base.BaseViewModel

class ContactDetailViewModel(application: Application) : BaseViewModel(application) {

    private val filterRepository = FilterRepository

    val filterListLiveData = MutableLiveData<ArrayList<BaseAdapter.NumberData>>()

    fun filterListWithContact(phone: String) {
        showProgress()
        launch {
            val blackNumberList = filterRepository.getFilterList(phone)
            blackNumberList?.let {
                filterListLiveData.postValue(ArrayList(it))
            }
            hideProgress()
        }
    }
}