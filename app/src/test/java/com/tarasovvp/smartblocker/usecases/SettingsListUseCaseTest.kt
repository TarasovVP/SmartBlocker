package com.tarasovvp.smartblocker.usecases

import com.tarasovvp.smartblocker.domain.models.Review
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.usecase.settings.settings_list.SettingsListUseCaseImpl
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SettingsListUseCaseTest {

    @Mock
    private lateinit var realDataBaseRepository: RealDataBaseRepository

    private lateinit var settingsListUseCaseImpl: SettingsListUseCaseImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        settingsListUseCaseImpl = SettingsListUseCaseImpl(realDataBaseRepository)
    }
    fun insertReview(review: Review, result: () -> Unit) = realDataBaseRepository.insertReview(review) {
        result.invoke()
    }
}