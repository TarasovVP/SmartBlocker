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

    val insertBlackNumberLiveData = MutableLiveData<BlackNumber>()
    val insertWhiteNumberLiveData = MutableLiveData<WhiteNumber>()
    val existBlackNumberLiveData = MutableLiveData<BlackNumber?>()
    val existWhiteNumberLiveData = MutableLiveData<WhiteNumber?>()
    val deleteNumberLiveData = MutableLiveData<Boolean>()

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
                    insertBlackNumberLiveData.postValue(number)
                }
            } else {
                whiteNumberRepository.insertWhiteNumber(number as WhiteNumber) {
                    insertWhiteNumberLiveData.postValue(number)
                }
            }
            hideProgress()
        }
    }

    fun deleteNumber(number: Number) {
        launch {
            if (number.isBlackNumber) {
                blackNumberRepository.deleteBlackNumber(number as BlackNumber) {
                    deleteNumberLiveData.postValue(true)
                }
            } else {
                whiteNumberRepository.deleteWhiteNumber(number as WhiteNumber) {
                    deleteNumberLiveData.postValue(true)
                }
            }
        }
    }

}