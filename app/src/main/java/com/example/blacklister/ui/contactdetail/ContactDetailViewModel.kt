package com.example.blacklister.ui.contactdetail

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.blacklister.model.BlackNumber
import com.example.blacklister.model.Contact
import com.example.blacklister.provider.BlackNumberRepositoryImpl
import com.example.blacklister.provider.ContactRepositoryImpl
import com.google.gson.Gson
import kotlinx.coroutines.launch

class ContactDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val contactRepository = ContactRepositoryImpl
    private val blackNumberRepository = BlackNumberRepositoryImpl

    fun updateContact(contact: Contact) {
        viewModelScope.launch {
            contactRepository.updateContact(contact)
            contact.phone?.let { phone -> BlackNumber(blackNumber = phone) }?.let { blackNumber -> updateBlackNumber(contact.isBlackList, blackNumber) }
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