package com.tarasovvp.smartblocker.domain.usecases

import com.tarasovvp.smartblocker.domain.entities.models.Review
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import kotlinx.coroutines.flow.Flow

interface SettingsListUseCase {

    suspend fun getAppLanguage(): Flow<String?>

    fun insertReview(review: Review, result: (Result<Unit>) -> Unit)
}