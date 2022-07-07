package com.tarasovvp.blacklister.ui.main.numberadd

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tarasovvp.blacklister.model.BlackNumber
import com.tarasovvp.blacklister.model.WhiteNumber
import com.tarasovvp.blacklister.repository.BlackNumberRepository
import com.tarasovvp.blacklister.repository.WhiteNumberRepository
import com.tarasovvp.blacklister.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class NumberAddViewModel(application: Application) : BaseViewModel(application) {

    private val blackNumberRepository = BlackNumberRepository
    private val whiteNumberRepository = WhiteNumberRepository

    val insertBlackNumberLiveData = MutableLiveData<BlackNumber>()
    val insertWhiteNumberLiveData = MutableLiveData<WhiteNumber>()
    val deleteNumberLiveData = MutableLiveData<Boolean>()

    fun insertBlackNumber(blackNumber: BlackNumber) {
        viewModelScope.launch {
            try {
                blackNumberRepository.insertBlackNumber(blackNumber) {
                    insertBlackNumberLiveData.postValue(blackNumber)
                }
            } catch (e: Exception) {
                exceptionLiveData.postValue(e.localizedMessage)
            }
        }
    }

    fun deleteBlackNumber(blackNumber: BlackNumber) {
        viewModelScope.launch {
            try {
                blackNumberRepository.deleteBlackNumber(blackNumber) {
                    deleteNumberLiveData.postValue(true)
                }
            } catch (e: java.lang.Exception) {
                exceptionLiveData.postValue(e.localizedMessage)
            }
        }
    }

    fun insertWhiteNumber(whiteNumber: WhiteNumber) {
        viewModelScope.launch {
            try {
                whiteNumberRepository.insertWhiteNumber(whiteNumber) {
                    insertWhiteNumberLiveData.postValue(whiteNumber)
                }
            } catch (e: Exception) {
                exceptionLiveData.postValue(e.localizedMessage)
            }
        }
    }

    fun deleteWhiteNumber(whiteNumber: WhiteNumber) {
        viewModelScope.launch {
            try {
                whiteNumberRepository.deleteWhiteNumber(whiteNumber) {
                    deleteNumberLiveData.postValue(true)
                }
            } catch (e: java.lang.Exception) {
                exceptionLiveData.postValue(e.localizedMessage)
            }
        }
    }

}