package com.tarasovvp.blacklister.ui.main.whitenumberlist

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tarasovvp.blacklister.model.WhiteNumber
import com.tarasovvp.blacklister.repository.WhiteNumberRepository
import com.tarasovvp.blacklister.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class WhiteNumberListViewModel(application: Application) : BaseViewModel(application) {

    val whiteNumberList = MutableLiveData<List<WhiteNumber>>()

    private val whiteNumberRepository = WhiteNumberRepository
    val whiteNumberHashMapLiveData = MutableLiveData<HashMap<String, List<WhiteNumber>>?>()

    fun getWhiteNumberList() {
        viewModelScope.launch {
            try {
                whiteNumberList.postValue(whiteNumberRepository.allWhiteNumbers())
            } catch (e: java.lang.Exception) {
                exceptionLiveData.postValue(e.localizedMessage)
            }
        }
    }

    fun getHashMapFromWhiteNumberList(whiteNumberList: List<WhiteNumber>) {
        viewModelScope.launch {
            try {
                whiteNumberHashMapLiveData.postValue(
                    whiteNumberRepository.getHashMapFromWhiteNumberList(whiteNumberList)
                )
            } catch (e: java.lang.Exception) {
                exceptionLiveData.postValue(e.localizedMessage)
            }
        }
    }
}