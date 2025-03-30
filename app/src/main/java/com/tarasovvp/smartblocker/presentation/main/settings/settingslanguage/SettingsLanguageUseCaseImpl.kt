package com.tarasovvp.smartblocker.presentation.main.settings.settingslanguage

import com.tarasovvp.smartblocker.domain.repository.DataStoreRepository
import com.tarasovvp.smartblocker.domain.usecases.SettingsLanguageUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsLanguageUseCaseImpl
    @Inject
    constructor(
        private val dataStoreRepository: DataStoreRepository,
    ) : SettingsLanguageUseCase {
        override suspend fun getAppLanguage(): Flow<String?> {
            return dataStoreRepository.getAppLang()
        }

        override suspend fun setAppLanguage(appLang: String) {
            dataStoreRepository.setAppLang(appLang)
        }
    }
