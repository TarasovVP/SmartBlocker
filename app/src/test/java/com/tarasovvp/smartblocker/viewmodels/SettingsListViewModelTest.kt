package com.tarasovvp.smartblocker.viewmodels

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_EMAIL
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_REVIEW
import com.tarasovvp.smartblocker.domain.entities.models.Review
import com.tarasovvp.smartblocker.domain.usecases.SettingsListUseCase
import com.tarasovvp.smartblocker.presentation.main.settings.settings_list.SettingsListViewModel
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test

@ExperimentalCoroutinesApi
class SettingsListViewModelTest: BaseViewModelTest<SettingsListViewModel>() {

    @MockK
    private lateinit var useCase: SettingsListUseCase

    @MockK(relaxed = true)
    private lateinit var resultMock: (Result<Unit>) -> Unit

    override fun createViewModel() = SettingsListViewModel(application, useCase)

    @Test
    fun insertReviewTest() {
        val review = Review(TEST_EMAIL, TEST_REVIEW, 1000)
        every { application.isNetworkAvailable } returns true
        coEvery { useCase.insertReview(eq(review), eq(resultMock)) } answers {
            resultMock.invoke(Result.Success())
        }
        viewModel.insertReview(review)
        coVerify { useCase.insertReview(review, any()) }
        verify { resultMock.invoke(Result.Success()) }
        verify { viewModel.successReviewLiveData.postValue(review.message) }
    }
}