package com.tarasovvp.smartblocker.domain.usecase.settings.settings_list

import com.tarasovvp.smartblocker.domain.models.Review
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import javax.inject.Inject

class SettingsListUseCaseImpl @Inject constructor(private val realDataBaseRepository: RealDataBaseRepository): SettingsListUseCase {

    override fun insertReview(review: Review, result: () -> Unit) = realDataBaseRepository.insertReview(review) {
        result.invoke()
    }
}