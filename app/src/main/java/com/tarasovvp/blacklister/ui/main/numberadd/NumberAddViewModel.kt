package com.tarasovvp.blacklister.ui.main.numberadd

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.blacklister.model.BlackNumber
import com.tarasovvp.blacklister.model.WhiteNumber
import com.tarasovvp.blacklister.repository.BlackNumberRepository
import com.tarasovvp.blacklister.repository.WhiteNumberRepository
import com.tarasovvp.blacklister.ui.base.BaseViewModel

class NumberAddViewModel(application: Application) : BaseViewModel(application) {

    private val blackNumberRepository = BlackNumberRepository
    private val whiteNumberRepository = WhiteNumberRepository

    val insertBlackNumberLiveData = MutableLiveData<BlackNumber>()
    val insertWhiteNumberLiveData = MutableLiveData<WhiteNumber>()
    val deleteNumberLiveData = MutableLiveData<Boolean>()

    fun insertBlackNumber(blackNumber: BlackNumber) {
        showProgress()
        launch {
            blackNumberRepository.insertBlackNumber(blackNumber) {
                insertBlackNumberLiveData.postValue(blackNumber)
            }
            hideProgress()
        }
    }

    fun deleteBlackNumber(blackNumber: BlackNumber) {
        showProgress()
        launch {
            blackNumberRepository.deleteBlackNumber(blackNumber) {
                deleteNumberLiveData.postValue(true)
            }
            hideProgress()
        }
    }

    fun insertWhiteNumber(whiteNumber: WhiteNumber) {
        showProgress()
        launch {
            whiteNumberRepository.insertWhiteNumber(whiteNumber) {
                insertWhiteNumberLiveData.postValue(whiteNumber)
            }
            hideProgress()
        }
    }

    fun deleteWhiteNumber(whiteNumber: WhiteNumber) {
        launch {
            whiteNumberRepository.deleteWhiteNumber(whiteNumber) {
                deleteNumberLiveData.postValue(true)
            }
        }
    }

}