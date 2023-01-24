package com.tarasovvp.smartblocker.ui.number_data.details.filter_add

import android.app.Application
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

class FilterAddViewModel(application: Application) : BaseViewModel(application) {

    private val countryCodeRepository = CountryCodeRepository
    private val filterRepository = FilterRepository
    private val contactRepository = ContactRepository
    private val callRepository = CallRepository

    val countryCodeListLiveData = MutableLiveData<List<CountryCode>>()
    val countryCodeLiveData = MutableLiveData<CountryCode>()
    val numberDataListLiveData = MutableLiveData<List<NumberData>>()
    val existingFilterLiveData = MutableLiveData<Filter>()
    val filteredNumberDataListLiveData = MutableLiveData<ArrayList<NumberData>>()
    val filterActionLiveData = MutableLiveData<Filter>()

    fun getCountryCodeList() {
        launch {
            val countryCodeList = countryCodeRepository.getAllCountryCodes()
            countryCodeList?.apply {
                countryCodeListLiveData.postValue(this)
            }
        }
    }

    fun getCountryCodeWithCountry(country: String?) {
        launch {
            val countryCode =
                country?.let { countryCodeRepository.getCountryCodeWithCountry(it) }
                    ?: CountryCode()
            countryCodeLiveData.postValue(countryCode)
        }
    }

    fun getCountryCodeWithCode(code: Int?) {
        launch {
            val countryCode =
                code?.let { countryCodeRepository.getCountryCodeWithCode(it) } ?: CountryCode()
            countryCodeLiveData.postValue(countryCode)
        }
    }

    fun getNumberDataList() {
        showProgress()
        launch {
            val contacts = async { contactRepository.getAllContacts() }
            val calls = async { callRepository.getAllCallsNumbers() }
            val contactList = contacts.await().orEmpty()
            val callList = calls.await().orEmpty()
            val numberDataList = ArrayList<NumberData>().apply {
                addAll(contactList)
                addAll(callList)
                sortBy {
                    it.numberData.replace(PLUS_CHAR.toString(), String.EMPTY)
                }
                distinctBy {
                    it.numberData
                }
            }
            numberDataListLiveData.postValue(numberDataList)
        }
    }

    fun checkFilterExist(filter: Filter) {
        launch {
            val result = filterRepository.getFilter(filter)
            existingFilterLiveData.postValue(result ?: Filter())
        }
    }

    fun filterNumberDataList(filter: Filter?, numberDataList: ArrayList<NumberData>) {
        showProgress()
        launch {
            filteredNumberDataListLiveData.postValue(contactRepository.filteredNumberDataList(filter,
                numberDataList))
            hideProgress()
        }
    }

    fun insertFilter(filter: Filter) {
        launch {
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
            filterRepository.deleteFilterList(listOf(filter)) {
                filterActionLiveData.postValue(filter)
            }
        }
    }

}