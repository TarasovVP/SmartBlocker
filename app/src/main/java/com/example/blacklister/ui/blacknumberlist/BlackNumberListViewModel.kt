package com.example.blacklister.ui.blacknumberlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blacklister.model.BlackNumber
import com.example.blacklister.provider.BlackNumberRepositoryImpl
import kotlinx.coroutines.launch

class BlackNumberListViewModel : ViewModel() {

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