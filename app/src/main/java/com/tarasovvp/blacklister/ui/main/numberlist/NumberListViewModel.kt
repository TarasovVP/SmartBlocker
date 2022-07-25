package com.tarasovvp.blacklister.ui.main.numberlist

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.blacklister.model.BlackNumber
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.model.Number
import com.tarasovvp.blacklister.model.WhiteNumber
import com.tarasovvp.blacklister.repository.BlackNumberRepository
import com.tarasovvp.blacklister.repository.ContactRepository
import com.tarasovvp.blacklister.repository.WhiteNumberRepository
import com.tarasovvp.blacklister.ui.base.BaseViewModel

class NumberListViewModel(application: Application) : BaseViewModel(application) {

    val numberListLiveData = MutableLiveData<List<Number>?>()
    val successDeleteNumberLiveData = MutableLiveData<Boolean>()

    private val whiteNumberRepository = WhiteNumberRepository
    private val blackNumberRepository = BlackNumberRepository
    private val contactRepository = ContactRepository

    val numberHashMapLiveData = MutableLiveData<HashMap<String, List<Number>>?>()

    fun getNumberList(isBlackList: Boolean) {
        launch {
            showProgress()
            val numberList = if (isBlackList) {
                blackNumberRepository.allBlackNumbers()
            } else {
                whiteNumberRepository.allWhiteNumbers()
            }
            numberListLiveData.postValue(numberList)
            hideProgress()
        }
    }

    fun getHashMapFromNumberList(numberList: List<Number>) {
        launch {
            showProgress()
            numberHashMapLiveData.postValue(
                whiteNumberRepository.getHashMapFromNumberList(numberList)
            )
            hideProgress()
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun deleteNumberList(numberList: List<Number>, isBlackList: Boolean) {
        showProgress()
        launch {
            if (isBlackList) {
                blackNumberRepository.deleteBlackNumberList(blackNumberList = numberList as List<BlackNumber>) {
                    successDeleteNumberLiveData.postValue(true)
                }
            } else {
                whiteNumberRepository.deleteWhiteNumberList(numberList as List<WhiteNumber>) {
                    successDeleteNumberLiveData.postValue(true)
                }
            }
            hideProgress()
        }
    }
}