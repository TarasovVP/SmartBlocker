package com.tarasovvp.smartblocker.domain.usecases

import com.tarasovvp.smartblocker.domain.entities.models.Review

interface SettingsListUseCase {

    fun insertReview(review: Review, result: () -> Unit)
}