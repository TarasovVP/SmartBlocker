package com.tarasovvp.blacklister.ui.main.filter_add

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.blacklister.extensions.isNotNull
import com.tarasovvp.blacklister.extensions.orZero
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.model.CountryCode
import com.tarasovvp.blacklister.model.Filter
import com.tarasovvp.blacklister.repository.ContactRepository
import com.tarasovvp.blacklister.repository.CountryCodeRepository
import com.tarasovvp.blacklister.repository.FilterRepository
import com.tarasovvp.blacklister.ui.base.BaseViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

class FilterAddViewModel(application: Application) : BaseViewModel(application) {

    private val filterRepository = FilterRepository
    private val contactRepository = ContactRepository
    private val countryCodeRepository = CountryCodeRepository

    val existFilterLiveData = MutableLiveData<Int>()
    val insertFilterLiveData = MutableLiveData<String>()
    val deleteFilterLiveData = MutableLiveData<String>()
    val countryCodeListLiveData = MutableLiveData<List<CountryCode>>()
    val queryContactListLiveData = MutableLiveData<List<Contact>>()
    val contactLiveData = MutableLiveData<List<Contact>>()

    fun checkFilterExist(filter: Filter) {
        Log.e("filterAddTAG", "AddViewModel checkFilterExist filter $filter")
        showProgress()
        launch {
            val existingFilter = filterRepository.getFilter(filter)
            Log.e("filterAddTAG", "AddViewModel checkFilterExist existingFilter $existingFilter")
            existFilterLiveData.postValue(existingFilter?.filterType)
            hideProgress()
        }
    }

    fun getCountryCodeAndContactsData() {
        launch {
            val countryCode = async { countryCodeRepository.getAllCountryCodes() }
            val contacts = async { contactRepository.getAllContacts() }
            awaitAll(countryCode, contacts)
            val countryCodeList = countryCode.await()
            val contactList = contacts.await()
            Log.e("filterAddTAG",
                "AddViewModel getCountryCodeAndContactsData countryCodeList?.size ${countryCodeList?.size} contactList?.size ${contactList?.size}")
            countryCodeList?.apply {
                countryCodeListLiveData.postValue(this)
            }
            contactList?.apply {
                contactLiveData.postValue(this)
            }
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