package com.tarasovvp.blacklister.ui.main.numberdetail

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.model.Number
import com.tarasovvp.blacklister.model.WhiteNumber
import com.tarasovvp.blacklister.repository.BlackNumberRepository
import com.tarasovvp.blacklister.repository.ContactRepository
import com.tarasovvp.blacklister.repository.WhiteNumberRepository
import com.tarasovvp.blacklister.ui.base.BaseViewModel

class NumberDetailViewModel(application: Application) : BaseViewModel(application) {

    private val contactRepository = ContactRepository
    private val blackNumberRepository = BlackNumberRepository
    private val whiteNumberRepository = WhiteNumberRepository

    val numberDetailLiveData = MutableLiveData<Contact>()
    val blackNumberLiveData = MutableLiveData<List<Number>>()
    val whiteNumberLiveData = MutableLiveData<List<WhiteNumber>>()

    fun getBlackNumberList(number: String) {
        launch {
            val blackNumberList = blackNumberRepository.getBlackNumberList(number)
            blackNumberList?.let {
                blackNumberLiveData.postValue(it)
            }
        }
    }

    fun getWhiteNumberList(number: String) {
        launch {
            val whiteNumberList = whiteNumberRepository.getWhiteNumberList(number)
            whiteNumberList?.let {
                whiteNumberLiveData.postValue(it)
            }
        }
    }

    fun getContact(number: String) {
        launch {
            val contact = contactRepository.getContactByNumber(number) ?: Contact(name = number,
                phone = number)
            numberDetailLiveData.postValue(contact)
        }
    }
}