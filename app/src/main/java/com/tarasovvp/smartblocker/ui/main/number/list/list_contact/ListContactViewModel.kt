package com.tarasovvp.smartblocker.ui.main.number.list.list_contact

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.database.database_views.ContactWithFilter
import com.tarasovvp.smartblocker.repository.ContactRepository
import com.tarasovvp.smartblocker.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListContactViewModel @Inject constructor(
    application: Application,
    private val contactRepository: ContactRepository
) : BaseViewModel(application) {

    val contactLiveData = MutableLiveData<List<ContactWithFilter>>()
    val contactHashMapLiveData = MutableLiveData<Map<String, List<ContactWithFilter>>?>()

    fun getContactsWithFilters(refreshing: Boolean) {
        if (refreshing.not()) showProgress()
        launch {
            val contactList = contactRepository.getContactsWithFilters()
            contactList.apply {
                contactLiveData.postValue(this)
            }
            hideProgress()
        }
    }

    fun getHashMapFromContactList(contactList: List<ContactWithFilter>, refreshing: Boolean) {
        if (refreshing.not()) showProgress()
        launch {
            contactHashMapLiveData.postValue(
                contactRepository.getHashMapFromContactList(
                    contactList
                )
            )
            hideProgress()
        }
    }
}