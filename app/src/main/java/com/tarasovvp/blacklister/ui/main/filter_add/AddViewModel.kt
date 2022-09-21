package com.tarasovvp.blacklister.ui.main.filter_add

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.blacklister.extensions.isNotNull
import com.tarasovvp.blacklister.model.BlackFilter
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.model.Filter
import com.tarasovvp.blacklister.model.WhiteFilter
import com.tarasovvp.blacklister.repository.BlackFilterRepository
import com.tarasovvp.blacklister.repository.ContactRepository
import com.tarasovvp.blacklister.repository.WhiteFilterRepository
import com.tarasovvp.blacklister.ui.base.BaseViewModel

class AddViewModel(application: Application) : BaseViewModel(application) {

    private val blackFilterRepository = BlackFilterRepository
    private val whiteFilterRepository = WhiteFilterRepository
    private val contactRepository = ContactRepository

    val existFilterLiveData = MutableLiveData<Filter?>()
    val emptyFilterLiveData = MutableLiveData<Boolean>()
    val insertFilterLiveData = MutableLiveData<String>()
    val deleteFilterLiveData = MutableLiveData<String>()
    val queryContactListLiveData = MutableLiveData<List<Contact>>()

    fun checkFilterExist(filter: String, isBlackFilter: Boolean) {
        Log.e("filterAddTAG", "AddViewModel checkFilterExist filter $filter isBlackFilter $isBlackFilter")
        showProgress()
        launch {
            val existingFilter = if (isBlackFilter) {
                blackFilterRepository.getBlackFilter(filter)
            } else {
                whiteFilterRepository.getWhiteFilter(filter)
            }
            Log.e("filterAddTAG", "AddViewModel checkFilterExist existingFilter $existingFilter")
            if (existingFilter.isNotNull()) {
                existFilterLiveData.postValue(existingFilter)
            } else {
                emptyFilterLiveData.postValue(true)
            }
            hideProgress()
        }
    }

    fun checkContactListByFilter(filter: Filter) {
        showProgress()
        launch {
            queryContactListLiveData.postValue(contactRepository.getQueryContacts(filter).orEmpty())
            hideProgress()
        }
    }

    fun insertFilter(filter: Filter) {
        showProgress()
        launch {
            if (filter.isBlackFilter) {
                blackFilterRepository.insertBlackFilter(filter as BlackFilter) {
                    insertFilterLiveData.postValue(filter.filter)
                }
            } else {
                whiteFilterRepository.insertWhiteFilter(filter as WhiteFilter) {
                    insertFilterLiveData.postValue(filter.filter)
                }
            }
            hideProgress()
        }
    }

    fun deleteFilter(filter: Filter) {
        showProgress()
        launch {
            if (filter.isBlackFilter) {
                blackFilterRepository.deleteBlackFilter(filter as BlackFilter) {
                    deleteFilterLiveData.postValue(filter.filter)
                }
            } else {
                whiteFilterRepository.deleteWhiteFilter(filter as WhiteFilter) {
                    deleteFilterLiveData.postValue(filter.filter)
                }
            }
            hideProgress()
        }
    }

}