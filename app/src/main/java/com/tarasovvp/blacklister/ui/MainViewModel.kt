package com.tarasovvp.blacklister.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.blacklister.constants.Constants.BLACK_FILTER
import com.tarasovvp.blacklister.constants.Constants.DEFAULT_FILTER
import com.tarasovvp.blacklister.constants.Constants.WHITE_FILTER
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.local.SharedPreferencesUtil
import com.tarasovvp.blacklister.model.Filter
import com.tarasovvp.blacklister.repository.*
import com.tarasovvp.blacklister.ui.base.BaseViewModel

class MainViewModel(application: Application) : BaseViewModel(application) {
    private val filterRepository = FilterRepository
    private val countryCodeRepository = CountryCodeRepository
    private val logCallRepository = CallRepository
    private val contactRepository = ContactRepository
    private val realDataBaseRepository = RealDataBaseRepository

    val successAllDataLiveData = MutableLiveData<Boolean>()

    fun getCurrentUser() {
        launch {
            Log.e("getAllDataTAG", "MainViewModel getCurrentUser")
            showProgress()
            progressStatusLiveData.postValue("Сбор информацию")
            realDataBaseRepository.getCurrentUser { currentUser ->
                currentUser?.let { insertAllFilters(it.filterList) }
            }
        }
    }

    private fun insertAllFilters(filterList: ArrayList<Filter>) {
        launch {
            Log.e("getAllDataTAG", "MainViewModel insertAllWhiteFilters")
            progressStatusLiveData.postValue("Обновление фильтров")
            filterRepository.insertAllFilters(filterList)
            getAllData()
        }
    }

    fun getAllData() {
        launch {
            Log.e("getAllDataTAG", "MainViewModel getAllData check time start")
            // init country code data
            Log.e("allDataTAG", "MainViewModel getAllData getSystemContactList")
            progressStatusLiveData.postValue("Обновление данных локализации")
            val countryCodeList = countryCodeRepository.getSystemCountryCodeList()
            Log.e("allDataTAG",
                "MainViewModel getSystemCountryCodeList countryCodeList.size ${countryCodeList.size}")
            countryCodeRepository.insertAllCountryCodes(countryCodeList)
            // init contacts data
            Log.e("allDataTAG", "MainViewModel getAllData getSystemContactList")
            progressStatusLiveData.postValue("Обновление контактов")
            val contactList = contactRepository.getSystemContactList(getApplication<Application>())
            Log.e("allDataTAG", "MainViewModel getAllData contactList.forEach")
            contactList.forEach { contact ->
                val filterList = filterRepository.getFilterList(contact.trimmedPhone)
                val isInWhiteList = filterList?.any { it.isBlackFilter().not() }.isTrue()
                val isInBlackList = filterList?.any { it.isBlackFilter() }.isTrue()
                contact.filterType = when {
                    (isInBlackList ) || (isInBlackList && isInWhiteList.not()) -> BLACK_FILTER
                    (isInWhiteList ) || (isInWhiteList && isInBlackList.not()) -> WHITE_FILTER
                    else -> DEFAULT_FILTER
                }
            }
            Log.e("allDataTAG", "MainViewModel getAllData insertContacts")
            contactRepository.insertContacts(contactList)
            Log.e("allDataTAG", "MainViewModel getAllData getSystemLogCallList")
            progressStatusLiveData.postValue("Обновление списка звонков")
            // init calls data
            val callLogList = logCallRepository.getSystemLogCallList(getApplication<Application>())
            Log.e("allDataTAG", "MainViewModel getAllData insertAllLogCalls")
            logCallRepository.insertAllLogCalls(callLogList)
            Log.e("allDataTAG", "MainViewModel getAllData successAllDataLiveData.postValue")
            successAllDataLiveData.postValue(true)
            progressStatusLiveData.postValue("Обновление данных")
            Log.e("getAllDataTAG", "MainViewModel getAllData check time finish")
        }
    }
}