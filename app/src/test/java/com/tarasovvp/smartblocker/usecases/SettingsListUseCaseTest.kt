package com.tarasovvp.smartblocker.usecases

import com.tarasovvp.smartblocker.domain.models.Review
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import javax.inject.Inject

@RunWith(MockitoJUnitRunner::class)
class SettingsListUseCaseTest @Inject constructor(private val realDataBaseRepository: RealDataBaseRepository) {

    fun insertReview(review: Review, result: () -> Unit) = realDataBaseRepository.insertReview(review) {
        result.invoke()
    }
}