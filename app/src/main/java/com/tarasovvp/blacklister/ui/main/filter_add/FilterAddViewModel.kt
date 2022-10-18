package com.tarasovvp.blacklister.ui.main.filter_add

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.blacklister.constants.Constants.PLUS_CHAR
import com.tarasovvp.blacklister.extensions.EMPTY
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.model.CountryCode
import com.tarasovvp.blacklister.model.Filter
import com.tarasovvp.blacklister.repository.ContactRepository
import com.tarasovvp.blacklister.repository.CountryCodeRepository
import com.tarasovvp.blacklister.repository.FilterRepository
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.ui.base.BaseViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

class FilterAddViewModel(application: Application) : BaseViewModel(application) {

    private val filterRepository = FilterRepository
    private val contactRepository = ContactRepository
    private val countryCodeRepository = CountryCodeRepository

    val countryCodeListLiveData = MutableLiveData<List<CountryCode>>()
    val mainDataListLiveData = MutableLiveData<List<BaseAdapter.MainData>>()
    val insertFilterLiveData = MutableLiveData<String>()
    val deleteFilterLiveData = MutableLiveData<String>()

    fun getCountryCodeAndContactsData() {
        launch {
            val countryCode = async { countryCodeRepository.getAllCountryCodes() }
            val contacts = async { contactRepository.getAllContacts() }
            val filters = async { filterRepository.allFilters() }
            awaitAll(countryCode, contacts, filters)
            val countryCodeList = countryCode.await().orEmpty()
            val contactList = contacts.await().orEmpty()
            val filterList = filters.await().orEmpty()
            Log.e("filterAddTAG",
                "AddViewModel getCountryCodeAndContactsData countryCodeList?.size ${countryCodeList.size} contactList?.size ${contactList?.size}")
            countryCodeListLiveData.postValue(countryCodeList)
            val mainDataList = ArrayList<BaseAdapter.MainData>().apply {
                addAll(contactList)
                addAll(filterList)
            }
            mainDataList.sortBy { when (it) {
                is Contact -> it.trimmedPhone.replace(PLUS_CHAR.toString(), String.EMPTY)
                is Filter -> it.filter.replace(PLUS_CHAR.toString(), String.EMPTY)
                else -> String.EMPTY
            } }
            mainDataListLiveData.postValue(mainDataList)
        }
    }

    fun getContactsData() {
        launch {
            val contacts = async { contactRepository.getAllContacts() }
            val filters = async { filterRepository.allFilters() }
            awaitAll(contacts, filters)
            val contactList = contacts.await().orEmpty()
            val filterList = filters.await().orEmpty()
            val mainDataList = ArrayList<BaseAdapter.MainData>().apply {
                addAll(contactList)
                addAll(filterList)
            }
            mainDataList.sortBy { when (it) {
                is Contact -> it.trimmedPhone.replace(PLUS_CHAR.toString(), String.EMPTY)
                is Filter -> it.filter.replace(PLUS_CHAR.toString(), String.EMPTY)
                else -> String.EMPTY
            } }
            Log.e("filterAddTAG",
                "AddViewModel getContactsData contactList?.size ${mainDataList.size}")
            mainDataListLiveData.postValue(mainDataList)
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