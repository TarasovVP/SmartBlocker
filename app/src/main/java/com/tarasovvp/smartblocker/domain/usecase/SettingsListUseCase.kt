package com.tarasovvp.smartblocker.domain.usecase

import com.tarasovvp.smartblocker.domain.models.Review

interface SettingsListUseCase {

    fun insertReview(review: Review, result: () -> Unit)
}