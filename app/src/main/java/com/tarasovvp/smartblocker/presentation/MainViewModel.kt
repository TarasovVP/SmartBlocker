package com.tarasovvp.smartblocker.presentation

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.firebase.perf.metrics.AddTrace
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.data.database.entities.Filter
import com.tarasovvp.smartblocker.utils.extensions.orZero
import com.tarasovvp.smartblocker.infrastructure.prefs.SharedPrefs
import com.tarasovvp.smartblocker.domain.models.CurrentUser
import com.tarasovvp.smartblocker.domain.models.MainProgress
import com.tarasovvp.smartblocker.domain.repository.*
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val contactRepository: ContactRepository,
    private val countryCodeRepository: CountryCodeRepository,
    private val filterRepository: FilterRepository,
    private val logCallRepository: LogCallRepository,
    private val filteredCallRepository: FilteredCallRepository,
    private val realDataBaseRepository: RealDataBaseRepository
) : BaseViewModel(application) {

    val successAllDataLiveData = MutableLiveData<Boolean>()

    private val mainProgress = MainProgress()

    fun getCurrentUser() {
        launch {
            showProgress()
            progressStatusLiveData.postValue(mainProgress.apply {
                progressDescription =
                    getApplication<Application>().getString(R.string.progress_data_collect)
            })
            realDataBaseRepository.getCurrentUser { currentUser ->
                insertCurrentUserData(currentUser)
            }
        }
    }

    private fun insertCurrentUserData(currentUser: CurrentUser?) {
        launch {
            currentUser?.let {
                progressStatusLiveData.postValue(mainProgress.apply {
                    progressDescription =
                        getApplication<Application>().getString(R.string.progress_external_data_collect)
                })
                val filters = async { filterRepository.insertAllFilters(it.filterList) }
                val filteredCalls =
                    async { filteredCallRepository.insertAllFilteredCalls(it.filteredCallList) }
                awaitAll(filters, filteredCalls)
                getAllData()
            }
        }
    }

    fun getAllData() {
        launch {
            setCountryCodeData()
            val filterList = filterRepository.allFilters() as? ArrayList
            setContactData(filterList)
            setLogCallData(filterList)
            setFilteredCallData(filterList)
            filterList?.let { filterRepository.insertAllFilters(it) }
            successAllDataLiveData.postValue(true)
        }
    }

    @AddTrace(name = "setCountryCodeData")
    private suspend fun setCountryCodeData() {
        val countryCodeList = countryCodeRepository.getSystemCountryCodeList { size, position ->
            progressStatusLiveData.postValue(mainProgress.apply {
                progressDescription =
                    getApplication<Application>().getString(R.string.progress_update_localizations)
                progressMax = size
                progressPosition = position
            })
        }
        if (SharedPrefs.countryCode.isNullOrEmpty()) {
            SharedPrefs.countryCode = countryCodeList.firstOrNull { it.country == SharedPrefs.country?.uppercase() }?.countryCode
        }
        countryCodeRepository.insertAllCountryCodes(countryCodeList)
    }

    @AddTrace(name = "setContactData")
    private suspend fun setContactData(filterList: ArrayList<Filter>?) {
        val contactList =
            contactRepository.getSystemContactList(getApplication()) { size, position ->
                progressStatusLiveData.postValue(mainProgress.apply {
                    progressDescription =
                        getApplication<Application>().getString(R.string.progress_update_contacts_receive)
                    progressMax = size
                    progressPosition = position
                })
            }
        contactRepository.setFilterToContact(filterList, contactList) { size, position ->
            progressStatusLiveData.postValue(mainProgress.apply {
                progressDescription =
                    getApplication<Application>().getString(R.string.progress_update_contacts_change)
                progressMax = size
                progressPosition = position
            })
        }
        contactRepository.insertContacts(contactList)
    }

    @AddTrace(name = "setLogCallData")
    private suspend fun setLogCallData(filterList: ArrayList<Filter>?) {
        val logCallList =
            logCallRepository.getSystemLogCallList(getApplication()) { size, position ->
                progressStatusLiveData.postValue(mainProgress.apply {
                    progressDescription =
                        getApplication<Application>().getString(R.string.progress_update_calls_receive)
                    progressMax = size
                    progressPosition = position
                })
            }
        logCallRepository.setFilterToLogCall(filterList, logCallList) { size, position ->
            progressStatusLiveData.postValue(mainProgress.apply {
                progressDescription =
                    getApplication<Application>().getString(R.string.progress_update_calls_change)
                progressMax = size
                progressPosition = position
            })
        }
        logCallRepository.insertAllLogCalls(logCallList)
        filterList?.onEach { filter ->
            filter.filteredContacts = filter.filteredContacts.orZero() + logCallList.filter { filter.filter == it.filter }.distinctBy { it.number }.size
        }
    }

    @AddTrace(name = "setFilteredCallData")
    private suspend fun setFilteredCallData(filterList: ArrayList<Filter>?) {
        val filteredCallList = filteredCallRepository.allFilteredCalls() as ArrayList
        filteredCallRepository.setFilterToFilteredCall(filterList, filteredCallList) { size, position ->
            progressStatusLiveData.postValue(mainProgress.apply {
                progressDescription =
                    getApplication<Application>().getString(R.string.progress_update_filtered_calls)
                progressMax = size
                progressPosition = position
            })
        }
        filteredCallRepository.insertAllFilteredCalls(filteredCallList)
    }
}