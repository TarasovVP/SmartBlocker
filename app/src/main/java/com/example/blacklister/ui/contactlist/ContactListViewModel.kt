package com.example.blacklister.ui.contactlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.blacklister.extensions.contactList
import com.example.blacklister.extensions.formattedPhoneNumber
import com.example.blacklister.model.BlackNumber
import com.example.blacklister.provider.BlackNumberRepositoryImpl
import com.example.blacklister.provider.ContactRepositoryImpl
import kotlinx.coroutines.launch

class ContactListViewModel(application: Application) : AndroidViewModel(application) {

    private val contactRepository = ContactRepositoryImpl
    private val blackNumberRepository = BlackNumberRepositoryImpl

    val contactLiveData = contactRepository.subscribeToContacts()

    fun getContactList() {
        viewModelScope.launch {
            val blackNumberList = blackNumberRepository.allBlackNumbers()
            val contactList = getApplication<Application>().contactList()
            contactList.forEach { contact ->
                contact.isBlackList =
                    blackNumberList?.contains(contact.phone?.formattedPhoneNumber()?.let {
                        BlackNumber(
                            it
                        )
                    }) == true
            }
            contactRepository.inasertContacts(contactList)
        }
    }
}