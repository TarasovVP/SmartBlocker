package com.tarasovvp.smartblocker.presentation.main.number.list.list_contact

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.tarasovvp.smartblocker.domain.enums.NumberDataFiltering
import com.tarasovvp.smartblocker.domain.usecases.ListContactUseCase
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import com.tarasovvp.smartblocker.presentation.mappers.ContactWithFilterUIMapper
import com.tarasovvp.smartblocker.presentation.ui_models.ContactWithFilterUIModel
import com.tarasovvp.smartblocker.utils.extensions.digitsTrimmed
import com.tarasovvp.smartblocker.utils.extensions.isContaining
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListContactViewModel
    @Inject
    constructor(
        application: Application,
        private val listContactUseCase: ListContactUseCase,
        private val contactWithFilterUIMapper: ContactWithFilterUIMapper,
        val savedStateHandle: SavedStateHandle,
    ) : BaseViewModel(application) {
        val contactListLiveData = MutableLiveData<List<ContactWithFilterUIModel>>()
        val filteredContactListLiveData = MutableLiveData<List<ContactWithFilterUIModel>>()

        fun getContactsWithFilters(refreshing: Boolean) {
            if (refreshing.not()) showProgress()
            launch {
                val contactList = listContactUseCase.allContactWithFilters()
                contactListLiveData.postValue(contactWithFilterUIMapper.mapToUIModelList(contactList))
                hideProgress()
            }
        }

        fun getFilteredContactList(
            contactList: List<ContactWithFilterUIModel>,
            searchQuery: String,
            filterIndexes: ArrayList<Int>,
        ) {
            launch {
                val filteredContactList =
                    if (searchQuery.isBlank() && filterIndexes.isEmpty()) {
                        contactList
                    } else {
                        contactList.filter { contactWithFilter ->
                            ((contactWithFilter.contactName isContaining searchQuery || contactWithFilter.number.digitsTrimmed() isContaining searchQuery)) &&
                                (
                                    contactWithFilter.filterWithFilteredNumberUIModel.filterType == Constants.BLOCKER &&
                                        filterIndexes.contains(
                                            NumberDataFiltering.CONTACT_WITH_BLOCKER.ordinal,
                                        ) ||
                                        contactWithFilter.filterWithFilteredNumberUIModel.filterType == Constants.PERMISSION &&
                                        filterIndexes.contains(
                                            NumberDataFiltering.CONTACT_WITH_PERMISSION.ordinal,
                                        ) ||
                                        filterIndexes.isEmpty()
                                )
                        }
                    }
                filteredContactListLiveData.postValue(filteredContactList)
                hideProgress()
            }
        }
    }
