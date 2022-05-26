package com.tarasovvp.blacklister.ui.main.contactlist

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.provider.ContactRepositoryImpl
import com.tarasovvp.blacklister.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class ContactListViewModel(application: Application) : BaseViewModel(application) {

    private val contactRepository = ContactRepositoryImpl

    val contactLiveData = MutableLiveData<List<Contact>>()
    val contactHashMapLiveData = MutableLiveData<HashMap<String, List<Contact>>?>()

    fun getContactList() {
        viewModelScope.launch {
            try {
                val contactList = contactRepository.getAllContacts()
                contactList?.apply {
                    contactLiveData.postValue(this)
                }
            } catch (e: Exception) {
                exceptionLiveData.postValue(e.localizedMessage)
            }
        }
    }

    fun getHashMapFromContactList(contactList: List<Contact>) {
        viewModelScope.launch {
            try {
                contactHashMapLiveData.postValue(contactRepository.getHashMapFromContactList(contactList))
            } catch (e: java.lang.Exception) {
                exceptionLiveData.postValue(e.localizedMessage)
            }
        }
    }
}