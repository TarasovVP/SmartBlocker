package com.tarasovvp.smartblocker

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.tarasovvp.smartblocker.TestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.database.entities.CountryCode
import com.tarasovvp.smartblocker.local.SharedPrefs
import com.tarasovvp.smartblocker.repository.CountryCodeRepository
import com.tarasovvp.smartblocker.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.ui.main.settings.settings_blocker.SettingsBlockerViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class SettingsBlockerViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var application: Application

    @Mock
    private lateinit var realDataBaseRepository: RealDataBaseRepository

    @Mock
    private lateinit var countryCodeRepository: CountryCodeRepository

    private var settingsBlockerViewModel: SettingsBlockerViewModel? = null

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(StandardTestDispatcher())
        settingsBlockerViewModel =
            SettingsBlockerViewModel(application, realDataBaseRepository, countryCodeRepository)
    }

    @Test
    fun testChangeBlockHidden() = runTest {
        `when`(realDataBaseRepository.changeBlockHidden(true) {})
            .thenReturn(Unit)
        settingsBlockerViewModel?.changeBlockHidden(true)
        advanceUntilIdle()
        val resultSuccess = settingsBlockerViewModel?.successBlockHiddenLiveData?.getOrAwaitValue()
        assertEquals(resultSuccess, true)
        `when`(realDataBaseRepository.changeBlockHidden(false) {})
            .thenReturn(Unit)
        settingsBlockerViewModel?.changeBlockHidden(false)
        advanceUntilIdle()
        val resultSuccess2 = settingsBlockerViewModel?.successBlockHiddenLiveData?.getOrAwaitValue()
        assertEquals(resultSuccess2, false)
    }

    @Test
    fun testCountryCodeWithCountry() = runTest {
        val queryCountry = "ua"
        val expectedCountryCode = CountryCode(countryCode = "+380", country = "UA")
        `when`(countryCodeRepository.getCountryCodeWithCountry(queryCountry))
            .thenReturn(expectedCountryCode)

        settingsBlockerViewModel?.getCountryCodeWithCountry(queryCountry)
        advanceUntilIdle()
        val resultCountry = settingsBlockerViewModel?.countryCodeLiveData?.getOrAwaitValue()
        assertEquals(expectedCountryCode.country, resultCountry?.country)
        assertEquals(expectedCountryCode.countryCode, resultCountry?.countryCode)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}