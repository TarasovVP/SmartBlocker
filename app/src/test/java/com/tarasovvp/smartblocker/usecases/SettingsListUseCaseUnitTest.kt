package com.tarasovvp.smartblocker.usecases

import com.tarasovvp.smartblocker.UnitTestUtils
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_EMAIL
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_REVIEW
import com.tarasovvp.smartblocker.domain.entities.models.Feedback
import com.tarasovvp.smartblocker.domain.repository.DataStoreRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.sealedclasses.Result
import com.tarasovvp.smartblocker.domain.usecases.SettingsListUseCase
import com.tarasovvp.smartblocker.presentation.main.settings.settingslist.SettingsListUseCaseImpl
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import junit.framework.TestCase
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class SettingsListUseCaseUnitTest {
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
    fun getAppLanguageTest() =
        runBlocking {
            val appLanguage = UnitTestUtils.TEST_APP_LANGUAGE
            coEvery { dataStoreRepository.getAppLang() } returns flowOf(appLanguage)
            val result = settingsListUseCase.getAppLanguage().single()
            TestCase.assertEquals(appLanguage, result)
            coVerify { dataStoreRepository.getAppLang() }
        }

    @Test
    fun insertReviewTest() {
        val feedback = Feedback(TEST_EMAIL, TEST_REVIEW, 1000)
        every { realDataBaseRepository.insertFeedback(eq(feedback), any()) } answers {
            resultMock.invoke(Result.Success())
        }
        settingsListUseCase.insertFeedback(feedback, resultMock)
        verify { resultMock.invoke(Result.Success()) }
    }
}
