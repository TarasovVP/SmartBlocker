package com.tarasovvp.blacklister.ui.main.numberdetail

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tarasovvp.blacklister.model.BlackNumber
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.repository.BlackNumberRepository
import com.tarasovvp.blacklister.repository.ContactRepository
import com.tarasovvp.blacklister.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class NumberDetailViewModel(application: Application) : BaseViewModel(application) {

    private val contactRepository = ContactRepository
    private val blackNumberRepository = BlackNumberRepository

    val numberDetailLiveData = MutableLiveData<Contact>()
    val blackNumberAmountLiveData = MutableLiveData<ArrayList<BlackNumber?>>()

    fun getContact(number: String) {
        viewModelScope.launch {
            try {
                val contact = contactRepository.getContactByNumber(number) ?: Contact(name = number,
                    phone = number)
                numberDetailLiveData.postValue(contact)
            } catch (e: java.lang.Exception) {
                exceptionLiveData.postValue(e.localizedMessage)
            }
        }
    }

    fun getNumberInfo(number: String) {
        viewModelScope.launch(Dispatchers.IO) {
            blackNumberRepository.blackNumbersRemoteCount(number) {
                blackNumberAmountLiveData.postValue(it)
            }
        }
    }
}