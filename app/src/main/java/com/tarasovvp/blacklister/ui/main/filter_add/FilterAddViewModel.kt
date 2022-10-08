package com.tarasovvp.blacklister.ui.main.filter_add

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.model.CountryCode
import com.tarasovvp.blacklister.model.Filter
import com.tarasovvp.blacklister.repository.ContactRepository
import com.tarasovvp.blacklister.repository.CountryCodeRepository
import com.tarasovvp.blacklister.repository.FilterRepository
import com.tarasovvp.blacklister.ui.base.BaseViewModel

class FilterAddViewModel(application: Application) : BaseViewModel(application) {

    private val filterRepository = FilterRepository
    private val contactRepository = ContactRepository
    private val countryCodeRepository = CountryCodeRepository

    val existFilterLiveData = MutableLiveData<Filter>()
    val insertFilterLiveData = MutableLiveData<String>()
    val deleteFilterLiveData = MutableLiveData<String>()
    val countryCodeLiveData = MutableLiveData<List<CountryCode>>()
    val queryContactListLiveData = MutableLiveData<List<Contact>>()
    val contactLiveData = MutableLiveData<List<Contact>>()

    fun checkFilterExist(filter: Filter) {
        Log.e("filterAddTAG", "AddViewModel checkFilterExist filter $filter")
        showProgress()
        launch {
            val existingFilter = filterRepository.getFilter(filter)
            Log.e("filterAddTAG", "AddViewModel checkFilterExist existingFilter $existingFilter")
            existFilterLiveData.postValue(existingFilter ?: filter)
            hideProgress()
        }
    }

    fun getCountryCodeMap() {
        showProgress()
        launch {
            val countryCodeList = countryCodeRepository.getAllCountryCodes()
            Log.e("filterAddTAG", "AddViewModel getCountryCodeMap countryCodeMap.size ${countryCodeList?.size}")
            countryCodeList?.apply {
                countryCodeLiveData.postValue(this)
            }
            hideProgress()
        }
    }

    fun checkContactListByFilter(filter: Filter) {
        Log.e("filterAddTAG", "AddViewModel checkContactListByFilter filter $filter")
        showProgress()
        launch {
            queryContactListLiveData.postValue(contactRepository.getQueryContacts(filter).orEmpty())
            Log.e("filterAddTAG", "AddViewModel queryContactListLiveData $filter")
            hideProgress()
        }
    }

    fun getContactList() {
        showProgress()
        launch {
            val contactList = contactRepository.getAllContacts()
            Log.e("filterAddTAG", "AddViewModel getContactList contactList?.size ${contactList?.size}")
            contactList?.apply {
                contactLiveData.postValue(this)
            }
            hideProgress()
        }
    }

    fun insertFilter(filter: Filter) {
        showProgress()
        launch {
            filter.isFromDb = true
            filterRepository.insertFilter(filter) {
                insertFilterLiveData.postValue(filter.filter)
            }
            hideProgress()
        }
    }

    fun deleteFilter(filter: Filter) {
        showProgress()
        launch {
            filterRepository.deleteFilterList(listOf(filter)) {
                deleteFilterLiveData.postValue(filter.filter)
            }
            hideProgress()
        }
    }

}