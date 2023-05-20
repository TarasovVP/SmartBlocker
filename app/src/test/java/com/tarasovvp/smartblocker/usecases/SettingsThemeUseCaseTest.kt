package com.tarasovvp.smartblocker.usecases

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_APP_THEME
import com.tarasovvp.smartblocker.domain.repository.DataStoreRepository
import com.tarasovvp.smartblocker.domain.usecases.SettingsThemeUseCase
import com.tarasovvp.smartblocker.presentation.main.settings.settings_theme.SettingsThemeUseCaseImpl
import io.mockk.*
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class SettingsThemeUseCaseTest {

    @MockK
    private lateinit var dataStoreRepository: DataStoreRepository

    private lateinit var settingsThemeUseCase: SettingsThemeUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        settingsThemeUseCase = SettingsThemeUseCaseImpl(dataStoreRepository)
    }

    @Test
    fun getAppThemeTest() = runBlocking{
        val appTheme = TEST_APP_THEME
        val flow = flowOf(appTheme)
        coEvery { dataStoreRepository.getAppTheme() } returns flow
        val result = settingsThemeUseCase.getAppTheme().single()
        assertEquals(appTheme, result)
        coVerify { dataStoreRepository.getAppTheme() }
    }

    @Test
    fun setAppThemeTest() = runBlocking{
        val appTheme = TEST_APP_THEME
        coEvery { dataStoreRepository.setAppTheme(appTheme) } just Runs
        settingsThemeUseCase.setAppTheme(appTheme)
        coVerify { dataStoreRepository.setAppTheme(appTheme) }
    }
}