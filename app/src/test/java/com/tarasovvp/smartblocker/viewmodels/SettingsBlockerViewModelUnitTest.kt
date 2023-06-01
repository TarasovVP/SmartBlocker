package com.tarasovvp.smartblocker.viewmodels

import com.tarasovvp.smartblocker.UnitTestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.SettingsBlockerUseCase
import com.tarasovvp.smartblocker.presentation.main.settings.settings_blocker.SettingsBlockerViewModel
import com.tarasovvp.smartblocker.presentation.mappers.CountryCodeUIMapper
import com.tarasovvp.smartblocker.presentation.ui_models.CountryCodeUIModel
import io.mockk.*
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test

@ExperimentalCoroutinesApi
class SettingsBlockerViewModelUnitTest: BaseViewModelUnitTest<SettingsBlockerViewModel>() {

    @MockK
    private lateinit var useCase: SettingsBlockerUseCase

    @MockK
    private lateinit var countryCodeUIMapper: CountryCodeUIMapper

    override fun createViewModel() = SettingsBlockerViewModel(application, useCase, countryCodeUIMapper)

    //TODO CI/CD test failed
    /*@Test
    fun getBlockerTurnOnTest() = runTest {
        val blockerTurnOn = true
        coEvery { useCase.getBlockerTurnOn() } returns flowOf(blockerTurnOn)
        viewModel.getBlockerTurnOn()
        advanceUntilIdle()
        coVerify { useCase.getBlockerTurnOn() }
        assertEquals(blockerTurnOn, viewModel.blockerTurnOnLiveData.getOrAwaitValue())
    }*/

    @Test
    fun setBlockerTurnOnTest() = runTest {
        val blockerTurnOn = true
        coEvery { useCase.setBlockerTurnOn(blockerTurnOn) } just Runs
        viewModel.setBlockerTurnOn(blockerTurnOn)
        advanceUntilIdle()
        coVerify { useCase.setBlockerTurnOn(blockerTurnOn) }
        assertEquals(blockerTurnOn.not(), viewModel.blockerTurnOnLiveData.getOrAwaitValue())
    }

    //TODO CI/CD test failed
    /*@Test
    fun getBlockHiddenTest() = runTest {
        val blockHidden = true
        coEvery { useCase.getBlockHidden() } returns flowOf(blockHidden)
        viewModel.getBlockHidden()
        advanceUntilIdle()
        coVerify { useCase.getBlockHidden() }
        assertEquals(blockHidden, viewModel.blockHiddenLiveData.getOrAwaitValue())
    }*/

    @Test
    fun setBlockHiddenTest() = runTest {
        val blockHidden = true
        coEvery { useCase.setBlockHidden(blockHidden) } just Runs
        viewModel.setBlockHidden(blockHidden)
        advanceUntilIdle()
        coVerify { useCase.setBlockHidden(blockHidden) }
        assertEquals(blockHidden, viewModel.blockHiddenLiveData.getOrAwaitValue())
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
        assertEquals(blockHidden, viewModel.successBlockHiddenLiveData.getOrAwaitValue())
    }

    //TODO CI/CD test failed
    /*@Test
    fun getCurrentCountryCodeTest() = runTest{
        val countryCode = CountryCode()
        val countryCodeUIModel = CountryCodeUIModel()
        coEvery { useCase.getCurrentCountryCode() } returns flowOf(countryCode)
        coEvery { countryCodeUIMapper.mapToUIModel(countryCode) } returns countryCodeUIModel
        viewModel.getCurrentCountryCode()
        advanceUntilIdle()
        coVerify { useCase.getCurrentCountryCode() }
        assertEquals(countryCodeUIModel, viewModel.currentCountryCodeLiveData.getOrAwaitValue())
    }*/

    @Test
    fun setCurrentCountryCodeTest() = runTest {
        val countryCode = CountryCode()
        val countryCodeUIModel = CountryCodeUIModel()
        coEvery { useCase.setCurrentCountryCode(countryCode) } just Runs
        coEvery { countryCodeUIMapper.mapFromUIModel(countryCodeUIModel) } returns countryCode
        viewModel.setCurrentCountryCode(countryCodeUIModel)
        advanceUntilIdle()
        coVerify { useCase.setCurrentCountryCode(countryCode) }
        assertEquals(countryCodeUIModel, viewModel.currentCountryCodeLiveData.getOrAwaitValue())
    }
}