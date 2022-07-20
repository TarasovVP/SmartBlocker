package com.tarasovvp.blacklister.ui.main.numberlist

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.blacklister.model.BlackNumber
import com.tarasovvp.blacklister.model.Number
import com.tarasovvp.blacklister.model.WhiteNumber
import com.tarasovvp.blacklister.repository.BlackNumberRepository
import com.tarasovvp.blacklister.repository.WhiteNumberRepository
import com.tarasovvp.blacklister.ui.base.BaseViewModel

class NumberListViewModel(application: Application) : BaseViewModel(application) {

    val numberListLiveData = MutableLiveData<List<Number>?>()
    val successDeleteNumberLiveData = MutableLiveData<Boolean>()

    private val whiteNumberRepository = WhiteNumberRepository
    private val blackNumberRepository = BlackNumberRepository
    val numberHashMapLiveData = MutableLiveData<HashMap<String, List<Number>>?>()

    fun getWhiteNumberList(isBlackList: Boolean) {
        launch {
            val numberList = if (isBlackList) {
                blackNumberRepository.allBlackNumbers()
            } else {
                whiteNumberRepository.allWhiteNumbers()
            }
            numberListLiveData.postValue(numberList)
        }
    }

    fun getHashMapFromNumberList(numberList: List<Number>) {
        launch {
            numberHashMapLiveData.postValue(
                whiteNumberRepository.getHashMapFromNumberList(numberList)
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun deleteNumberList(numberList: List<Number>, isBlackList: Boolean) {
        if (isBlackList) {
            blackNumberRepository.deleteBlackNumberList(blackNumberList = numberList as List<BlackNumber>) {
                successDeleteNumberLiveData.postValue(true)
            }
        } else {
            whiteNumberRepository.deleteWhiteNumberList(numberList as List<WhiteNumber>) {
                successDeleteNumberLiveData.postValue(true)
            }
        }

    }
}