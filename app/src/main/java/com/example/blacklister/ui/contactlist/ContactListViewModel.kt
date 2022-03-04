package com.example.blacklister.ui.contactlist

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.blacklister.extensions.contactList
import com.example.blacklister.model.Contact
import kotlinx.coroutines.launch

class ContactListViewModel(application: Application) : AndroidViewModel(application) {

    val contactLiveData = MutableLiveData<List<Contact>>()

    fun getContactList() {
        viewModelScope.launch {
            val contactList = getApplication<Application>().contactList()
            contactLiveData.postValue(contactList)
        }
    }
}