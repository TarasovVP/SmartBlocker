package com.tarasovvp.smartblocker.viewmodels

import com.tarasovvp.smartblocker.domain.usecase.settings.settings_blocker.SettingsBlockerUseCase
import com.tarasovvp.smartblocker.presentation.main.settings.settings_blocker.SettingsBlockerViewModel
import junit.framework.TestCase.assertEquals
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner.Silent::class)
class SettingsBlockerViewModelTest: BaseViewModelTest<SettingsBlockerViewModel>() {

    @Mock
    private lateinit var useCase: SettingsBlockerUseCase

    override fun createViewModel() = SettingsBlockerViewModel(application, useCase)

    @Test
    fun changeBlockHiddenTest() {
        Mockito.doAnswer {
            @Suppress("UNCHECKED_CAST")
            val result = it.arguments[1] as () -> Unit
            result.invoke()
        }.`when`(useCase).changeBlockHidden(eq(true), any())
        viewModel.changeBlockHidden(true)
        assertEquals(viewModel.successBlockHiddenLiveData.value, true)
    }
}