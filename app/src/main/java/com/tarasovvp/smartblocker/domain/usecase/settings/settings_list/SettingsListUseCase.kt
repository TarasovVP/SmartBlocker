package com.tarasovvp.smartblocker.domain.usecase.settings.settings_list

import com.tarasovvp.smartblocker.domain.models.Review

interface SettingsListUseCase {

    fun insertReview(review: Review, result: () -> Unit)
}