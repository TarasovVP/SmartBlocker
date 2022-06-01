package com.tarasovvp.blacklister.ui.main.contactdetail

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.tarasovvp.blacklister.model.BlackNumber
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.provider.BlackNumberRepositoryImpl
import com.tarasovvp.blacklister.provider.ContactRepositoryImpl
import com.tarasovvp.blacklister.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class ContactDetailViewModel(application: Application) : BaseViewModel(application) {

    private val contactRepository = ContactRepositoryImpl
    private val blackNumberRepository = BlackNumberRepositoryImpl

    fun updateContact(contact: Contact) {
        viewModelScope.launch {
            try {
                contactRepository.updateContact(contact)
                contact.phone?.let { phone -> BlackNumber(blackNumber = phone) }
                    ?.let { blackNumber -> updateBlackNumber(contact.isBlackList, blackNumber) }
            } catch (e: java.lang.Exception) {
                exceptionLiveData.postValue(e.localizedMessage)
            }
        }
    }

    private fun updateBlackNumber(isBlackList: Boolean, blackNumber: BlackNumber) {
        viewModelScope.launch {
            try {
                if (isBlackList) {
                    blackNumberRepository.insertBlackNumber(blackNumber)
                } else {
                    blackNumberRepository.deleteBlackNumber(blackNumber)
                }
            } catch (e: java.lang.Exception) {
                exceptionLiveData.postValue(e.localizedMessage)
            }
        }
    }

}