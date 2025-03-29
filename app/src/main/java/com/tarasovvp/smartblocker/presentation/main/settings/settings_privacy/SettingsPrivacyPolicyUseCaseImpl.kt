package com.tarasovvp.smartblocker.presentation.main.settings.settings_privacy

import com.tarasovvp.smartblocker.domain.repository.DataStoreRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.SettingsPrivacyPolicyUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsPrivacyPolicyUseCaseImpl
    @Inject
    constructor(
        private val dataStoreRepository: DataStoreRepository,
        private val realDataBaseRepository: RealDataBaseRepository,
    ) : SettingsPrivacyPolicyUseCase {
        override suspend fun getAppLanguage(): Flow<String?> {
            return dataStoreRepository.getAppLang()
        }

        override suspend fun getPrivacyPolicy(
            appLang: String,
            result: (Result<String>) -> Unit,
        ) {
            return realDataBaseRepository.getPrivacyPolicy(appLang, result)
        }
    }
