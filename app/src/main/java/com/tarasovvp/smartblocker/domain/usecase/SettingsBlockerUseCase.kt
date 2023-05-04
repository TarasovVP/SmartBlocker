package com.tarasovvp.smartblocker.domain.usecase

interface SettingsBlockerUseCase {

    fun changeBlockHidden(blockHidden: Boolean, result: () -> Unit)
}