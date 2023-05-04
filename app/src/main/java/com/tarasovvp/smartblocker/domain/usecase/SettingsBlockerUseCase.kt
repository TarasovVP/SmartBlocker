package com.tarasovvp.smartblocker.domain.usecase

import com.tarasovvp.smartblocker.domain.sealed_classes.OperationResult

interface SettingsBlockerUseCase {

    fun changeBlockHidden(blockHidden: Boolean, result: (OperationResult<Unit>) -> Unit)
}