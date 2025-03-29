package com.tarasovvp.smartblocker.domain.usecases

interface OnBoardingUseCase {
    suspend fun setOnBoardingSeen(onBoardingSeen: Boolean)
}
