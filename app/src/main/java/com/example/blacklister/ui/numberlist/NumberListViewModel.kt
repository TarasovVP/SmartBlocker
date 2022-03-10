package com.example.blacklister.ui.numberlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blacklister.model.BlackNumber
import com.example.blacklister.provider.BlackNumberRepositoryImpl
import kotlinx.coroutines.launch

class NumberListViewModel : ViewModel() {

    val blackNumberList = MutableLiveData<List<BlackNumber>>()

    private val blackNumberRepository = BlackNumberRepositoryImpl

    fun getBlackNumberList() {
        viewModelScope.launch {
            blackNumberList.postValue(blackNumberRepository.allBlackNumbers())
        }
    }
}