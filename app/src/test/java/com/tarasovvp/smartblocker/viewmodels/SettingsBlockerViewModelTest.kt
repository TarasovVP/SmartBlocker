package com.tarasovvp.smartblocker.viewmodels

import com.tarasovvp.smartblocker.domain.usecases.SettingsBlockerUseCase
import com.tarasovvp.smartblocker.presentation.main.settings.settings_blocker.SettingsBlockerViewModel
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test

@ExperimentalCoroutinesApi
class SettingsBlockerViewModelTest: BaseViewModelTest<SettingsBlockerViewModel>() {

    @MockK
    private lateinit var useCase: SettingsBlockerUseCase

    @MockK(relaxed = true)
    private lateinit var resultMock: (Result<Unit>) -> Unit

    override fun createViewModel() = SettingsBlockerViewModel(application, useCase)

    @Test
    fun changeBlockHiddenTest() {
        val blockHidden = true
        every { application.isNetworkAvailable } returns true
        coEvery { useCase.changeBlockHidden(eq(blockHidden), eq(true), any()) } answers {
            resultMock.invoke(Result.Success())
        }
        viewModel.changeBlockHidden(blockHidden)
        coVerify { useCase.changeBlockHidden(blockHidden, true, any()) }
        verify { resultMock.invoke(Result.Success()) }
        verify { viewModel.blockHiddenLiveData.postValue(blockHidden) }
    }
}