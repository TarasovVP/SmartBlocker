package com.tarasovvp.smartblocker.usecases

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.usecase.settings.settings_blocker.SettingsBlockerUseCase
import com.tarasovvp.smartblocker.domain.usecase.settings.settings_blocker.SettingsBlockerUseCaseImpl
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

class SettingsBlockerUseCaseTest {

    @MockK
    private lateinit var realDataBaseRepository: RealDataBaseRepository

    @MockK(relaxed = true)
    private lateinit var resultMock: () -> Unit

    private lateinit var settingsBlockerUseCase: SettingsBlockerUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        settingsBlockerUseCase = SettingsBlockerUseCaseImpl(realDataBaseRepository)
    }

    @Test
    fun changeBlockHiddenTest() {
        Mockito.doAnswer {
            @Suppress("UNCHECKED_CAST")
            val result = it.arguments[1] as () -> Unit
            result.invoke()
        }.`when`(realDataBaseRepository).changeBlockHidden(eq(true), any())
        settingsBlockerUseCase.changeBlockHidden(true, resultMock)
        verify(resultMock).invoke()
    }
}