package com.example.blacklister.ui.contactdetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.blacklister.model.Contact
import com.example.blacklister.provider.ContactRepositoryImpl
import kotlinx.coroutines.launch

class ContactDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val contactRepository = ContactRepositoryImpl

    fun updateContact(contact: Contact) {
        viewModelScope.launch {
            contactRepository.updateContact(contact)
        }
    }

}