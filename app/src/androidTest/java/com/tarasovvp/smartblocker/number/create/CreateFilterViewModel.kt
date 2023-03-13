package com.tarasovvp.smartblocker.ui.main.number.create

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.constants.Constants.DEFAULT_FILTER
import com.tarasovvp.smartblocker.constants.Constants.PLUS_CHAR
import com.tarasovvp.smartblocker.extensions.EMPTY
import com.tarasovvp.smartblocker.extensions.isDarkMode
import com.tarasovvp.smartblocker.models.*
import com.tarasovvp.smartblocker.repository.LogCallRepository
import com.tarasovvp.smartblocker.repository.ContactRepository
import com.tarasovvp.smartblocker.repository.CountryCodeRepository
import com.tarasovvp.smartblocker.repository.FilterRepository
import com.tarasovvp.smartblocker.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import javax.inject.Inject

@HiltViewModel
class CreateFilterViewModel @Inject constructor(
    application: Application,
    private val contactRepository: ContactRepository,
    private val countryCodeRepository: CountryCodeRepository,
    private val filterRepository: FilterRepository,
    private val logCallRepository: LogCallRepository
) : BaseViewModel(application) {

    val countryCodeLiveData = MutableLiveData<CountryCode>()
    val numberDataListLiveData = MutableLiveData<List<NumberData>>()
    val existingFilterLiveData = MutableLiveData<FilterWithCountryCode>()
    val filteredNumberDataListLiveData = MutableLiveData<ArrayList<NumberData>>()
    val filterActionLiveData = MutableLiveData<Filter>()

    fun getCountryCodeWithCountry(country: String?) {
        Log.e("createFilterTAG", "CreateFilterViewModel getCountryCodeWithCountry country $country")
        launch {
            val countryCode =
                country?.let { countryCodeRepository.getCountryCodeWithCountry(it) }
                    ?: CountryCode()
            countryCodeLiveData.postValue(countryCode)
        }
    }

    fun getCountryCodeWithCode(code: Int?) {
        Log.e("createFilterTAG", "CreateFilterViewModel getCountryCodeWithCode code $code")
        launch {
            val countryCode =
                code?.let { countryCodeRepository.getCountryCodeWithCode(it) } ?: CountryCode()
            countryCodeLiveData.postValue(countryCode)
        }
    }

    fun getNumberDataList() {
        Log.e("createFilterTAG", "CreateFilterViewModel getNumberDataList showProgress")
        launch {
            val contacts = async { contactRepository.getContactsWithFilters() }
            val calls = async { logCallRepository.allCallNumberWithFilter() }
            val contactList = contacts.await()
            val callList = calls.await()
            val numberDataList = ArrayList<NumberData>().apply {
                addAll(contactList)
                addAll(callList)
                sortBy {
                    if (it is ContactWithFilter) it.contact?.number?.replace(PLUS_CHAR.toString(), String.EMPTY) else if (it is LogCallWithFilter) it.call?.number?.replace(PLUS_CHAR.toString(), String.EMPTY) else String.EMPTY
                }
            }
            numberDataListLiveData.postValue(numberDataList)
        }
    }

    fun checkFilterExist(filterWithCountryCode: FilterWithCountryCode) {
        Log.e(
            "createFilterTAG",
            "CreateFilterViewModel checkFilterExist filter?.filter ${filterWithCountryCode.filter} createFilter ${filterWithCountryCode.createFilter()}"
        )
        launch {
            val result = filterRepository.getFilter(filterWithCountryCode)
            existingFilterLiveData.postValue(result ?: FilterWithCountryCode(Filter(filterType = DEFAULT_FILTER)))
        }
    }

    fun filterNumberDataList(filterWithCountryCode: FilterWithCountryCode?, numberDataList: ArrayList<NumberData>, color: Int) {
        Log.e(
            "createFilterTAG",
            "CreateFilterViewModel filterNumberDataList showProgress filter?.filter ${filterWithCountryCode?.filter} createFilter ${filterWithCountryCode?.createFilter()} numberDataList.size ${numberDataList.size}"
        )
        showProgress()
        launch {
            val filteredNumberDataList =
                contactRepository.filteredNumberDataList(filterWithCountryCode?.filter, numberDataList, color)
            filteredNumberDataListLiveData.postValue(filteredNumberDataList)
            hideProgress()
            Log.e(
                "createFilterTAG",
                "CreateFilterViewModel filterNumberDataList hideProgress filteredNumberDataList.size ${filteredNumberDataList.size} isDarkMode ${getApplication<Application>().isDarkMode()}"
            )
        }
    }

    fun createFilter(filter: Filter?) {
        Log.e(
            "createFilterTAG",
            "CreateFilterViewModel createFilter createFilter $filter filter.country ${filter?.country}"
        )
        launch {
            filter?.let {
                filterRepository.insertFilter(it) {
                    filterActionLiveData.postValue(it)
                }
            }
        }
    }

    fun updateFilter(filter: Filter?) {
        Log.e("createFilterTAG", "CreateFilterViewModel updateFilter")
        launch {
            filter?.let {
                filterRepository.updateFilter(it) {
                    filterActionLiveData.postValue(it)
                }
            }
        }
    }

    fun deleteFilter(filter: Filter?) {
        Log.e("createFilterTAG", "CreateFilterViewModel deleteFilter")
        launch {
            filter?.let {
                filterRepository.deleteFilterList(listOf(it)) {
                    filterActionLiveData.postValue(it)
                }
            }
        }
    }

}