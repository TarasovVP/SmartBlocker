package com.tarasovvp.smartblocker.ui.number_data.details.filter_details

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.constants.Constants
import com.tarasovvp.smartblocker.extensions.EMPTY
import com.tarasovvp.smartblocker.models.Filter
import com.tarasovvp.smartblocker.models.NumberData
import com.tarasovvp.smartblocker.repository.CallRepository
import com.tarasovvp.smartblocker.repository.ContactRepository
import com.tarasovvp.smartblocker.repository.FilterRepository
import com.tarasovvp.smartblocker.repository.FilteredCallRepository
import com.tarasovvp.smartblocker.ui.base.BaseViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

class FilterDetailsViewModel(application: Application) : BaseViewModel(application) {

    private val contactRepository = ContactRepository
    private val filterRepository = FilterRepository
    private val callRepository = CallRepository
    private val filteredCallRepository = FilteredCallRepository

    val numberDataListLiveData = MutableLiveData<ArrayList<NumberData>>()
    val filteredCallListLiveData = MutableLiveData<ArrayList<NumberData>>()

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
            val numberDataList = ArrayList<NumberData>().apply {
                addAll(callList)
                addAll(contactList)
                sortBy {
                    it.numberData.replace(Constants.PLUS_CHAR.toString(), String.EMPTY)
                }
                distinctBy {
                    it.numberData
                }
            }
            filteredNumberDataList(filter, numberDataList)
            Log.e("filterAddTAG", "AddViewModel queryContactListLiveData $filter")
        }
    }

    private fun filteredNumberDataList(filter: Filter?, numberDataList: ArrayList<NumberData>) {
        launch {
            Log.e("filterAddTAG",
                "AddViewModel filteredNumberDataList filter $filter")
            numberDataListLiveData.postValue(contactRepository.filteredNumberDataList(filter,
                numberDataList))
            hideProgress()
        }
    }

    fun filteredCallsByFilter(filter: String) {
        launch {
            val filteredCallList = filteredCallRepository.filteredCallsByFilter(filter)
            filteredCallList?.let { filteredCalls ->
                filteredCallListLiveData.postValue(ArrayList(filteredCalls.sortedByDescending { it.callDate }))
            }
        }
    }

    fun deleteFilter(filter: Filter) {
        showProgress()
        launch {
            Log.e("filterAddTAG",
                "AddViewModel deleteFilter filter $filter")
            filterRepository.deleteFilterList(listOf(filter)) {
                filterActionLiveData.postValue(filter)
            }
            hideProgress()
        }
    }

    fun updateFilter(filter: Filter) {
        showProgress()
        launch {
            filterRepository.updateFilter(filter) {
                filterActionLiveData.postValue(filter)
            }
            hideProgress()
        }
    }
}