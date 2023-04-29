package com.tarasovvp.smartblocker.usecases

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_EMAIL
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_REVIEW
import com.tarasovvp.smartblocker.domain.models.Review
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.usecase.settings.settings_list.SettingsListUseCase
import com.tarasovvp.smartblocker.domain.usecase.settings.settings_list.SettingsListUseCaseImpl
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

class SettingsListUseCaseTest {

    @MockK
    private lateinit var realDataBaseRepository: RealDataBaseRepository

    @MockK(relaxed = true)
    private lateinit var resultMock: () -> Unit

    private lateinit var settingsListUseCase: SettingsListUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        settingsListUseCase = SettingsListUseCaseImpl(realDataBaseRepository)
    }

    @Test
    fun insertReviewTest() {
        val review = Review(TEST_EMAIL, TEST_REVIEW, 1000)
        Mockito.doAnswer {
            @Suppress("UNCHECKED_CAST")
            val result = it.arguments[1] as () -> Unit
            result.invoke()
        }.`when`(realDataBaseRepository).insertReview(eq(review), any())
        settingsListUseCase.insertReview(review, resultMock)
        verify(resultMock).invoke()
    }
}