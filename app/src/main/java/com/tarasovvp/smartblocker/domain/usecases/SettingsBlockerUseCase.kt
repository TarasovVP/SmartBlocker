package com.tarasovvp.smartblocker.domain.usecases

import com.tarasovvp.smartblocker.domain.sealed_classes.Result

interface SettingsBlockerUseCase {

    fun changeBlockHidden(blockHidden: Boolean, isNetworkAvailable: Boolean, result: (Result<Unit>) -> Unit)
}