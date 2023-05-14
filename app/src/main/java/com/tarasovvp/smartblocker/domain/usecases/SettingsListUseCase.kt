package com.tarasovvp.smartblocker.domain.usecases

import com.tarasovvp.smartblocker.domain.entities.models.Review
import kotlinx.coroutines.flow.Flow

interface SettingsListUseCase {

    suspend fun getAppLanguage(): Flow<String?>

    fun insertReview(review: Review, result: () -> Unit)
}