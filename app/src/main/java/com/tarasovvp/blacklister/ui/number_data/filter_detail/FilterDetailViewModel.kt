package com.tarasovvp.blacklister.ui.number_data.filter_detail

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.blacklister.constants.Constants
import com.tarasovvp.blacklister.enums.FilterAction
import com.tarasovvp.blacklister.extensions.EMPTY
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.model.Filter
import com.tarasovvp.blacklister.model.LogCall
import com.tarasovvp.blacklister.repository.CallRepository
import com.tarasovvp.blacklister.repository.ContactRepository
import com.tarasovvp.blacklister.repository.FilterRepository
import com.tarasovvp.blacklister.ui.base.BaseViewModel
import com.tarasovvp.blacklister.ui.number_data.NumberData
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
            val contacts = async { contactRepository.getQueryContactList(filter) }
            val calls = async { callRepository.getQueryCallList(filter) }
            awaitAll(contacts, calls)
            val contactList = contacts.await().orEmpty()
            val callList = calls.await().orEmpty()
            val numberDataList = ArrayList<NumberData>().apply {
                addAll(contactList)
                addAll(callList)
            }
            numberDataList.sortBy {
                when (it) {
                    is Contact -> it.trimmedPhone.replace(Constants.PLUS_CHAR.toString(),
                        String.EMPTY)
                    is LogCall -> it.number.replace(Constants.PLUS_CHAR.toString(), String.EMPTY)
                    else -> String.EMPTY
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