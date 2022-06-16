package com.tarasovvp.blacklister.ui.main.numberadd

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tarasovvp.blacklister.model.BlackNumber
import com.tarasovvp.blacklister.model.WhiteNumber
import com.tarasovvp.blacklister.provider.BlackNumberRepositoryImpl
import com.tarasovvp.blacklister.provider.WhiteNumberRepositoryImpl
import com.tarasovvp.blacklister.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class NumberAddViewModel(application: Application) : BaseViewModel(application) {

    private val blackNumberRepository = BlackNumberRepositoryImpl
    private val whiteNumberRepository = WhiteNumberRepositoryImpl

    val checkBlackNumberNumberLiveData = MutableLiveData<BlackNumber>()
    val checkWhiteNumberNumberLiveData = MutableLiveData<WhiteNumber>()
    val insertBlackNumberLiveData = MutableLiveData<BlackNumber>()
    val insertWhiteNumberLiveData = MutableLiveData<WhiteNumber>()

    fun checkWhiteNumber(whiteNumber: WhiteNumber) {
        viewModelScope.launch {
            try {
                whiteNumberRepository.checkWhiteNumber(whiteNumber) {
                    checkWhiteNumberNumberLiveData.postValue(whiteNumber)
                }
            } catch (e: Exception) {
                exceptionLiveData.postValue(e.localizedMessage)
            }
        }
    }

    fun checkBlackNumber(blackNumber: BlackNumber) {
        viewModelScope.launch {
            try {
                blackNumberRepository.checkBlackNumber(blackNumber) {
                    checkBlackNumberNumberLiveData.postValue(blackNumber)
                }
            } catch (e: Exception) {
                exceptionLiveData.postValue(e.localizedMessage)
            }
        }
    }

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

}