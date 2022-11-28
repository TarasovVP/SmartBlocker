package com.tarasovvp.smartblocker.ui.number_data.filter_add

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.constants.Constants.PLUS_CHAR
import com.tarasovvp.smartblocker.extensions.EMPTY
import com.tarasovvp.smartblocker.models.CountryCode
import com.tarasovvp.smartblocker.models.Filter
import com.tarasovvp.smartblocker.models.NumberData
import com.tarasovvp.smartblocker.repository.CallRepository
import com.tarasovvp.smartblocker.repository.ContactRepository
import com.tarasovvp.smartblocker.repository.CountryCodeRepository
import com.tarasovvp.smartblocker.repository.FilterRepository
import com.tarasovvp.smartblocker.ui.base.BaseViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

class FilterAddViewModel(application: Application) : BaseViewModel(application) {

    private val filterRepository = FilterRepository
    private val contactRepository = ContactRepository
    private val callRepository = CallRepository
    private val countryCodeRepository = CountryCodeRepository

    val countryCodeListLiveData = MutableLiveData<List<CountryCode>>()
    val numberDataListLiveData = MutableLiveData<List<NumberData>>()
    val filteredNumberDataListLiveData = MutableLiveData<ArrayList<NumberData>>()
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
                "AddViewModel getNumberDataList callList.size ${callList.size}")
            val numberDataList = ArrayList<NumberData>().apply {
                addAll(contactList)
                addAll(filterList)
                addAll(callList)
                sortBy {
                    it.numberData.replace(PLUS_CHAR.toString(), String.EMPTY)
                }
                distinctBy {
                    it.numberData
                }
            }
            Log.e("filterAddTAG",
                "AddViewModel getNumberDataList mainDataList?.size ${numberDataList.size}")
            numberDataListLiveData.postValue(numberDataList)
        }
    }

    fun filterNumberDataList(filter: Filter?, numberDataList: ArrayList<NumberData>) {
        showProgress()
        launch {
            Log.e("filterAddTAG",
                "AddViewModel filteredNumberDataList filter $filter")
            filteredNumberDataListLiveData.postValue(contactRepository.filteredNumberDataList(filter, numberDataList))
            hideProgress()
        }
    }

    fun insertFilter(filter: Filter) {
        launch {
            Log.e("filterAddTAG",
                "AddViewModel insertFilter filter $filter")
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