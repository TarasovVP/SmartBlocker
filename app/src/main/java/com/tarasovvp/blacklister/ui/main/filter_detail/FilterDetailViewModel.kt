package com.tarasovvp.blacklister.ui.main.filter_detail

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.blacklister.extensions.EMPTY
import com.tarasovvp.blacklister.model.BlockedCall
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.model.Filter
import com.tarasovvp.blacklister.repository.BlockedCallRepository
import com.tarasovvp.blacklister.repository.ContactRepository
import com.tarasovvp.blacklister.repository.FilterRepository
import com.tarasovvp.blacklister.ui.base.BaseViewModel

class FilterDetailViewModel(application: Application) : BaseViewModel(application) {

    private val contactRepository = ContactRepository
    private val filterRepository = FilterRepository
    private val blockedCallRepository = BlockedCallRepository

    val contactDetailLiveData = MutableLiveData<Contact>()
    val filterLiveData = MutableLiveData<List<Filter>>()
    val blockedCallLiveData = MutableLiveData<List<BlockedCall>>()

    fun filterList(phone: String) {
        showProgress()
        launch {
            val blackNumberList = filterRepository.getFilterList(phone)
            blackNumberList?.let {
                filterLiveData.postValue(it)
            }
            hideProgress()
        }
    }

    fun getContact(phone: String) {
        showProgress()
        launch {
            val contact = contactRepository.getContactByPhone(phone)
                ?: Contact(name = String.EMPTY, phone = phone)
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