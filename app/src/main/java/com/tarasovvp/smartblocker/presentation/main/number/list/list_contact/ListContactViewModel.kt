package com.tarasovvp.smartblocker.presentation.main.number.list.list_contact

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.domain.models.database_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.usecase.number.list.list_contact.ListContactUseCase
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListContactViewModel @Inject constructor(
    application: Application,
    private val listContactUseCase: ListContactUseCase
) : BaseViewModel(application) {

    val contactLiveData = MutableLiveData<List<ContactWithFilter>>()
    val contactHashMapLiveData = MutableLiveData<Map<String, List<ContactWithFilter>>?>()

    fun getContactsWithFilters(refreshing: Boolean) {
        if (refreshing.not()) showProgress()
        launch {
            val contactList = listContactUseCase.getContactsWithFilters()
            contactList.apply {
                contactLiveData.postValue(this)
            }
            hideProgress()
        }
    }

    fun getHashMapFromContactList(contactList: List<ContactWithFilter>, refreshing: Boolean) {
        if (refreshing.not()) showProgress()
        launch {
            contactHashMapLiveData.postValue(
                listContactUseCase.getHashMapFromContactList(
                    contactList
                )
            )
            hideProgress()
        }
    }
}