package com.tarasovvp.blacklister.ui.main.blacknumberadd

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tarasovvp.blacklister.model.BlackNumber
import com.tarasovvp.blacklister.provider.BlackNumberRepositoryImpl
import com.tarasovvp.blacklister.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class BlackNumberAddViewModel(application: Application) : BaseViewModel(application) {

    private val blackNumberRepository = BlackNumberRepositoryImpl

    val blackNumberLiveData = MutableLiveData<BlackNumber>()

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

}