package com.tarasovvp.blacklister.ui.main.contactlist

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.blacklister.model.BlackNumber
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.repository.BlackNumberRepository
import com.tarasovvp.blacklister.repository.ContactRepository
import com.tarasovvp.blacklister.ui.base.BaseViewModel

class ContactListViewModel(application: Application) : BaseViewModel(application) {

    private val contactRepository = ContactRepository
    private val blackNumberRepository = BlackNumberRepository

    val contactLiveData = MutableLiveData<List<Contact>>()
    val contactHashMapLiveData = MutableLiveData<HashMap<String, List<Contact>>?>()

    fun getContactList() {
        launch {

            val contactList = contactRepository.getAllContacts()
            contactList?.apply {
                contactLiveData.postValue(this)
            }
        }
    }

    fun getHashMapFromContactList(contactList: List<Contact>) {
        launch {
            contactHashMapLiveData.postValue(contactRepository.getHashMapFromContactList(
                contactList))
        }
    }

    fun updateContact(contact: Contact) {
        launch {

            contactRepository.updateContact(contact)
            contact.phone?.let { phone -> BlackNumber(number = phone) }
                ?.let { blackNumber -> updateBlackNumber(contact.isBlackList, blackNumber) }
            getContactList()
        }
    }

    private fun updateBlackNumber(isBlackList: Boolean, blackNumber: BlackNumber) {
        launch {
            if (isBlackList) {
                blackNumberRepository.insertBlackNumber(blackNumber) {
                    //TODO check implementing
                }
            } else {
                blackNumberRepository.deleteBlackNumber(blackNumber) {

                }
            }
        }
    }
}