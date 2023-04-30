package com.tarasovvp.smartblocker.usecases

import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.usecase.settings.settings_blocker.SettingsBlockerUseCase
import com.tarasovvp.smartblocker.domain.usecase.settings.settings_blocker.SettingsBlockerUseCaseImpl
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test

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
        every { realDataBaseRepository.changeBlockHidden(eq(true), any()) } answers {
            resultMock.invoke()
        }
        settingsBlockerUseCase.changeBlockHidden(true, resultMock)
        verify(exactly = 1) { resultMock.invoke() }
    }
}