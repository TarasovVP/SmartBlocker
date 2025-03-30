package com.tarasovvp.smartblocker.domain.usecases

import com.tarasovvp.smartblocker.domain.sealedclasses.Result
import kotlinx.coroutines.flow.Flow

interface SettingsPrivacyPolicyUseCase {
    suspend fun getAppLanguage(): Flow<String?>

    suspend fun getPrivacyPolicy(
        appLang: String,
        result: (Result<String>) -> Unit,
    )
}
