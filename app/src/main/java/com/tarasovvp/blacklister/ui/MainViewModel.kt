package com.tarasovvp.blacklister.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.local.SharedPreferencesUtil
import com.tarasovvp.blacklister.model.CurrentUser
import com.tarasovvp.blacklister.model.WhiteFilter
import com.tarasovvp.blacklister.repository.*
import com.tarasovvp.blacklister.ui.base.BaseViewModel

class MainViewModel(application: Application) : BaseViewModel(application) {
    private val logCallRepository = LogCallRepository
    private val blackFilterRepository = BlackFilterRepository
    private val whiteFilterRepository = WhiteFilterRepository
    private val contactRepository = ContactRepository
    private val realDataBaseRepository = RealDataBaseRepository

    val successAllDataLiveData = MutableLiveData<Boolean>()

    fun getCurrentUser() {
        launch {
            showProgress()
            realDataBaseRepository.getCurrentUser { currentUser ->
                SharedPreferencesUtil.isWhiteListPriority =
                    currentUser?.isWhiteListPriority.isTrue()
                currentUser?.let { insertAllBlackFilters(it) }
            }
        }
    }

    private fun insertAllBlackFilters(currentUser: CurrentUser) {
        launch {
            blackFilterRepository.insertAllBlackFilters(currentUser.blackFilterList)
            insertAllWhiteFilters(currentUser.whiteFilterList)
        }
    }

    private fun insertAllWhiteFilters(whiteFilterList: ArrayList<WhiteFilter>) {
        launch {
            whiteFilterRepository.insertAllWhiteFilters(whiteFilterList)
            getAllData()
        }
    }

    fun getAllData() {
        showProgress()
        launch {
            Log.e("allDataTAG", "MainViewModel getAllData")
            val contactList = contactRepository.getSystemContactList(getApplication<Application>())
            contactList.forEach { contact ->
                val isInWhiteList =
                    whiteFilterRepository.getWhiteFilterList(contact.trimmedPhone)?.isEmpty()
                        .isTrue().not()
                val isInBlackList =
                    blackFilterRepository.getBlackFilterList(contact.trimmedPhone)?.isEmpty()
                        .isTrue().not()
                contact.isBlackFilter =
                    (isInBlackList && SharedPreferencesUtil.isWhiteListPriority.not()) || (isInBlackList && SharedPreferencesUtil.isWhiteListPriority && isInWhiteList.not())
                contact.isWhiteFilter =
                    (isInWhiteList && SharedPreferencesUtil.isWhiteListPriority) || (isInWhiteList && SharedPreferencesUtil.isWhiteListPriority.not() && isInBlackList.not())
            }
            contactRepository.insertContacts(contactList)

            logCallRepository.insertAllLogCalls(logCallRepository.getSystemLogCallList(
                getApplication<Application>()))

            successAllDataLiveData.postValue(true)
            hideProgress()
        }
    }
}