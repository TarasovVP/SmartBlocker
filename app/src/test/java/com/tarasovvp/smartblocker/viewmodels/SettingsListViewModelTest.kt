package com.tarasovvp.smartblocker.viewmodels

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_EMAIL
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_REVIEW
import com.tarasovvp.smartblocker.domain.models.Review
import com.tarasovvp.smartblocker.domain.usecase.settings.settings_list.SettingsListUseCase
import com.tarasovvp.smartblocker.presentation.main.settings.settings_list.SettingsListViewModel
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class SettingsListViewModelTest: BaseViewModelTest<SettingsListViewModel>() {

    @Mock
    private lateinit var useCase: SettingsListUseCase

    override fun createViewModel() = SettingsListViewModel(application, useCase)

    @Test
    fun insertReviewTest() {
        val review = Review(TEST_EMAIL, TEST_REVIEW, 1000)
        Mockito.doAnswer {
            @Suppress("UNCHECKED_CAST")
            val result = it.arguments[1] as () -> Unit
            result.invoke()
        }.`when`(useCase).insertReview(eq(review),any())
        viewModel.insertReview(review)
        TestCase.assertEquals(review.message, viewModel.successReviewLiveData.value)
    }
}