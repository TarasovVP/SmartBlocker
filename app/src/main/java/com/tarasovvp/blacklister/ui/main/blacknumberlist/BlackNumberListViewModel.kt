package com.tarasovvp.blacklister.ui.main.blacknumberlist

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tarasovvp.blacklister.model.BlackNumber
import com.tarasovvp.blacklister.repository.BlackNumberRepository
import com.tarasovvp.blacklister.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class BlackNumberListViewModel(application: Application) : BaseViewModel(application) {

    val blackNumberList = MutableLiveData<List<BlackNumber>>()

    private val blackNumberRepository = BlackNumberRepository
    val blackNumberHashMapLiveData = MutableLiveData<HashMap<String, List<BlackNumber>>?>()

    fun getBlackNumberList() {
        viewModelScope.launch {
            try {
                blackNumberList.postValue(blackNumberRepository.allBlackNumbers())
            } catch (e: java.lang.Exception) {
                exceptionLiveData.postValue(e.localizedMessage)
            }
        }
    }

    fun getHashMapFromBlackNumberList(blackNumberList: List<BlackNumber>) {
        viewModelScope.launch {
            try {
                blackNumberHashMapLiveData.postValue(
                    blackNumberRepository.getHashMapFromBlackNumberList(blackNumberList)
                )
            } catch (e: java.lang.Exception) {
                exceptionLiveData.postValue(e.localizedMessage)
            }
        }
    }
}