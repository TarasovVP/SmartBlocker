package com.tarasovvp.blacklister.ui.main.contact_detail

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.blacklister.model.BlockedCall
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.model.Filter
import com.tarasovvp.blacklister.model.WhiteFilter
import com.tarasovvp.blacklister.repository.BlackFilterRepository
import com.tarasovvp.blacklister.repository.BlockedCallRepository
import com.tarasovvp.blacklister.repository.ContactRepository
import com.tarasovvp.blacklister.repository.WhiteFilterRepository
import com.tarasovvp.blacklister.ui.base.BaseViewModel

class ContactDetailViewModel(application: Application) : BaseViewModel(application) {

    private val contactRepository = ContactRepository
    private val blackFilterRepository = BlackFilterRepository
    private val whiteFilterRepository = WhiteFilterRepository
    private val blockedCallRepository = BlockedCallRepository

    val contactDetailLiveData = MutableLiveData<Contact>()
    val blackFilterLiveData = MutableLiveData<List<Filter>>()
    val whiteFilterLiveData = MutableLiveData<List<WhiteFilter>>()
    val blockedCallLiveData = MutableLiveData<List<BlockedCall>>()

    fun getBlackFilterList(phone: String) {
        showProgress()
        launch {
            val blackNumberList = blackFilterRepository.getBlackFilterList(phone)
            blackNumberList?.let {
                blackFilterLiveData.postValue(it)
            }
            hideProgress()
        }
    }

    fun getWhiteFilterList(filter: String) {
        showProgress()
        launch {
            val whiteNumberList = whiteFilterRepository.getWhiteFilterList(filter)
            whiteNumberList?.let {
                whiteFilterLiveData.postValue(it)
            }
            hideProgress()
        }
    }

    fun getContact(phone: String) {
        showProgress()
        launch {
            val contact = contactRepository.getContactByPhone(phone)
                ?: Contact(name = "Нет в списке контактов", phone = phone)
            contactDetailLiveData.postValue(contact)
            hideProgress()
        }
    }

    fun getBlockedCallList(phone: String) {
        showProgress()
        launch {
            val blockedCallsList = blockedCallRepository.blockedCallsByPhone(phone)
            blockedCallsList?.let {
                blockedCallLiveData.postValue(it)
            }
            hideProgress()
        }
    }
}