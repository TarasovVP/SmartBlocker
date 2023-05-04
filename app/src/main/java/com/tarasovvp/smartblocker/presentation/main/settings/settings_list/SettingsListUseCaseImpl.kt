package com.tarasovvp.smartblocker.presentation.main.settings.settings_list

import com.tarasovvp.smartblocker.domain.models.Review
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.sealed_classes.OperationResult
import com.tarasovvp.smartblocker.domain.usecase.SettingsListUseCase
import javax.inject.Inject

class SettingsListUseCaseImpl @Inject constructor(private val realDataBaseRepository: RealDataBaseRepository):
    SettingsListUseCase {

    override fun insertReview(review: Review, result: (OperationResult<Unit>) -> Unit) = realDataBaseRepository.insertReview(review) { operationResult ->
        result.invoke(operationResult)
    }
}