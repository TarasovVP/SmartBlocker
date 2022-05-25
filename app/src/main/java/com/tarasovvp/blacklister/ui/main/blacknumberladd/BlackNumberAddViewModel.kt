package com.tarasovvp.blacklister.ui.main.blacknumberladd

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.tarasovvp.blacklister.model.BlackNumber
import com.tarasovvp.blacklister.provider.BlackNumberRepositoryImpl
import com.tarasovvp.blacklister.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class BlackNumberAddViewModel(application: Application) : BaseViewModel(application) {

    private val blackNumberRepository = BlackNumberRepositoryImpl

    private fun updateBlackNumber(isBlackList: Boolean, blackNumber: BlackNumber) {
        viewModelScope.launch {
            if (isBlackList) {
                blackNumberRepository.insertBlackNumber(blackNumber)
            } else {
                blackNumberRepository.deleteBlackNumber(blackNumber)
            }
        }
    }

}