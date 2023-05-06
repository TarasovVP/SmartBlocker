package com.tarasovvp.smartblocker.domain.usecases

interface SettingsBlockerUseCase {

    fun changeBlockHidden(blockHidden: Boolean, result: () -> Unit)
}