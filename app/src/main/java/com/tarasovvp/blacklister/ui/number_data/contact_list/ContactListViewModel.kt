package com.tarasovvp.blacklister.ui.number_data.contact_list

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.repository.ContactRepository
import com.tarasovvp.blacklister.ui.base.BaseViewModel

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