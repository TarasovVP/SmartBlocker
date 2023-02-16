package com.tarasovvp.smartblocker.ui.main.number.create

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.constants.Constants.PLUS_CHAR
import com.tarasovvp.smartblocker.extensions.EMPTY
import com.tarasovvp.smartblocker.extensions.isDarkMode
import com.tarasovvp.smartblocker.models.CountryCode
import com.tarasovvp.smartblocker.models.Filter
import com.tarasovvp.smartblocker.models.NumberData
import com.tarasovvp.smartblocker.repository.CallRepository
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
    private val callRepository: CallRepository
) : BaseViewModel(application) {

    val countryCodeListLiveData = MutableLiveData<List<CountryCode>>()
    val countryCodeLiveData = MutableLiveData<CountryCode>()
    val numberDataListLiveData = MutableLiveData<List<NumberData>>()
    val existingFilterLiveData = MutableLiveData<Filter>()
    val filteredNumberDataListLiveData = MutableLiveData<ArrayList<NumberData>>()
    val filterActionLiveData = MutableLiveData<Filter>()

    fun getCountryCodeList() {
        Log.e("createFilterTAG", "CreateFilterViewModel getCountryCodeList")
        launch {
            val countryCodeList = countryCodeRepository.getAllCountryCodes()
            countryCodeList.apply {
                countryCodeListLiveData.postValue(this)
            }
        }
    }

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
            val contacts = async { contactRepository.getAllContacts() }
            val calls = async { callRepository.getAllCallsNumbers() }
            val contactList = contacts.await()
            val callList = calls.await()
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
        Log.e(
            "createFilterTAG",
            "CreateFilterViewModel checkFilterExist filter?.filter ${filter.filter} createFilter ${filter.createFilter()}"
        )
        launch {
            val result = filterRepository.getFilter(filter)
            existingFilterLiveData.postValue(result ?: Filter())
        }
    }

    fun filterNumberDataList(filter: Filter?, numberDataList: ArrayList<NumberData>, color: Int) {
        Log.e(
            "createFilterTAG",
            "CreateFilterViewModel filterNumberDataList showProgress filter?.filter ${filter?.filter} createFilter ${filter?.createFilter()} numberDataList.size ${numberDataList.size}"
        )
        showProgress()
        launch {
            val filteredNumberDataList =
                contactRepository.filteredNumberDataList(filter, numberDataList, color)
            filteredNumberDataListLiveData.postValue(filteredNumberDataList)
            hideProgress()
            Log.e(
                "createFilterTAG",
                "CreateFilterViewModel filterNumberDataList hideProgress filteredNumberDataList.size ${filteredNumberDataList.size} isDarkMode ${getApplication<Application>().isDarkMode()}"
            )
        }
    }

    fun createFilter(filter: Filter) {
        Log.e(
            "createFilterTAG",
            "CreateFilterViewModel createFilter createFilter ${filter.createFilter()}"
        )
        launch {
            filterRepository.insertFilter(filter) {
                filterActionLiveData.postValue(filter)
            }
        }
    }

    fun updateFilter(filter: Filter) {
        Log.e("createFilterTAG", "CreateFilterViewModel updateFilter")
        launch {
            filterRepository.updateFilter(filter) {
                filterActionLiveData.postValue(filter)
            }
        }
    }

    fun deleteFilter(filter: Filter) {
        Log.e("createFilterTAG", "CreateFilterViewModel deleteFilter")
        launch {
            filterRepository.deleteFilterList(listOf(filter)) {
                filterActionLiveData.postValue(filter)
            }
        }
    }

}