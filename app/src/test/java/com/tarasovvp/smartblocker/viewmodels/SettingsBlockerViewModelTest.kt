package com.tarasovvp.smartblocker.viewmodels

import com.tarasovvp.smartblocker.domain.usecase.settings.settings_blocker.SettingsBlockerUseCase
import com.tarasovvp.smartblocker.presentation.main.settings.settings_blocker.SettingsBlockerViewModel
import junit.framework.TestCase.assertEquals
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test

@ExperimentalCoroutinesApi
class SettingsBlockerViewModelTest: BaseViewModelTest<SettingsBlockerViewModel>() {

    @MockK
    private lateinit var useCase: SettingsBlockerUseCase

    override fun createViewModel() = SettingsBlockerViewModel(application, useCase)

    @Test
    fun changeBlockHiddenTest() {
        coEvery { useCase.changeBlockHidden(eq(true), any()) } answers {
            val result = secondArg<() -> Unit>()
            result.invoke()
        }
        viewModel.changeBlockHidden(true)
        assertEquals(viewModel.successBlockHiddenLiveData.value, true)
    }
}