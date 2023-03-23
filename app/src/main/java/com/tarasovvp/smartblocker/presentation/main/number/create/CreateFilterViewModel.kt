package com.tarasovvp.smartblocker.presentation.main.number.create

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.data.database.database_views.ContactWithFilter
import com.tarasovvp.smartblocker.data.database.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.data.database.database_views.LogCallWithFilter
import com.tarasovvp.smartblocker.data.database.entities.CountryCode
import com.tarasovvp.smartblocker.data.database.entities.Filter
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.DEFAULT_FILTER
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.PLUS_CHAR
import com.tarasovvp.smartblocker.domain.models.NumberData
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.utils.extensions.isDarkMode
import com.tarasovvp.smartblocker.domain.repository.LogCallRepository
import com.tarasovvp.smartblocker.domain.repository.ContactRepository
import com.tarasovvp.smartblocker.domain.repository.CountryCodeRepository
import com.tarasovvp.smartblocker.domain.repository.FilterRepository
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import timber.log.Timber
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
        Timber.e("CreateFilterViewModel getCountryCodeWithCountry country $country")
        launch {
            val countryCode =
                country?.let { countryCodeRepository.getCountryCodeWithCountry(it) }
                    ?: CountryCode()
            countryCodeLiveData.postValue(countryCode)
        }
    }

    fun getCountryCodeWithCode(code: Int?) {
        Timber.e("CreateFilterViewModel getCountryCodeWithCode code $code")
        launch {
            val countryCode =
                code?.let { countryCodeRepository.getCountryCodeWithCode(it) } ?: CountryCode()
            countryCodeLiveData.postValue(countryCode)
        }
    }

    fun getNumberDataList() {
        Timber.e("CreateFilterViewModel getNumberDataList showProgress")
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
        Timber.e("CreateFilterViewModel checkFilterExist filter?.filter ${filterWithCountryCode.filter} createFilter ${filterWithCountryCode.createFilter()}")
        launch {
            val result = filterRepository.getFilter(filterWithCountryCode)
            existingFilterLiveData.postValue(result ?: FilterWithCountryCode(Filter(filterType = DEFAULT_FILTER)))
        }
    }

    fun filterNumberDataList(filterWithCountryCode: FilterWithCountryCode?, numberDataList: ArrayList<NumberData>, color: Int) {
        Timber.e("CreateFilterViewModel filterNumberDataList showProgress filter?.filter ${filterWithCountryCode?.filter} createFilter ${filterWithCountryCode?.createFilter()} numberDataList.size ${numberDataList.size}")
        showProgress()
        launch {
            val filteredNumberDataList =
                contactRepository.filteredNumberDataList(filterWithCountryCode?.filter, numberDataList, color)
            filteredNumberDataListLiveData.postValue(filteredNumberDataList)
            hideProgress()
            Timber.e("CreateFilterViewModel filterNumberDataList hideProgress filteredNumberDataList.size ${filteredNumberDataList.size} isDarkMode ${getApplication<Application>().isDarkMode()}")
        }
    }

    fun createFilter(filter: Filter?) {
        Timber.e("CreateFilterViewModel createFilter createFilter $filter filter.country ${filter?.country}")
        launch {
            filter?.let {
                filterRepository.insertFilter(it) {
                    filterActionLiveData.postValue(it)
                }
            }
        }
    }

    fun updateFilter(filter: Filter?) {
        Timber.e("CreateFilterViewModel updateFilter")
        launch {
            filter?.let {
                filterRepository.updateFilter(it) {
                    filterActionLiveData.postValue(it)
                }
            }
        }
    }

    fun deleteFilter(filter: Filter?) {
        Timber.e("CreateFilterViewModel deleteFilter")
        launch {
            filter?.let {
                filterRepository.deleteFilterList(listOf(it)) {
                    filterActionLiveData.postValue(it)
                }
            }
        }
    }

}