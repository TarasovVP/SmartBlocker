package com.tarasovvp.smartblocker.viewmodels

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.tarasovvp.smartblocker.domain.models.Review
import com.tarasovvp.smartblocker.domain.usecase.settings.settings_list.SettingsListUseCase
import com.tarasovvp.smartblocker.presentation.main.settings.settings_list.SettingsListViewModel
import junit.framework.TestCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@Suppress("UNCHECKED_CAST")
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class SettingsListViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var application: Application

    @Mock
    private lateinit var settingsListUseCase: SettingsListUseCase

    private lateinit var viewModel: SettingsListViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(StandardTestDispatcher())
        viewModel =
            SettingsListViewModel(application, settingsListUseCase)
    }

    @Test
    fun insertReviewTest() {
        val review = Review("user","review", 1000)
        Mockito.doAnswer {
            val result = it.arguments[1] as () -> Unit
            result.invoke()
        }.`when`(settingsListUseCase).insertReview(eq(review),any())
        viewModel.insertReview(review)
        TestCase.assertEquals(review.message, viewModel.successReviewLiveData.value)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}