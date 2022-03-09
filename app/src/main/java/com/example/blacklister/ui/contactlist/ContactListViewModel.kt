package com.example.blacklister.ui.contactlist

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.blacklister.extensions.contactList
import com.example.blacklister.provider.ContactRepositoryImpl
import com.google.gson.Gson
import kotlinx.coroutines.launch

class ContactListViewModel(application: Application) : AndroidViewModel(application) {

    private val contactRepository = ContactRepositoryImpl

    val contactLiveData = contactRepository.subscribeToContacts()

    fun getContactList() {
        viewModelScope.launch {
            val contactList = getApplication<Application>().contactList()
            contactRepository.inasertContacts(contactList)
        }
    }
}