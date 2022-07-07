package com.tarasovvp.blacklister.ui.main.numberdetail

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tarasovvp.blacklister.model.BlackNumber
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.model.WhiteNumber
import com.tarasovvp.blacklister.repository.BlackNumberRepository
import com.tarasovvp.blacklister.repository.ContactRepository
import com.tarasovvp.blacklister.repository.WhiteNumberRepository
import com.tarasovvp.blacklister.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class NumberDetailViewModel(application: Application) : BaseViewModel(application) {

    private val contactRepository = ContactRepository
    private val blackNumberRepository = BlackNumberRepository
    private val whiteNumberRepository = WhiteNumberRepository

    val numberDetailLiveData = MutableLiveData<Contact>()
    val blackNumberLiveData = MutableLiveData<List<BlackNumber>>()
    val whiteNumberLiveData = MutableLiveData<List<WhiteNumber>>()

    fun getBlackNumberList(number: String) {
        viewModelScope.launch {
            try {
                val blackNumberList = blackNumberRepository.getBlackNumberList(number)
                blackNumberList?.let {
                    blackNumberLiveData.postValue(it)
                }
            } catch (e: java.lang.Exception) {
                exceptionLiveData.postValue(e.localizedMessage)
            }
        }
    }

    fun getWhiteNumberList(number: String) {
        viewModelScope.launch {
            try {
                val whiteNumberList = whiteNumberRepository.getWhiteNumberList(number)
                whiteNumberList?.let {
                    whiteNumberLiveData.postValue(it)
                }
            } catch (e: java.lang.Exception) {
                exceptionLiveData.postValue(e.localizedMessage)
            }
        }
    }

    fun getContact(number: String) {
        viewModelScope.launch {
            try {
                val contact = contactRepository.getContactByNumber(number) ?: Contact(name = number, phone = number)
                numberDetailLiveData.postValue(contact)
            } catch (e: java.lang.Exception) {
                exceptionLiveData.postValue(e.localizedMessage)
            }
        }
    }
}