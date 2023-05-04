package com.tarasovvp.smartblocker.presentation.main.settings.settings_blocker

import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.sealed_classes.OperationResult
import com.tarasovvp.smartblocker.domain.usecase.SettingsBlockerUseCase
import javax.inject.Inject

class SettingsBlockerUseCaseImpl @Inject constructor(
    private val realDataBaseRepository: RealDataBaseRepository
): SettingsBlockerUseCase {

    override fun changeBlockHidden(blockHidden: Boolean, result: (OperationResult<Unit>) -> Unit) = realDataBaseRepository.changeBlockHidden(blockHidden) { operationResult ->
        result.invoke(operationResult)
    }
}