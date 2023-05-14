package com.tarasovvp.smartblocker.presentation.main.authorization.onboarding

import com.tarasovvp.smartblocker.domain.repository.DataStoreRepository
import com.tarasovvp.smartblocker.domain.usecases.OnBoardingUseCase
import javax.inject.Inject

class OnBoardingUseCaseImpl @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
): OnBoardingUseCase {

    override suspend fun setOnBoardingSeen(onBoardingSeen: Boolean) {
        return dataStoreRepository.setOnBoardingSeen(true)
    }
}