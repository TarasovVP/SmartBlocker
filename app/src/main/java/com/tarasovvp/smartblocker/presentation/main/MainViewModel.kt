package com.tarasovvp.smartblocker.presentation.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.entities.db_entities.FilteredCall
import com.tarasovvp.smartblocker.domain.entities.models.CurrentUser
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.MainUseCase
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import com.tarasovvp.smartblocker.presentation.ui_models.MainProgress
import com.tarasovvp.smartblocker.utils.extensions.getUserCountry
import com.tarasovvp.smartblocker.utils.extensions.isNotNull
import com.tarasovvp.smartblocker.utils.extensions.isNull
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val application: Application,
    private val mainUseCase: MainUseCase
) : BaseViewModel(application) {
    val onBoardingSeenLiveData = MutableLiveData<Boolean>()
    val blockerTurnOnLiveData = MutableLiveData<Boolean>()
    val successAllDataLiveData = MutableLiveData<Boolean>()
    private val mainProgress = MainProgress()

    fun getOnBoardingSeen() {
        launch {
            mainUseCase.getOnBoardingSeen().collect { isOnBoardingSeen ->
                onBoardingSeenLiveData.postValue(isOnBoardingSeen.isTrue())
            }
        }
    }

    fun getBlockerTurnOn() {
        launch {
            mainUseCase.getBlockerTurnOn().collect { blockerTurnOn ->
                blockerTurnOnLiveData.postValue(blockerTurnOn ?: true)
            }
        }
    }

    fun getCurrentUser(isInit: Boolean = false) {
        launch {
            progressStatusLiveData.postValue(mainProgress.apply {
                progressDescription = R.string.progress_data_collect
            })
            mainUseCase.getCurrentUser { operationResult ->
                when(operationResult) {
                    is Result.Success -> operationResult.data.takeIf { it.isNotNull() }?.let { setCurrentUserData(it, isInit) } ?: exceptionLiveData.postValue(application.getString(R.string.error_message))
                    is Result.Failure -> operationResult.errorMessage?.let { exceptionLiveData.postValue(it) }
                }
            }
        }
    }

    private fun setCurrentUserData(currentUser: CurrentUser, isInit: Boolean = false) {
        launch {
            insertUserFilters(currentUser.filterList)
            insertUserFilteredCalls(currentUser.filteredCallList)
            Log.e("blockHiddenTAG", "MainViewModel setCurrentUserData currentUser $currentUser")
            mainUseCase.setBlockHidden(currentUser.isBlockHidden)
            getAllData(isInit)
        }
    }

    suspend fun insertUserFilters(filterList: List<Filter>) {
        mainUseCase.insertAllFilters(filterList)
    }

    suspend fun insertUserFilteredCalls(filteredCallList: List<FilteredCall>) {
        mainUseCase.insertAllFilteredCalls(filteredCallList)
    }

    fun getAllData(isInit: Boolean = false) {
        launch {
            setCountryCodeData(isInit)
            setContactData(isInit)
            setLogCallData(isInit)
            successAllDataLiveData.postValue(true)
        }
    }

    suspend fun setCountryCodeData(isInit: Boolean = false) {
        progressStatusLiveData.postValue(mainProgress.apply {
            progressDescription = if (isInit) R.string.progress_collect_localizations else R.string.progress_update_localizations
        })
        val countryCodeList = mainUseCase.getSystemCountryCodes { size, position ->
            progressStatusLiveData.postValue(mainProgress.apply {
                progressMax = size
                progressPosition = position
            })
        }
        setCurrentCountryCode(countryCodeList)
        mainUseCase.insertAllCountryCodes(countryCodeList)
    }

    suspend fun setCurrentCountryCode(countryCodeList: List<CountryCode>) {
        mainUseCase.getCurrentCountryCode().collect { countryCode ->
            if (countryCode.isNull()) mainUseCase.setCurrentCountryCode(countryCodeList.firstOrNull { it.country == getApplication<Application>().getUserCountry() } ?: CountryCode())
        }
    }

    suspend fun setContactData(isInit: Boolean = false) {
        progressStatusLiveData.postValue(mainProgress.apply {
            progressDescription = if (isInit) R.string.progress_collect_contacts else R.string.progress_update_contacts
        })
        val contactList = mainUseCase.getSystemContacts(getApplication()) { size, position ->
            progressStatusLiveData.postValue(mainProgress.apply {
                progressMax = size
                progressPosition = position
            })
        }
        mainUseCase.insertAllContacts(contactList)
    }

    suspend fun setLogCallData(isInit: Boolean = false) {
        progressStatusLiveData.postValue(mainProgress.apply {
            progressDescription = if (isInit) R.string.progress_collect_calls else R.string.progress_update_calls
        })
        val logCallList = mainUseCase.getSystemLogCalls(getApplication()) { size, position ->
            progressStatusLiveData.postValue(mainProgress.apply {
                progressMax = size
                progressPosition = position
            })
        }
        mainUseCase.insertAllLogCalls(logCallList)
    }
}