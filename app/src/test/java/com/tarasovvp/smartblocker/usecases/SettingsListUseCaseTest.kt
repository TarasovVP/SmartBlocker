package com.tarasovvp.smartblocker.usecases

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_EMAIL
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_REVIEW
import com.tarasovvp.smartblocker.domain.entities.models.Review
import com.tarasovvp.smartblocker.domain.repository.DataStoreRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.usecases.SettingsListUseCase
import com.tarasovvp.smartblocker.presentation.main.settings.settings_list.SettingsListUseCaseImpl
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class SettingsListUseCaseTest {

    @MockK
    private lateinit var dataStoreRepository: DataStoreRepository

    @MockK
    private lateinit var realDataBaseRepository: RealDataBaseRepository

    @MockK(relaxed = true)
    private lateinit var resultMock: (Result<Unit>) -> Unit

    private lateinit var settingsListUseCase: SettingsListUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        settingsListUseCase = SettingsListUseCaseImpl(dataStoreRepository, realDataBaseRepository)
    }

    @Test
    fun insertReviewTest() {
        val review = Review(TEST_EMAIL, TEST_REVIEW, 1000)
        every { realDataBaseRepository.insertReview(eq(review), any()) } answers {
            resultMock.invoke(Result.Success())
        }
        settingsListUseCase.insertReview(review, resultMock)
        verify { resultMock.invoke(Result.Success()) }
    }
}