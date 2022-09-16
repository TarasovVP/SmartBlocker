package com.tarasovvp.blacklister.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.local.SharedPreferencesUtil
import com.tarasovvp.blacklister.model.CurrentUser
import com.tarasovvp.blacklister.model.WhiteFilter
import com.tarasovvp.blacklister.repository.*
import com.tarasovvp.blacklister.ui.base.BaseViewModel

class MainViewModel(application: Application) : BaseViewModel(application) {
    private val logCallRepository = CallRepository
    private val blackFilterRepository = BlackFilterRepository
    private val whiteFilterRepository = WhiteFilterRepository
    private val contactRepository = ContactRepository
    private val realDataBaseRepository = RealDataBaseRepository

    val successAllDataLiveData = MutableLiveData<Boolean>()
    val progressStatusLiveData = MutableLiveData<String>()

    fun getCurrentUser() {
        launch {
            Log.e("getAllDataTAG", "MainViewModel getCurrentUser")
            showProgress()
            progressStatusLiveData.postValue("Сбор информацию")
            realDataBaseRepository.getCurrentUser { currentUser ->
                SharedPreferencesUtil.isWhiteListPriority =
                    currentUser?.isWhiteListPriority.isTrue()
                currentUser?.let { insertAllBlackFilters(it) }
            }
        }
    }

    private fun insertAllBlackFilters(currentUser: CurrentUser) {
        launch {
            Log.e("getAllDataTAG", "MainViewModel insertAllBlackFilters")
            blackFilterRepository.insertAllBlackFilters(currentUser.blackFilterList)
            insertAllWhiteFilters(currentUser.whiteFilterList)
        }
    }

    private fun insertAllWhiteFilters(whiteFilterList: ArrayList<WhiteFilter>) {
        launch {
            Log.e("getAllDataTAG", "MainViewModel insertAllWhiteFilters")
            whiteFilterRepository.insertAllWhiteFilters(whiteFilterList)
            getAllData()
        }
    }

    fun getAllData() {
        launch {
            Log.e("getAllDataTAG", "MainViewModel getAllData start")
            Log.e("allDataTAG", "MainViewModel getAllData getSystemContactList")
            progressStatusLiveData.postValue("Обновление контактов")
            val contactList = contactRepository.getSystemContactList(getApplication<Application>())
            Log.e("allDataTAG", "MainViewModel getAllData contactList.forEach")
            contactList.forEach { contact ->
                val isInWhiteList = whiteFilterRepository.getWhiteFilterList(contact.trimmedPhone)?.isEmpty().isTrue().not()
                val isInBlackList = blackFilterRepository.getBlackFilterList(contact.trimmedPhone)?.isEmpty().isTrue().not()
                contact.isBlackFilter = (isInBlackList && SharedPreferencesUtil.isWhiteListPriority.not()) || (isInBlackList && SharedPreferencesUtil.isWhiteListPriority && isInWhiteList.not())
                contact.isWhiteFilter = (isInWhiteList && SharedPreferencesUtil.isWhiteListPriority) || (isInWhiteList && SharedPreferencesUtil.isWhiteListPriority.not() && isInBlackList.not())
            }
            Log.e("allDataTAG", "MainViewModel getAllData insertContacts")
            contactRepository.insertContacts(contactList)
            Log.e("allDataTAG", "MainViewModel getAllData getSystemLogCallList")
            progressStatusLiveData.postValue("Обновление списка звонков")
            val callLogList = logCallRepository.getSystemLogCallList(getApplication<Application>())
            Log.e("allDataTAG", "MainViewModel getAllData insertAllLogCalls")
            logCallRepository.insertAllLogCalls(callLogList)
            Log.e("allDataTAG", "MainViewModel getAllData successAllDataLiveData.postValue")
            successAllDataLiveData.postValue(true)
        }
    }
}