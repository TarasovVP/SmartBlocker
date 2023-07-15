package com.tarasovvp.smartblocker.domain.usecases

import com.tarasovvp.smartblocker.domain.entities.models.Feedback
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import kotlinx.coroutines.flow.Flow

interface SettingsListUseCase {

    suspend fun getAppLanguage(): Flow<String?>

    fun insertFeedback(feedback: Feedback, result: (Result<Unit>) -> Unit)
}