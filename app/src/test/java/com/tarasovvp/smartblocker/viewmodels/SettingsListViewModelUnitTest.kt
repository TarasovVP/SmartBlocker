package com.tarasovvp.smartblocker.viewmodels

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_APP_LANGUAGE
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_EMAIL
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_REVIEW
import com.tarasovvp.smartblocker.UnitTestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.domain.entities.models.Feedback
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.SettingsListUseCase
import com.tarasovvp.smartblocker.presentation.main.settings.settings_list.SettingsListViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test

@ExperimentalCoroutinesApi
class SettingsListViewModelUnitTest : BaseViewModelUnitTest<SettingsListViewModel>() {
    @MockK
    private lateinit var useCase: SettingsListUseCase

    override fun createViewModel() = SettingsListViewModel(application, useCase)

    @Test
    fun getAppLanguageTest() =
        runTest {
            val appLang = TEST_APP_LANGUAGE
            coEvery { useCase.getAppLanguage() } returns flowOf(appLang)
            viewModel.getAppLanguage()
            advanceUntilIdle()
            coVerify { useCase.getAppLanguage() }
            assertEquals(appLang, viewModel.appLanguageLiveData.getOrAwaitValue())
        }

    @Test
    fun insertReviewTest() {
        val expectedResult = Result.Success<Unit>()
        val feedback = Feedback(TEST_EMAIL, TEST_REVIEW, 1000)
        every { application.isNetworkAvailable } returns true
        coEvery { useCase.insertFeedback(eq(feedback), any()) } answers {
            val callback = secondArg<(Result<Unit>) -> Unit>()
            callback.invoke(expectedResult)
        }
        viewModel.insertFeedback(feedback)
        coVerify { useCase.insertFeedback(feedback, any()) }
        assertEquals(feedback.message, viewModel.successFeedbackLiveData.getOrAwaitValue())
    }
}
