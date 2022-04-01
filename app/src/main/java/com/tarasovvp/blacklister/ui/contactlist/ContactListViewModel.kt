package com.tarasovvp.blacklister.ui.contactlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.provider.ContactRepositoryImpl
import kotlinx.coroutines.launch

class ContactListViewModel(application: Application) : AndroidViewModel(application) {

    private val contactRepository = ContactRepositoryImpl

    val contactLiveData = MutableLiveData<List<Contact>>()
    val contactHashMapLiveData = MutableLiveData<HashMap<String, List<Contact>>?>()

    fun getContactList() {
        viewModelScope.launch {
            val contactList = contactRepository.getAllContacts()
            contactList?.apply {
                contactLiveData.postValue(this)
            }
        }
    }

    fun getHashMapFromContactList(contactList: List<Contact>) {
        viewModelScope.launch {
            contactHashMapLiveData.postValue(contactRepository.getHashMapFromContactList(contactList))
        }
    }
}