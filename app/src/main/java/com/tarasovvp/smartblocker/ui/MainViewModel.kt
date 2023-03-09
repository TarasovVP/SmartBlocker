package com.tarasovvp.smartblocker.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants.OUT_GOING_CALL
import com.tarasovvp.smartblocker.extensions.isValidPhoneNumber
import com.tarasovvp.smartblocker.models.CurrentUser
import com.tarasovvp.smartblocker.models.MainProgress
import com.tarasovvp.smartblocker.repository.*
import com.tarasovvp.smartblocker.ui.base.BaseViewModel
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
            // init country code data
            val countryCodeList = countryCodeRepository.getSystemCountryCodeList { size, position ->
                progressStatusLiveData.postValue(mainProgress.apply {
                    progressDescription =
                        getApplication<Application>().getString(R.string.progress_update_localizations)
                    progressMax = size
                    progressPosition = position
                })
            }
            countryCodeRepository.insertAllCountryCodes(countryCodeList)
            // init contacts data
            val contactList =
                contactRepository.getSystemContactList(getApplication<Application>(), filterRepository) { size, position ->
                    progressStatusLiveData.postValue(mainProgress.apply {
                        progressDescription =
                            getApplication<Application>().getString(R.string.progress_update_contacts)
                        progressMax = size
                        progressPosition = position
                    })
                }
            contactRepository.insertContacts(contactList)
            Log.e("callTAG", "MainViewModel callLogList start")
            val callLogList =
                logCallRepository.getSystemLogCallList(getApplication<Application>()) { size, position ->
                    progressStatusLiveData.postValue(mainProgress.apply {
                        progressDescription =
                            getApplication<Application>().getString(R.string.progress_update_calls)
                        progressMax = size
                        progressPosition = position
                    })
                }
            logCallRepository.insertAllLogCalls(callLogList)
            Log.e("callTAG", "MainViewModel callLogList finish")
            // init filtered calls data
            val filteredCallList = filteredCallRepository.allFilteredCalls() as ArrayList
            filteredCallList.forEachIndexed { index, filteredCall ->
                progressStatusLiveData.postValue(mainProgress.apply {
                    progressDescription =
                        getApplication<Application>().getString(R.string.progress_update_filtered_calls)
                    progressMax = filteredCallList.size
                    progressPosition = index
                    filteredCall.filter = filteredCall.number.let { filterRepository.queryFilter(it) }?.filter
                })
            }
            filteredCallRepository.insertAllFilteredCalls(filteredCallList)
            // init filter data
            val filterList = filterRepository.allFilters() as? ArrayList
            filterList?.forEachIndexed { index, filter ->
                filter.filteredContacts = contactList.filter { it.filter == filter.filter }.size + callLogList.filter { it.filter == filter.filter }.distinctBy { it.number }.size
                progressStatusLiveData.postValue(mainProgress.apply {
                    progressDescription =
                        getApplication<Application>().getString(R.string.progress_update_filters)
                    progressMax = filterList.size
                    progressPosition = index
                })
            }
            filterList?.let { filterRepository.insertAllFilters(it) }
            successAllDataLiveData.postValue(true)
            progressStatusLiveData.postValue(mainProgress.apply {
                progressDescription =
                    getApplication<Application>().getString(R.string.progress_update_data)
                progressMax = 0
                progressPosition = 0
            })
        }
    }
}