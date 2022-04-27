package com.tarasovvp.blacklister.ui.main.blacknumberlist

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tarasovvp.blacklister.model.BlackNumber
import com.tarasovvp.blacklister.provider.BlackNumberRepositoryImpl
import com.tarasovvp.blacklister.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class BlackNumberListViewModel(application: Application) : BaseViewModel(application) {

    val blackNumberList = MutableLiveData<List<BlackNumber>>()

    private val blackNumberRepository = BlackNumberRepositoryImpl
    val blackNumberHashMapLiveData = MutableLiveData<HashMap<String, List<BlackNumber>>?>()

    fun getBlackNumberList() {
        viewModelScope.launch {
            blackNumberList.postValue(blackNumberRepository.allBlackNumbers())
        }
    }

    fun deleteBlackNumber(blackNumber: BlackNumber) {
        viewModelScope.launch {
            blackNumberRepository.deleteBlackNumber(blackNumber)
            blackNumberList.postValue(blackNumberRepository.allBlackNumbers())
        }
    }

    fun getHashMapFromBlackNumberList(blackNumberList: List<BlackNumber>) {
        viewModelScope.launch {
            blackNumberHashMapLiveData.postValue(
                blackNumberRepository.getHashMapFromBlackNumberList(
                    blackNumberList
                )
            )
        }
    }
}