package com.tarasovvp.smartblocker.ui.number_data.filtered_calls_detail

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.models.NumberData
import com.tarasovvp.smartblocker.repository.FilteredCallRepository
import com.tarasovvp.smartblocker.ui.base.BaseViewModel

class FilteredCallsDetailViewModel(application: Application) : BaseViewModel(application) {

    private val filteredCallRepository = FilteredCallRepository

    val callListLiveData = MutableLiveData<ArrayList<NumberData>>()

    fun filteredCallsByFilter(filter: String) {
        showProgress()
        launch {
            val filteredCallList = filteredCallRepository.filteredCallsByFilter(filter)
            filteredCallList?.let { filteredCalls ->
                callListLiveData.postValue(ArrayList(filteredCalls.sortedByDescending { it.callDate }))
            }
            hideProgress()
        }
    }
}