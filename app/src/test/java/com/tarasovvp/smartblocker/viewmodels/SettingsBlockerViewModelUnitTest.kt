package com.tarasovvp.smartblocker.viewmodels

import com.tarasovvp.smartblocker.UnitTestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.domain.entities.dbentities.CountryCode
import com.tarasovvp.smartblocker.domain.sealedclasses.Result
import com.tarasovvp.smartblocker.domain.usecases.SettingsBlockerUseCase
import com.tarasovvp.smartblocker.presentation.main.settings.settingsblocker.SettingsBlockerViewModel
import com.tarasovvp.smartblocker.presentation.mappers.CountryCodeUIMapper
import com.tarasovvp.smartblocker.presentation.uimodels.CountryCodeUIModel
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
class SettingsBlockerViewModelUnitTest : BaseViewModelUnitTest<SettingsBlockerViewModel>() {
    @MockK
    private lateinit var useCase: SettingsBlockerUseCase

    @MockK
    private lateinit var countryCodeUIMapper: CountryCodeUIMapper

    @MockK(relaxed = true)
    private lateinit var resultMock: (Result<Unit>) -> Unit

    override fun createViewModel() = SettingsBlockerViewModel(application, useCase, countryCodeUIMapper)

    @Test
    fun getBlockerTurnOnTest() =
        runTest {
            val blockerTurnOn = true
            coEvery { useCase.getBlockerTurnOn() } returns flowOf(blockerTurnOn)
            viewModel.getBlockerTurnOn()
            advanceUntilIdle()
            coVerify { useCase.getBlockerTurnOn() }
            assertEquals(blockerTurnOn, viewModel.blockerTurnOnLiveData.getOrAwaitValue())
        }

    @Test
    fun getBlockHiddenTest() =
        runTest {
            val blockHidden = true
            coEvery { useCase.getBlockHidden() } returns flowOf(blockHidden)
            viewModel.getBlockHidden()
            advanceUntilIdle()
            coVerify { useCase.getBlockHidden() }
            assertEquals(blockHidden, viewModel.blockHiddenLiveData.getOrAwaitValue())
        }

    @Test
    fun changeBlockHiddenTest() =
        runTest {
            val blockHidden = true
            val expectedResult = Result.Success<Unit>()
            every { application.isNetworkAvailable } returns true
            coEvery { useCase.changeBlockHidden(eq(blockHidden), eq(true), any()) } answers {
                val callback = thirdArg<(Result<Unit>) -> Unit>()
                callback.invoke(expectedResult)
            }
            viewModel.changeBlockHidden(blockHidden)
            advanceUntilIdle()
            coVerify { useCase.changeBlockHidden(blockHidden, true, any()) }
            assertEquals(blockHidden, viewModel.blockHiddenLiveData.getOrAwaitValue())
        }

    @Test
    fun getCurrentCountryCodeTest() =
        runTest {
            val countryCode = CountryCode()
            val countryCodeUIModel = CountryCodeUIModel()
            coEvery { useCase.getCurrentCountryCode() } returns flowOf(countryCode)
            coEvery { countryCodeUIMapper.mapToUIModel(countryCode) } returns countryCodeUIModel
            viewModel.getCurrentCountryCode()
            advanceUntilIdle()
            coVerify { useCase.getCurrentCountryCode() }
            assertEquals(countryCodeUIModel, viewModel.currentCountryCodeLiveData.getOrAwaitValue())
        }

    @Test
    fun setCurrentCountryCodeTest() =
        runTest {
            val countryCode = CountryCode()
            val countryCodeUIModel = CountryCodeUIModel()
            val expectedResult = Result.Success<Unit>()
            every { application.isNetworkAvailable } returns true
            coEvery { useCase.changeCountryCode(eq(countryCode), eq(true), any()) } answers {
                val callback = thirdArg<(Result<Unit>) -> Unit>()
                callback.invoke(expectedResult)
            }
            coEvery { countryCodeUIMapper.mapFromUIModel(countryCodeUIModel) } returns countryCode
            viewModel.changeCountryCode(countryCodeUIModel)
            advanceUntilIdle()
            coVerify { useCase.changeCountryCode(eq(countryCode), eq(true), any()) }
            assertEquals(
                countryCodeUIModel,
                viewModel.successCurrentCountryCodeLiveData.getOrAwaitValue(),
            )
        }
}
