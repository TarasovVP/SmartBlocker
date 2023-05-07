package com.tarasovvp.smartblocker.presentation.main.number.list.list_contact

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.domain.entities.db_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.usecases.ListContactUseCase
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListContactViewModel @Inject constructor(
    application: Application,
    private val listContactUseCase: ListContactUseCase
) : BaseViewModel(application) {

    val contactListLiveData = MutableLiveData<List<ContactWithFilter>>()
    val filteredContactListLiveData = MutableLiveData<List<ContactWithFilter>>()
    val contactHashMapLiveData = MutableLiveData<Map<String, List<ContactWithFilter>>?>()

    fun getContactsWithFilters(refreshing: Boolean) {
        if (refreshing.not()) showProgress()
        launch {
            val contactList = listContactUseCase.allContactWithFilters()
            contactListLiveData.postValue(contactList)
            hideProgress()
        }
    }

    fun getFilteredContactList(contactList: List<ContactWithFilter>, searchQuery: String, filterIndexes: ArrayList<Int>) {
        launch {
            val filteredContactList = listContactUseCase.getFilteredContactList(contactList, searchQuery, filterIndexes)
            filteredContactListLiveData.postValue(filteredContactList)
            hideProgress()
        }
    }

    fun getHashMapFromContactList(contactList: List<ContactWithFilter>, refreshing: Boolean) {
        if (refreshing.not()) showProgress()
        launch {
            val contactsWithFilters = contactList.groupBy {
                if (it.contact?.name.isNullOrEmpty()) String.EMPTY else it.contact?.name?.get(0).toString()
            }/*.mapValues { (_, contactsWithFilterList) ->
                contactsWithFilterList.map { contactWithFilter ->
                    contactWithFilterMapper.mapToUIModel(contactWithFilter)
                }
            }*/
            contactHashMapLiveData.postValue(contactsWithFilters)
            hideProgress()
        }
    }
}