package com.tarasovvp.smartblocker.presentation

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.firebase.perf.metrics.AddTrace
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.utils.extensions.orZero
import com.tarasovvp.smartblocker.infrastructure.prefs.SharedPrefs
import com.tarasovvp.smartblocker.domain.models.entities.CurrentUser
import com.tarasovvp.smartblocker.domain.models.MainProgress
import com.tarasovvp.smartblocker.domain.usecase.main.MainUseCase
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val mainUseCase: MainUseCase
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
            mainUseCase.getCurrentUser { currentUser ->
                insertCurrentUserData(currentUser)
            }
        }
    }

    private fun insertCurrentUserData(currentUser: CurrentUser?) {
        launch {
            currentUser?.let {
                val filters = async { mainUseCase.insertAllFilters(it.filterList) }
                val filteredCalls =
                    async { mainUseCase.insertAllFilteredCalls(it.filteredCallList) }
                awaitAll(filters, filteredCalls)
                getAllData()
            }
        }
    }

    fun getAllData() {
        launch {
            setCountryCodeData()
            val filterList = mainUseCase.getAllFilters()
            setContactData(filterList)
            setLogCallData(filterList)
            setFilteredCallData(filterList)
            mainUseCase.insertAllFilters(filterList)
            successAllDataLiveData.postValue(true)
            progressStatusLiveData.postValue(mainProgress.apply {
                progressDescription =
                    getApplication<Application>().getString(R.string.progress_update_calls_change)
            })
        }
    }

    @AddTrace(name = "setCountryCodeData")
    private suspend fun setCountryCodeData() {
        progressStatusLiveData.postValue(mainProgress.apply {
            progressDescription =
                getApplication<Application>().getString(R.string.progress_update_localizations)
        })
        val countryCodeList = mainUseCase.getSystemCountryCodeList { size, position ->
            progressStatusLiveData.postValue(mainProgress.apply {
                progressMax = size
                progressPosition = position
            })
        }
        if (SharedPrefs.countryCode.isNullOrEmpty()) {
            SharedPrefs.countryCode = countryCodeList.firstOrNull { it.country == SharedPrefs.country?.uppercase() }?.countryCode
        }
        mainUseCase.insertAllCountryCodes(countryCodeList)
    }

    @AddTrace(name = "setContactData")
    private suspend fun setContactData(filterList: List<Filter>) {
        progressStatusLiveData.postValue(mainProgress.apply {
            progressDescription =
                getApplication<Application>().getString(R.string.progress_update_contacts_receive)
        })
        val contactList =
            mainUseCase.getSystemContactList(getApplication()) { size, position ->
                progressStatusLiveData.postValue(mainProgress.apply {
                    progressMax = size
                    progressPosition = position
                })
            }
        progressStatusLiveData.postValue(mainProgress.apply {
            progressDescription =
                getApplication<Application>().getString(R.string.progress_update_contacts_change)
        })
        mainUseCase.setFilterToContact(filterList, contactList) { size, position ->
            progressStatusLiveData.postValue(mainProgress.apply {
                progressMax = size
                progressPosition = position
            })
        }
        mainUseCase.insertContacts(contactList)
    }

    @AddTrace(name = "setLogCallData")
    private suspend fun setLogCallData(filterList: List<Filter>) {
        progressStatusLiveData.postValue(mainProgress.apply {
            progressDescription =
                getApplication<Application>().getString(R.string.progress_update_calls_receive)
        })
        val logCallList =
            mainUseCase.getSystemLogCallList(getApplication()) { size, position ->
                progressStatusLiveData.postValue(mainProgress.apply {
                    progressMax = size
                    progressPosition = position
                })
            }
        progressStatusLiveData.postValue(mainProgress.apply {
            progressDescription =
                getApplication<Application>().getString(R.string.progress_update_calls_change)
        })
        mainUseCase.setFilterToLogCall(filterList, logCallList) { size, position ->
            progressStatusLiveData.postValue(mainProgress.apply {
                progressMax = size
                progressPosition = position
            })
        }
        mainUseCase.insertAllLogCalls(logCallList)
        filterList.onEach { filter ->
            filter.filteredContacts = filter.filteredContacts.orZero() + logCallList.filter { filter.filter == it.filter }.distinctBy { it.number }.size
        }
    }

    @AddTrace(name = "setFilteredCallData")
    private suspend fun setFilteredCallData(filterList: List<Filter>) {
        progressStatusLiveData.postValue(mainProgress.apply {
            progressDescription =
                getApplication<Application>().getString(R.string.progress_update_filtered_calls)
        })
        val filteredCallList = mainUseCase.getAllFilteredCalls()
        mainUseCase.setFilterToFilteredCall(filterList, filteredCallList) { size, position ->
            progressStatusLiveData.postValue(mainProgress.apply {
                progressMax = size
                progressPosition = position
            })
        }
        mainUseCase.insertAllFilteredCalls(filteredCallList)
    }
}