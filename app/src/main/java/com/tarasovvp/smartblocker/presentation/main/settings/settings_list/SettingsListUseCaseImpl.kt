package com.tarasovvp.smartblocker.presentation.main.settings.settings_list

import com.tarasovvp.smartblocker.domain.entities.models.Review
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.usecases.SettingsListUseCase
import javax.inject.Inject

class SettingsListUseCaseImpl @Inject constructor(private val realDataBaseRepository: RealDataBaseRepository):
    SettingsListUseCase {

    override fun insertReview(review: Review, result: () -> Unit) = realDataBaseRepository.insertReview(review) {
        result.invoke()
    }
}