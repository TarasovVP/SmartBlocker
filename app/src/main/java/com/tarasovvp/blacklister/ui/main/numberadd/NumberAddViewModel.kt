package com.tarasovvp.blacklister.ui.main.numberadd

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.blacklister.model.BlackNumber
import com.tarasovvp.blacklister.model.Number
import com.tarasovvp.blacklister.model.WhiteNumber
import com.tarasovvp.blacklister.repository.BlackNumberRepository
import com.tarasovvp.blacklister.repository.WhiteNumberRepository
import com.tarasovvp.blacklister.ui.base.BaseViewModel

class NumberAddViewModel(application: Application) : BaseViewModel(application) {

    private val blackNumberRepository = BlackNumberRepository
    private val whiteNumberRepository = WhiteNumberRepository

    val existBlackNumberLiveData = MutableLiveData<BlackNumber?>()
    val existWhiteNumberLiveData = MutableLiveData<WhiteNumber?>()
    val insertNumberLiveData = MutableLiveData<String>()
    val deleteNumberLiveData = MutableLiveData<String>()

    fun checkNumberExist(number: String, isBlackNumber: Boolean) {
        showProgress()
        launch {
            if (isBlackNumber) {
                val blackNumber = blackNumberRepository.getBlackNumber(number)
                existBlackNumberLiveData.postValue(blackNumber)
            } else {
                existWhiteNumberLiveData.postValue(whiteNumberRepository.getWhiteNumber(number))
            }
            hideProgress()
        }
    }

    fun insertNumber(number: Number) {
        showProgress()
        launch {
            if (number.isBlackNumber) {
                blackNumberRepository.insertBlackNumber(number as BlackNumber) {
                    insertNumberLiveData.postValue(number.number)
                }
            } else {
                whiteNumberRepository.insertWhiteNumber(number as WhiteNumber) {
                    insertNumberLiveData.postValue(number.number)
                }
            }
            hideProgress()
        }
    }

    fun deleteNumber(number: Number) {
        launch {
            if (number.isBlackNumber) {
                blackNumberRepository.deleteBlackNumber(number as BlackNumber) {
                    deleteNumberLiveData.postValue(number.number)
                }
            } else {
                whiteNumberRepository.deleteWhiteNumber(number as WhiteNumber) {
                    deleteNumberLiveData.postValue(number.number)
                }
            }
        }
    }

}