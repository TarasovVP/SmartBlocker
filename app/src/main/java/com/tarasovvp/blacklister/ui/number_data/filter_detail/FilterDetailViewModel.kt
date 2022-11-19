package com.tarasovvp.blacklister.ui.number_data.filter_detail

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.blacklister.constants.Constants
import com.tarasovvp.blacklister.enums.FilterAction
import com.tarasovvp.blacklister.extensions.*
import com.tarasovvp.blacklister.model.Filter
import com.tarasovvp.blacklister.repository.CallRepository
import com.tarasovvp.blacklister.repository.ContactRepository
import com.tarasovvp.blacklister.repository.FilterRepository
import com.tarasovvp.blacklister.ui.base.BaseViewModel
import com.tarasovvp.blacklister.model.NumberData
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

class FilterDetailViewModel(application: Application) : BaseViewModel(application) {

    private val contactRepository = ContactRepository
    private val filterRepository = FilterRepository
    private val callRepository = CallRepository

    val contactCallListLiveData = MutableLiveData<ArrayList<NumberData>>()
    val filterActionLiveData = MutableLiveData<Filter>()

    fun getQueryContactCallList(filter: Filter) {
        Log.e("filterAddTAG", "AddViewModel checkContactListByFilter filter $filter")
        showProgress()
        launch {
            val calls = async { callRepository.getQueryCallList(filter) }
            val contacts = async { contactRepository.getAllContacts() }
            awaitAll(calls, contacts)
            val callList = calls.await().orEmpty()
            val contactList = contacts.await().orEmpty()
            val matchedContactList = arrayListOf<NumberData>()
            val supposedFilteredList = arrayListOf<NumberData>()
            contactList.forEach { numberData ->
                if (numberData.numberData.digitsTrimmed()
                        .contains(filter.addFilter())
                        .isTrue()) matchedContactList.add(numberData.apply {
                    searchText = filter.addFilter()
                })
                else if (numberData.numberData.digitsTrimmed()
                        .contains(filter.filter).isTrue()
                ) supposedFilteredList.add(numberData.apply {
                    searchText = filter.filter
                })
            }
            val numberDataList = ArrayList<NumberData>().apply {
                addAll(callList)
                addAll(supposedFilteredList)
                sortBy {
                    it.numberData.replace(Constants.PLUS_CHAR.toString(), String.EMPTY)
                }
                addAll(matchedContactList)
                distinctBy {
                    it.numberData
                }
            }
            contactCallListLiveData.postValue(ArrayList(numberDataList))
            Log.e("filterAddTAG", "AddViewModel queryContactListLiveData $filter")
            hideProgress()
        }
    }

    fun deleteFilter(filter: Filter) {
        showProgress()
        launch {
            Log.e("filterAddTAG",
                "AddViewModel deleteFilter filter $filter")
            filterRepository.deleteFilterList(listOf(filter)) {
                filterActionLiveData.postValue(filter.apply {
                    filterAction = FilterAction.FILTER_ACTION_DELETE
                })
            }
            hideProgress()
        }
    }

    fun updateFilter(filter: Filter) {
        showProgress()
        launch {
            filterRepository.updateFilter(filter) {
                filterActionLiveData.postValue(filter.apply {
                    filterAction = FilterAction.FILTER_ACTION_CHANGE
                })
            }
            hideProgress()
        }
    }

    fun insertFilter(filter: Filter) {
        showProgress()
        launch {
            Log.e("filterAddTAG",
                "FilterDetailViewModel insertFilter filter $filter")
            filterRepository.insertFilter(filter) {
                filterActionLiveData.postValue(filter.apply {
                    filterAction = FilterAction.FILTER_ACTION_ADD
                })
            }
            hideProgress()
        }
    }
}