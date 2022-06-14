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

    val blackNumberLiveData = MutableLiveData<BlackNumber>()
    val whiteNumberLiveData = MutableLiveData<WhiteNumber>()

    fun insertBlackNumber(blackNumber: BlackNumber) {
        viewModelScope.launch {
            try {
                blackNumberRepository.insertBlackNumber(blackNumber) {
                    blackNumberLiveData.postValue(blackNumber)
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
                    whiteNumberLiveData.postValue(whiteNumber)
                }
            } catch (e: Exception) {
                exceptionLiveData.postValue(e.localizedMessage)
            }
        }
    }

}