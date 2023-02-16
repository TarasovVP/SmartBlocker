package com.tarasovvp.smartblocker.ui.main.number.details.details_filter

import android.app.Application
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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import javax.inject.Inject

@HiltViewModel
class DetailsFilterViewModel @Inject constructor(
    application: Application,
    private val contactRepository: ContactRepository,
    private val filterRepository: FilterRepository,
    private val callRepository: CallRepository,
    private val filteredCallRepository: FilteredCallRepository
) : BaseViewModel(application) {

    val numberDataListLiveData = MutableLiveData<ArrayList<NumberData>>()
    val filteredCallListLiveData = MutableLiveData<ArrayList<NumberData>>()

    val filterActionLiveData = MutableLiveData<Filter>()

    fun getQueryContactCallList(filter: Filter, color: Int) {
        showProgress()
        launch {
            val calls = async { callRepository.getQueryCallList(filter) }
            val contacts = async { contactRepository.getAllContacts() }
            val callList = calls.await()
            val contactList = contacts.await()
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
            filteredNumberDataList(filter, numberDataList, color)
        }
    }

    private fun filteredNumberDataList(
        filter: Filter?,
        numberDataList: ArrayList<NumberData>,
        color: Int,
    ) {
        launch {
            numberDataListLiveData.postValue(
                contactRepository.filteredNumberDataList(
                    filter,
                    numberDataList, color
                )
            )
            hideProgress()
        }
    }

    fun filteredCallsByFilter(filter: String) {
        launch {
            val filteredCallList = filteredCallRepository.filteredCallsByFilter(filter)
            filteredCallList.let { filteredCalls ->
                filteredCallListLiveData.postValue(ArrayList(filteredCalls))
            }
        }
    }

    fun deleteFilter(filter: Filter) {
        showProgress()
        launch {
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