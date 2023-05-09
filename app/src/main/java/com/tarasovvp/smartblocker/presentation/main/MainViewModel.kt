package com.tarasovvp.smartblocker.presentation.main

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.firebase.perf.metrics.AddTrace
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.domain.entities.models.CurrentUser
import com.tarasovvp.smartblocker.data.prefs.SharedPrefs
import com.tarasovvp.smartblocker.domain.entities.models.MainProgress
import com.tarasovvp.smartblocker.domain.entities.db_entities.*
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.MainUseCase
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import com.tarasovvp.smartblocker.utils.extensions.getUserCountry
import com.tarasovvp.smartblocker.utils.extensions.isNull
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
            mainUseCase.getCurrentUser { operationResult ->
                when(operationResult) {
                    is Result.Success -> operationResult.data?.let { currentUserLiveData.postValue(it) }
                    is Result.Failure -> operationResult.errorMessage?.let { exceptionLiveData.postValue(it) }
                }
            }
        }
    }

    fun setCurrentUserData(currentUser: CurrentUser) {
        launch {
            insertUserFilters(currentUser.filterList)
            insertUserFilteredCalls(currentUser.filteredCallList)
            SharedPrefs.blockHidden = currentUser.isBlockHidden
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
        if (SharedPrefs.countryCode.isNull()) {
            SharedPrefs.countryCode = countryCodeList.firstOrNull { it.country == getApplication<Application>().getUserCountry() } ?: CountryCode()
        }
        insertAllCountryCodes(countryCodeList)
    }

    suspend fun getSystemCountryCodeList(): List<CountryCode> {
        return mainUseCase.getSystemCountryCodes { size, position ->
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
        insertContacts(contactList)
    }

    suspend fun getSystemContactList(): List<Contact> {
        return mainUseCase.getSystemContacts(getApplication()) { size, position ->
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
        mainUseCase.insertAllLogCalls(logCallList)
    }

    suspend fun getSystemLogCallList(): List<LogCall> {
        return mainUseCase.getSystemLogCalls(getApplication()) { size, position ->
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
        insertAllFilteredCalls(filteredCallList)
    }

    suspend fun getAllFilteredCalls(): List<FilteredCall> {
        return mainUseCase.getAllFilteredCalls()
    }

    suspend fun insertAllFilteredCalls(filteredCallList: List<FilteredCall>) {
        mainUseCase.insertAllFilteredCalls(filteredCallList)
    }
}