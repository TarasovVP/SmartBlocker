package com.tarasovvp.smartblocker.ui.main.number.list.list_contact

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.models.Contact
import com.tarasovvp.smartblocker.repository.ContactRepository
import com.tarasovvp.smartblocker.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListContactViewModel @Inject constructor(
    application: Application,
    private val contactRepository: ContactRepository
) : BaseViewModel(application) {

    val contactLiveData = MutableLiveData<List<Contact>>()
    val contactHashMapLiveData = MutableLiveData<Map<String, List<Contact>>?>()

    fun getContactList(refreshing: Boolean) {
        if (refreshing.not()) showProgress()
        launch {
            val contactList = contactRepository.getAllContacts()
            contactList.apply {
                contactLiveData.postValue(this)
            }
            hideProgress()
        }
    }

    fun getHashMapFromContactList(contactList: List<Contact>, refreshing: Boolean) {
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