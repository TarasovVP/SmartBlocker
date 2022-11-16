package com.tarasovvp.blacklister.ui.number_data.filter_add

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.blacklister.constants.Constants.PLUS_CHAR
import com.tarasovvp.blacklister.extensions.EMPTY
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.model.CountryCode
import com.tarasovvp.blacklister.model.Filter
import com.tarasovvp.blacklister.model.LogCall
import com.tarasovvp.blacklister.repository.CallRepository
import com.tarasovvp.blacklister.repository.ContactRepository
import com.tarasovvp.blacklister.repository.CountryCodeRepository
import com.tarasovvp.blacklister.repository.FilterRepository
import com.tarasovvp.blacklister.ui.base.BaseViewModel
import com.tarasovvp.blacklister.ui.number_data.NumberData
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

class FilterAddViewModel(application: Application) : BaseViewModel(application) {

    private val filterRepository = FilterRepository
    private val contactRepository = ContactRepository
    private val callRepository = CallRepository
    private val countryCodeRepository = CountryCodeRepository

    val countryCodeListLiveData = MutableLiveData<List<CountryCode>>()
    val numberDataListLiveData = MutableLiveData<List<NumberData>>()
    val filterActionLiveData = MutableLiveData<Filter>()

    fun getCountryCodeList() {
        launch {
            val countryCodeList = countryCodeRepository.getAllCountryCodes()
            Log.e("filterAddTAG",
                "AddViewModel getCountryCodeMap countryCodeMap.size ${countryCodeList?.size}")
            countryCodeList?.apply {
                countryCodeListLiveData.postValue(this)
            }
        }
    }

    fun getNumberDataList() {
        showProgress()
        launch {
            val contacts = async { contactRepository.getAllContacts() }
            val filters = async { filterRepository.allFilters() }
            val calls = async { callRepository.getAllCallsNumbers() }
            awaitAll(contacts, filters, calls)
            val contactList = contacts.await().orEmpty()
            val filterList = filters.await().orEmpty()
            val callList = calls.await().orEmpty()
            Log.e("filterAddTAG",
                "AddViewModel getContactFilterList callList?.size ${callList.size}")
            val numberDataList = ArrayList<NumberData>().apply {
                addAll(contactList)
                addAll(filterList)
                addAll(callList)
            }
            numberDataList.sortBy {
                when (it) {
                    is Contact -> it.trimmedPhone.replace(PLUS_CHAR.toString(), String.EMPTY)
                    is Filter -> it.filter.replace(PLUS_CHAR.toString(), String.EMPTY)
                    is LogCall -> it.number.replace(PLUS_CHAR.toString(), String.EMPTY)
                    else -> String.EMPTY
                }
            }
            Log.e("filterAddTAG",
                "AddViewModel getContactFilterList mainDataList?.size ${numberDataList.size}")
            numberDataListLiveData.postValue(numberDataList)
        }
    }

    fun insertFilter(filter: Filter) {
        launch {
            Log.e("filterAddTAG",
                "AddViewModel getContactFilterList filter $filter")
            filterRepository.insertFilter(filter) {
                filterActionLiveData.postValue(filter)
            }
        }
    }

    fun updateFilter(filter: Filter) {
        launch {
            filterRepository.updateFilter(filter) {
                filterActionLiveData.postValue(filter)
            }
        }
    }

    fun deleteFilter(filter: Filter) {
        launch {
            Log.e("filterAddTAG",
                "AddViewModel deleteFilter filter $filter")
            filterRepository.deleteFilterList(listOf(filter)) {
                filterActionLiveData.postValue(filter)
            }
        }
    }

}