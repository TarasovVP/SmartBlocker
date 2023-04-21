package com.tarasovvp.smartblocker.domain.usecase.settings.settings_blocker

interface SettingsBlockerUseCase {

    fun changeBlockHidden(blockHidden: Boolean, result: () -> Unit)
}