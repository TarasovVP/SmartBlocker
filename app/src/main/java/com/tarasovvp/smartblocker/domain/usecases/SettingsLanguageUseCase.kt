package com.tarasovvp.smartblocker.domain.usecases

import kotlinx.coroutines.flow.Flow

interface SettingsLanguageUseCase {
    suspend fun getAppLanguage(): Flow<String?>

    suspend fun setAppLanguage(appLang: String)
}
