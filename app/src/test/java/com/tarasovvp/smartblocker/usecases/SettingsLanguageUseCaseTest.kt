package com.tarasovvp.smartblocker.usecases

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_APP_LANGUAGE
import com.tarasovvp.smartblocker.domain.repository.DataStoreRepository
import com.tarasovvp.smartblocker.domain.usecases.SettingsLanguageUseCase
import com.tarasovvp.smartblocker.presentation.main.settings.settings_language.SettingsLanguageUseCaseImpl
import io.mockk.*
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class SettingsLanguageUseCaseTest {


    @MockK
    private lateinit var dataStoreRepository: DataStoreRepository

    private lateinit var settingsLanguageUseCase: SettingsLanguageUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        settingsLanguageUseCase = SettingsLanguageUseCaseImpl(dataStoreRepository)
    }

    @Test
    fun getAppLanguageTest() = runBlocking{
        val appLang = TEST_APP_LANGUAGE
        val flow = flowOf(appLang)
        coEvery { dataStoreRepository.getAppLang() } returns flow
        val result = settingsLanguageUseCase.getAppLanguage().single()
        assertEquals(appLang, result)
        coVerify { dataStoreRepository.getAppLang() }
    }

    @Test
    fun setAppLanguageTest() = runBlocking{
        val appLang = TEST_APP_LANGUAGE
        coEvery { dataStoreRepository.setAppLang(appLang) } just Runs
        settingsLanguageUseCase.setAppLanguage(appLang)
        coVerify { dataStoreRepository.setAppLang(appLang) }
    }
}