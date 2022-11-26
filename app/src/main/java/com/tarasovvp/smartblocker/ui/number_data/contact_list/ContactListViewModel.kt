package com.tarasovvp.smartblocker.ui.number_data.contact_list

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.models.Contact
import com.tarasovvp.smartblocker.repository.ContactRepository
import com.tarasovvp.smartblocker.ui.base.BaseViewModel

class ContactListViewModel(application: Application) : BaseViewModel(application) {

    private val contactRepository = ContactRepository

    val contactLiveData = MutableLiveData<List<Contact>>()
    val contactHashMapLiveData = MutableLiveData<Map<String, List<Contact>>?>()

    fun getContactList(refreshing: Boolean) {
        if (refreshing.not()) showProgress()
        launch {
            val contactList = contactRepository.getAllContacts()
            contactList?.apply {
                contactLiveData.postValue(this)
            }
            hideProgress()
        }
    }

    fun getHashMapFromContactList(contactList: List<Contact>, refreshing: Boolean) {
        if (refreshing.not()) showProgress()
        launch {
            contactHashMapLiveData.postValue(contactRepository.getHashMapFromContactList(
                contactList))
            hideProgress()
        }
    }
}