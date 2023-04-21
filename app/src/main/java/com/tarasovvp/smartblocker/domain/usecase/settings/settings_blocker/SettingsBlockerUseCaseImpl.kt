package com.tarasovvp.smartblocker.domain.usecase.settings.settings_blocker

import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import javax.inject.Inject

class SettingsBlockerUseCaseImpl @Inject constructor(
    private val realDataBaseRepository: RealDataBaseRepository
): SettingsBlockerUseCase {

    override fun changeBlockHidden(blockHidden: Boolean, result: () -> Unit) = realDataBaseRepository.changeBlockHidden(blockHidden) {
        result.invoke()
    }
}