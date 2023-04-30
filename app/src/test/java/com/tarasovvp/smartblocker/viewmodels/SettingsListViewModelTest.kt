package com.tarasovvp.smartblocker.viewmodels

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_EMAIL
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_REVIEW
import com.tarasovvp.smartblocker.domain.models.Review
import com.tarasovvp.smartblocker.domain.usecase.settings.settings_list.SettingsListUseCase
import com.tarasovvp.smartblocker.presentation.main.settings.settings_list.SettingsListViewModel
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test

@ExperimentalCoroutinesApi
class SettingsListViewModelTest: BaseViewModelTest<SettingsListViewModel>() {

    @MockK
    private lateinit var useCase: SettingsListUseCase

    override fun createViewModel() = SettingsListViewModel(application, useCase)

    @Test
    fun insertReviewTest() {
        val review = Review(TEST_EMAIL, TEST_REVIEW, 1000)
        coEvery { useCase.insertReview(eq(review), any()) } answers {
            val result = secondArg<() -> Unit>()
            result.invoke()
        }
        viewModel.insertReview(review)
        assertEquals(review.message, viewModel.successReviewLiveData.value)
    }
}