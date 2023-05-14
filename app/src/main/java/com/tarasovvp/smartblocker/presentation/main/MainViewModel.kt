package com.tarasovvp.smartblocker.presentation.main

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.MutableLiveData
import com.google.firebase.perf.metrics.AddTrace
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.domain.entities.models.CurrentUser
import com.tarasovvp.smartblocker.presentation.ui_models.MainProgress
import com.tarasovvp.smartblocker.domain.entities.db_entities.*
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.MainUseCase
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import com.tarasovvp.smartblocker.utils.extensions.getUserCountry
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val mainUseCase: MainUseCase
) : BaseViewModel(application) {

    val onBoardingSeenLiveData = MutableLiveData<Boolean>()
    val blockerTurnOffLiveData = MutableLiveData<Boolean>()
    val successAllDataLiveData = MutableLiveData<Boolean>()
    val currentUserLiveData = MutableLiveData<CurrentUser>()

    private val mainProgress = MainProgress()

    fun setAppLanguage() {
        launch {
            mainUseCase.getAppLanguage().collect { appLang ->
                mainUseCase.setAppLanguage(appLang ?: Locale.getDefault().language)
            }
        }
    }

    fun setAppTheme() {
        launch {
            mainUseCase.getAppTheme().collect { appTheme ->
                AppCompatDelegate.setDefaultNightMode(appTheme ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    fun getOnBoardingSeen() {
        launch {
            mainUseCase.getOnBoardingSeen().collect { isOnBoardingSeen ->
                onBoardingSeenLiveData.postValue(isOnBoardingSeen.isTrue())
            }
        }
    }

    fun getBlockerTurnOff() {
        launch {
            mainUseCase.getBlockerTurnOff().collect { isSmartBlockerTurnOff ->
                blockerTurnOffLiveData.postValue(isSmartBlockerTurnOff.isTrue())
            }
        }
    }

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
            mainUseCase.setBlockHidden(currentUser.isBlockHidden)
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
            setContactData()
            setLogCallData()
            successAllDataLiveData.postValue(true)
        }
    }

    @AddTrace(name = "setCountryCodeData")
    private suspend fun setCountryCodeData() {
        progressStatusLiveData.postValue(mainProgress.apply {
            progressDescription = R.string.progress_update_localizations
        })
        val countryCodeList = mainUseCase.getSystemCountryCodes { size, position ->
            progressStatusLiveData.postValue(mainProgress.apply {
                progressMax = size
                progressPosition = position
            }) }
        getCurrentCountryCode(countryCodeList)
        mainUseCase.insertAllCountryCodes(countryCodeList)
    }

    private suspend fun getCurrentCountryCode(countryCodeList: List<CountryCode>) {
        mainUseCase.getCurrentCountryCode().collect { countryCode ->
            countryCode?.let {
                mainUseCase.setCurrentCountryCode(countryCodeList.firstOrNull { it.country == getApplication<Application>().getUserCountry() } ?: CountryCode())
            }
        }
    }

    @AddTrace(name = "setContactData")
    private suspend fun setContactData() {
        progressStatusLiveData.postValue(mainProgress.apply {
            progressDescription = R.string.progress_update_contacts_receive
        })
        val contactList = mainUseCase.getSystemContacts(getApplication()) { size, position ->
            progressStatusLiveData.postValue(mainProgress.apply {
                progressMax = size
                progressPosition = position
            })
        }
        mainUseCase.insertContacts(contactList)
    }

    @AddTrace(name = "setLogCallData")
    private suspend fun setLogCallData() {
        progressStatusLiveData.postValue(mainProgress.apply {
            progressDescription = R.string.progress_update_calls_receive
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