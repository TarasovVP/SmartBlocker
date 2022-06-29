package com.tarasovvp.blacklister.ui.main.contactlist

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tarasovvp.blacklister.model.BlackNumber
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.repository.BlackNumberRepository
import com.tarasovvp.blacklister.repository.ContactRepository
import com.tarasovvp.blacklister.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class ContactListViewModel(application: Application) : BaseViewModel(application) {

    private val contactRepository = ContactRepository
    private val blackNumberRepository = BlackNumberRepository

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
                contactHashMapLiveData.postValue(contactRepository.getHashMapFromContactList(
                    contactList))
            } catch (e: java.lang.Exception) {
                exceptionLiveData.postValue(e.localizedMessage)
            }
        }
    }

    fun updateContact(contact: Contact) {
        viewModelScope.launch {
            try {
                contactRepository.updateContact(contact)
                contact.phone?.let { phone -> BlackNumber(blackNumber = phone) }
                    ?.let { blackNumber -> updateBlackNumber(contact.isBlackList, blackNumber) }
                getContactList()
            } catch (e: java.lang.Exception) {
                exceptionLiveData.postValue(e.localizedMessage)
            }
        }
    }

    private fun updateBlackNumber(isBlackList: Boolean, blackNumber: BlackNumber) {
        viewModelScope.launch {
            try {
                if (isBlackList) {
                    blackNumberRepository.insertBlackNumber(blackNumber) {
                        //TODO check implementing
                    }
                } else {
                    blackNumberRepository.deleteBlackNumber(blackNumber) {

                    }
                }
            } catch (e: java.lang.Exception) {
                exceptionLiveData.postValue(e.localizedMessage)
            }
        }
    }
}