package com.tarasovvp.smartblocker.presentation

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.firebase.perf.metrics.AddTrace
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.utils.extensions.orZero
import com.tarasovvp.smartblocker.infrastructure.prefs.SharedPrefs
import com.tarasovvp.smartblocker.domain.models.MainProgress
import com.tarasovvp.smartblocker.domain.models.entities.*
import com.tarasovvp.smartblocker.domain.usecase.main.MainUseCase
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val mainUseCase: MainUseCase
) : BaseViewModel(application) {

    val successAllDataLiveData = MutableLiveData<Boolean>()
    val currentUserLiveData = MutableLiveData<CurrentUser>()

    private val mainProgress = MainProgress()

    fun getCurrentUser() {
        launch {
            showProgress()
            progressStatusLiveData.postValue(mainProgress.apply {
                progressDescription = R.string.progress_data_collect
            })
            mainUseCase.getCurrentUser { currentUser ->
                currentUserLiveData.postValue(currentUser)
            }
        }
    }

    fun setCurrentUserData(currentUser: CurrentUser) {
        launch {
            insertUserFilters(currentUser.filterList)
            insertUserFilteredCalls(currentUser.filteredCallList)
            getAllData()
        }
    }

    suspend fun insertUserFilters(filterList: List<Filter>) {
        mainUseCase.insertAllFilters(filterList)
    }

    suspend fun insertUserFilteredCalls(filteredCallList: List<FilteredCall>) {
        mainUseCase.insertAllFilteredCalls(filteredCallList)
    }

    fun getAllData() {
        launch {
            setCountryCodeData()
            val filterList = getAllFilters()
            setContactData(filterList)
            setLogCallData(filterList)
            setFilteredCallData(filterList)
            mainUseCase.insertAllFilters(filterList)
            successAllDataLiveData.postValue(true)
        }
    }

    @AddTrace(name = "setCountryCodeData")
    private suspend fun setCountryCodeData() {
        progressStatusLiveData.postValue(mainProgress.apply {
            progressDescription = R.string.progress_update_localizations
        })
        val countryCodeList = getSystemCountryCodeList()
        if (SharedPrefs.countryCode.isNullOrEmpty()) {
            SharedPrefs.countryCode = countryCodeList.firstOrNull { it.country == SharedPrefs.country?.uppercase() }?.countryCode
        }
        insertAllCountryCodes(countryCodeList)
    }

    suspend fun getSystemCountryCodeList(): List<CountryCode> {
        return mainUseCase.getSystemCountryCodeList { size, position ->
            progressStatusLiveData.postValue(mainProgress.apply {
                progressMax = size
                progressPosition = position
            })
        }
    }

    suspend fun insertAllCountryCodes(countryCodeList: List<CountryCode>) {
        mainUseCase.insertAllCountryCodes(countryCodeList)
    }

    suspend fun getAllFilters(): List<Filter> {
        return mainUseCase.getAllFilters()
    }

    @AddTrace(name = "setContactData")
    private suspend fun setContactData(filterList: List<Filter>) {
        progressStatusLiveData.postValue(mainProgress.apply {
            progressDescription = R.string.progress_update_contacts_receive
        })
        val contactList = getSystemContactList()
        progressStatusLiveData.postValue(mainProgress.apply {
            progressDescription = R.string.progress_update_contacts_change
        })
        val updatedContactList = setFilterToContact(filterList, contactList)
        insertContacts(updatedContactList)
    }

    suspend fun getSystemContactList(): List<Contact> {
        return mainUseCase.getSystemContactList(getApplication()) { size, position ->
            progressStatusLiveData.postValue(mainProgress.apply {
                progressMax = size
                progressPosition = position
            })
        }
    }

    suspend fun setFilterToContact(filterList: List<Filter>, contactList: List<Contact>): List<Contact> {
        return mainUseCase.setFilterToContact(filterList, contactList) { size, position ->
            progressStatusLiveData.postValue(mainProgress.apply {
                progressMax = size
                progressPosition = position
            })
        }
    }

    suspend fun insertContacts(contactList: List<Contact>) {
        mainUseCase.insertContacts(contactList)
    }

    @AddTrace(name = "setLogCallData")
    private suspend fun setLogCallData(filterList: List<Filter>) {
        progressStatusLiveData.postValue(mainProgress.apply {
            progressDescription = R.string.progress_update_calls_receive
        })
        val logCallList = getSystemLogCallList()
        progressStatusLiveData.postValue(mainProgress.apply {
            progressDescription = R.string.progress_update_calls_change
        })
        val updatedLocCallList = setFilterToLogCall(filterList, logCallList)
        mainUseCase.insertAllLogCalls(updatedLocCallList)
        filterList.onEach { filter ->
            filter.filteredContacts = filter.filteredContacts.orZero() + logCallList.filter { filter.filter == it.filter }.distinctBy { it.number }.size
        }
    }

    suspend fun getSystemLogCallList(): List<LogCall> {
        return mainUseCase.getSystemLogCallList(getApplication()) { size, position ->
            progressStatusLiveData.postValue(mainProgress.apply {
                progressMax = size
                progressPosition = position
            })
        }
    }

    suspend fun setFilterToLogCall(filterList: List<Filter>, logCallList: List<LogCall>): List<LogCall> {
        return mainUseCase.setFilterToLogCall(filterList, logCallList) { size, position ->
            progressStatusLiveData.postValue(mainProgress.apply {
                progressMax = size
                progressPosition = position
            })
        }
    }

    @AddTrace(name = "setFilteredCallData")
    private suspend fun setFilteredCallData(filterList: List<Filter>) {
        progressStatusLiveData.postValue(mainProgress.apply {
            progressDescription = R.string.progress_update_filtered_calls
        })
        val filteredCallList = getAllFilteredCalls()
        mainUseCase.setFilterToFilteredCall(filterList, filteredCallList) { size, position ->
            progressStatusLiveData.postValue(mainProgress.apply {
                progressMax = size
                progressPosition = position
            })
        }
        val updatedFilteredCallList = setFilterToFilteredCall(filterList, filteredCallList)
        insertAllFilteredCalls(updatedFilteredCallList)
    }

    suspend fun getAllFilteredCalls(): List<FilteredCall> {
        return mainUseCase.getAllFilteredCalls()
    }

    suspend fun setFilterToFilteredCall(filterList: List<Filter>, filteredCallList: List<FilteredCall>): List<FilteredCall> {
        return mainUseCase.setFilterToFilteredCall(filterList, filteredCallList) { size, position ->
            progressStatusLiveData.postValue(mainProgress.apply {
                progressMax = size
                progressPosition = position
            })
        }
    }

    suspend fun insertAllFilteredCalls(filteredCallList: List<FilteredCall>) {
        mainUseCase.insertAllFilteredCalls(filteredCallList)
    }
}