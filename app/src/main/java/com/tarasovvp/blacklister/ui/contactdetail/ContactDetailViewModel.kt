package com.tarasovvp.blacklister.ui.contactdetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tarasovvp.blacklister.model.BlackNumber
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.provider.BlackNumberRepositoryImpl
import com.tarasovvp.blacklister.provider.ContactRepositoryImpl
import kotlinx.coroutines.launch

class ContactDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val contactRepository = ContactRepositoryImpl
    private val blackNumberRepository = BlackNumberRepositoryImpl

    fun updateContact(contact: Contact) {
        viewModelScope.launch {
            contactRepository.updateContact(contact)
            contact.phone?.let { phone -> BlackNumber(blackNumber = phone) }
                ?.let { blackNumber -> updateBlackNumber(contact.isBlackList, blackNumber) }
        }
    }

    private fun updateBlackNumber(isBlackList: Boolean, blackNumber: BlackNumber) {
        viewModelScope.launch {
            if (isBlackList) {
                blackNumberRepository.insertBlackNumber(blackNumber)
            } else {
                blackNumberRepository.deleteBlackNumber(blackNumber)
            }
        }
    }

}