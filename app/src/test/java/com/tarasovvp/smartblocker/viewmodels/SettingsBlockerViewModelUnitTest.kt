package com.tarasovvp.smartblocker.viewmodels

import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode
import com.tarasovvp.smartblocker.domain.usecases.SettingsBlockerUseCase
import com.tarasovvp.smartblocker.presentation.main.settings.settings_blocker.SettingsBlockerViewModel
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import io.mockk.*
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test

@ExperimentalCoroutinesApi
class SettingsBlockerViewModelUnitTest: BaseViewModelUnitTest<SettingsBlockerViewModel>() {

    @MockK
    private lateinit var useCase: SettingsBlockerUseCase

    override fun createViewModel() = SettingsBlockerViewModel(application, useCase)

    @Test
    fun getBlockerTurnOnTest() = runTest {
        val blockerTurnOn = true
        coEvery { useCase.getBlockerTurnOn() } returns flowOf(blockerTurnOn)
        viewModel.getBlockerTurnOn()
        advanceUntilIdle()
        coVerify { useCase.getBlockerTurnOn() }
        assertEquals(blockerTurnOn, viewModel.blockerTurnOnLiveData.value)
    }

    @Test
    fun setBlockerTurnOffTest() = runTest {
        val blockerTurnOff = true
        coEvery { useCase.setBlockerTurnOn(blockerTurnOff) } just Runs
        viewModel.setBlockerTurnOn(blockerTurnOff)
        advanceUntilIdle()
        coVerify { useCase.setBlockerTurnOn(blockerTurnOff) }
        assertEquals(blockerTurnOff, viewModel.blockerTurnOnLiveData.value)
    }

    @Test
    fun getBlockHiddenTest() = runTest {
        val blockHidden = true
        coEvery { useCase.getBlockHidden() } returns flowOf(blockHidden)
        viewModel.getBlockHidden()
        advanceUntilIdle()
        coVerify { useCase.getBlockHidden() }
        assertEquals(blockHidden, viewModel.blockHiddenLiveData.value)
    }

    @Test
    fun setBlockHiddenTest() = runTest {
        val blockHidden = true
        coEvery { useCase.setBlockHidden(blockHidden) } just Runs
        viewModel.setBlockHidden(blockHidden)
        advanceUntilIdle()
        coVerify { useCase.setBlockHidden(blockHidden) }
        assertEquals(blockHidden, viewModel.blockHiddenLiveData.value)
    }
    @Test
    fun changeBlockHiddenTest() {
        val blockHidden = true
        val expectedResult = Result.Success<Unit>()
        every { application.isNetworkAvailable } returns true
        every { useCase.changeBlockHidden(eq(blockHidden), eq(true), any()) } answers {
            val callback = thirdArg<(Result<Unit>) -> Unit>()
            callback.invoke(expectedResult)
        }
        viewModel.changeBlockHidden(blockHidden)
        verify { useCase.changeBlockHidden(blockHidden, true, any()) }
        assertEquals(blockHidden, viewModel.successBlockHiddenLiveData.value)
    }

    @Test
    fun getCurrentCountryCodeTest() = runTest{
        val countryCode = CountryCode()
        coEvery { useCase.getCurrentCountryCode() } returns flowOf(countryCode)
        viewModel.getCurrentCountryCode()
        advanceUntilIdle()
        coVerify { useCase.getCurrentCountryCode() }
        assertEquals(countryCode, viewModel.currentCountryCodeLiveData.value)
    }

    @Test
    fun setCurrentCountryCodeTest() = runTest {
        val countryCode = CountryCode()
        coEvery { useCase.setCurrentCountryCode(countryCode) } just Runs
        viewModel.setCurrentCountryCode(countryCode)
        advanceUntilIdle()
        coVerify { useCase.setCurrentCountryCode(countryCode) }
        assertEquals(countryCode, viewModel.currentCountryCodeLiveData.value)
    }
}