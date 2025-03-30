package com.tarasovvp.smartblocker.presentation.main.settings.settingslist

import com.tarasovvp.smartblocker.domain.entities.models.Feedback
import com.tarasovvp.smartblocker.domain.repository.DataStoreRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.sealedclasses.Result
import com.tarasovvp.smartblocker.domain.usecases.SettingsListUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsListUseCaseImpl
    @Inject
    constructor(
        private val dataStoreRepository: DataStoreRepository,
        private val realDataBaseRepository: RealDataBaseRepository,
    ) : SettingsListUseCase {
        override suspend fun getAppLanguage(): Flow<String?> {
            return dataStoreRepository.getAppLang()
        }

        override fun insertFeedback(
            feedback: Feedback,
            result: (Result<Unit>) -> Unit,
        ) = realDataBaseRepository.insertFeedback(feedback) {
            result.invoke(it)
        }
    }
