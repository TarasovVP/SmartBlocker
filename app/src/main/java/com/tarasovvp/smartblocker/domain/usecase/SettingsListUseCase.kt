package com.tarasovvp.smartblocker.domain.usecase

import com.tarasovvp.smartblocker.domain.models.Review
import com.tarasovvp.smartblocker.domain.sealed_classes.OperationResult

interface SettingsListUseCase {

    fun insertReview(review: Review, result: (OperationResult<Unit>) -> Unit)
}