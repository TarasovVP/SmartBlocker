package com.tarasovvp.smartblocker.viewmodels

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.tarasovvp.smartblocker.TestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.domain.models.entities.CountryCode
import com.tarasovvp.smartblocker.domain.usecase.settings.settings_blocker.SettingsBlockerUseCase
import com.tarasovvp.smartblocker.presentation.main.settings.settings_blocker.SettingsBlockerViewModel
import junit.framework.TestCase.assertEquals
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner.Silent::class)
class SettingsBlockerViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var application: Application

    @Mock
    private lateinit var settingsBlockerUseCase: SettingsBlockerUseCase

    private lateinit var viewModel: SettingsBlockerViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(StandardTestDispatcher())
        viewModel =
            SettingsBlockerViewModel(application, settingsBlockerUseCase)
    }

    @Test
    fun changeBlockHiddenTest() {
        Mockito.doAnswer {
            val result = it.arguments[1] as () -> Unit
            result.invoke()
        }.`when`(settingsBlockerUseCase).changeBlockHidden(eq(true), any())
        viewModel.changeBlockHidden(true)
        assertEquals(viewModel.successBlockHiddenLiveData.value, true)
    }

    @Test
    fun countryCodeWithCountryTest() = runTest {
        val queryCountry = "ua"
        val expectedCountryCode = CountryCode(countryCode = "+380", country = "UA")
        Mockito.`when`(settingsBlockerUseCase.getCountryCodeWithCountry(queryCountry))
            .thenReturn(expectedCountryCode)

        viewModel.getCountryCodeWithCountry(queryCountry)
        advanceUntilIdle()
        val resultCountry = viewModel.countryCodeLiveData.getOrAwaitValue()
        assertEquals(expectedCountryCode.country, resultCountry?.country)
        assertEquals(expectedCountryCode.countryCode, resultCountry?.countryCode)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}