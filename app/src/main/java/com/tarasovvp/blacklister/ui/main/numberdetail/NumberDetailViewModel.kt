package com.tarasovvp.blacklister.ui.main.numberdetail

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.model.NumberInfo
import com.tarasovvp.blacklister.provider.BlackNumberRepositoryImpl
import com.tarasovvp.blacklister.provider.ContactRepositoryImpl
import com.tarasovvp.blacklister.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class NumberDetailViewModel(application: Application) : BaseViewModel(application) {

    private val contactRepository = ContactRepositoryImpl
    private val blackNumberRepository = BlackNumberRepositoryImpl

    val numberDetailLiveData = MutableLiveData<Contact>()
    val numberInfoLiveData = MutableLiveData<NumberInfo>()
    val blackNumberAmountLiveData = MutableLiveData<String>()

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
                blackNumberAmountLiveData.postValue(it.toString())
            }
        }
    }
}