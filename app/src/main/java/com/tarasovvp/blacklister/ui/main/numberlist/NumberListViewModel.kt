package com.tarasovvp.blacklister.ui.main.numberlist

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tarasovvp.blacklister.model.BlackNumber
import com.tarasovvp.blacklister.model.Number
import com.tarasovvp.blacklister.model.WhiteNumber
import com.tarasovvp.blacklister.repository.BlackNumberRepository
import com.tarasovvp.blacklister.repository.WhiteNumberRepository
import com.tarasovvp.blacklister.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class NumberListViewModel(application: Application) : BaseViewModel(application) {

    val whiteNumberList = MutableLiveData<List<Number>>()
    val successDeleteNumberLiveData = MutableLiveData<Boolean>()

    private val whiteNumberRepository = WhiteNumberRepository
    private val blackNumberRepository = BlackNumberRepository
    val numberHashMapLiveData = MutableLiveData<HashMap<String, List<Number>>?>()

    fun getWhiteNumberList(isBlackList: Boolean) {
        viewModelScope.launch {
            try {
                val numberList = if (isBlackList) {
                    blackNumberRepository.allBlackNumbers()
                } else {
                    whiteNumberRepository.allWhiteNumbers()
                }
                whiteNumberList.postValue(numberList as? List<Number>)
            } catch (e: java.lang.Exception) {
                exceptionLiveData.postValue(e.localizedMessage)
            }
        }
    }

    fun getHashMapFromNumberList(numberList: List<Number>) {
        viewModelScope.launch {
            try {
                numberHashMapLiveData.postValue(
                    whiteNumberRepository.getHashMapFromNumberList(numberList)
                )
            } catch (e: java.lang.Exception) {
                exceptionLiveData.postValue(e.localizedMessage)
            }
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