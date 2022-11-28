package com.tarasovvp.smartblocker.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.models.CurrentUser
import com.tarasovvp.smartblocker.models.MainProgress
import com.tarasovvp.smartblocker.repository.*
import com.tarasovvp.smartblocker.ui.base.BaseViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

class MainViewModel(application: Application) : BaseViewModel(application) {
    private val filterRepository = FilterRepository
    private val filteredCallRepository = FilteredCallRepository
    private val countryCodeRepository = CountryCodeRepository
    private val logCallRepository = CallRepository
    private val contactRepository = ContactRepository
    private val realDataBaseRepository = RealDataBaseRepository

    val successAllDataLiveData = MutableLiveData<Boolean>()

    private val mainProgress = MainProgress()

    fun getCurrentUser() {
        launch {
            Log.e("getAllDataTAG", "MainViewModel getCurrentUser")
            showProgress()
            progressStatusLiveData.postValue(mainProgress.apply {
                progressDescription = "Сбор информацию"
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
                    progressDescription = "Запрос внешних данных"
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
            Log.e("getAllDataTAG", "MainViewModel getAllData check time start")
            // init country code data
            Log.e("allDataTAG", "MainViewModel getAllData getSystemContactList")
            val countryCodeList = countryCodeRepository.getSystemCountryCodeList { size, position ->
                progressStatusLiveData.postValue(mainProgress.apply {
                    progressDescription = getApplication<Application>().getString(R.string.progress_update_localizations)
                    progressMax = size
                    progressPosition = position
                })
            }
            Log.e("allDataTAG",
                "MainViewModel getSystemCountryCodeList countryCodeList.size ${countryCodeList.size}")
            countryCodeRepository.insertAllCountryCodes(countryCodeList)
            // init contacts data
            Log.e("allDataTAG", "MainViewModel getAllData getSystemContactList")
            val contactList = contactRepository.getSystemContactList(getApplication<Application>()) { size, position ->
                progressStatusLiveData.postValue(mainProgress.apply {
                    progressDescription = getApplication<Application>().getString(R.string.update_contacts)
                    progressMax = size
                    progressPosition = position
                })
            }
            Log.e("allDataTAG", "MainViewModel getAllData insertContacts")
            contactRepository.insertContacts(contactList)
            Log.e("allDataTAG", "MainViewModel getAllData getSystemLogCallList")
            // init calls data
            val callLogList = logCallRepository.getSystemLogCallList(getApplication<Application>()) { size, position ->
                progressStatusLiveData.postValue(mainProgress.apply {
                    progressDescription = getApplication<Application>().getString(R.string.update_calls)
                    progressMax = size
                    progressPosition = position
                })
            }
            Log.e("allDataTAG", "MainViewModel getAllData insertAllLogCalls")
            logCallRepository.insertAllLogCalls(callLogList)
            // init filter data
            val filterList = filterRepository.allFilters() as? ArrayList
            filterList?.forEachIndexed { index, filter ->
                filter.filteredContacts = contactList.filter { it.filter == filter }.size.toString()
                filter.filteredCalls = filteredCallRepository.filteredCallsByFilter(filter.filter)?.size.toString()
                progressStatusLiveData.postValue(mainProgress.apply {
                    progressDescription = getApplication<Application>().getString(R.string.update_filters)
                    progressMax = filterList.size
                    progressPosition = index
                })
            }
            Log.e("allDataTAG", "MainViewModel filterList $filterList")
            filterList?.let { filterRepository.insertAllFilters(it) }
            Log.e("allDataTAG", "MainViewModel getAllData successAllDataLiveData.postValue")
            successAllDataLiveData.postValue(true)
            progressStatusLiveData.postValue(mainProgress.apply {
                progressDescription = getApplication<Application>().getString(R.string.update_data)
                progressMax = 0
                progressPosition = 0
            })
            Log.e("getAllDataTAG", "MainViewModel getAllData check time finish")
        }
    }
}