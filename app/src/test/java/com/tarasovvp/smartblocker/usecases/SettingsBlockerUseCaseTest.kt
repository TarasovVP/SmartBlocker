package com.tarasovvp.smartblocker.usecases

import com.tarasovvp.smartblocker.domain.repository.CountryCodeRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.usecase.settings.settings_blocker.SettingsBlockerUseCaseImpl
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

//TODO unfinished
@Suppress
@RunWith(MockitoJUnitRunner::class)
class SettingsBlockerUseCaseTest {

    @Mock
    private lateinit var realDataBaseRepository: RealDataBaseRepository

    @Mock
    private lateinit var countryCodeRepository: CountryCodeRepository

    private lateinit var settingsBlockerUseCaseImpl: SettingsBlockerUseCaseImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        settingsBlockerUseCaseImpl = SettingsBlockerUseCaseImpl(realDataBaseRepository, countryCodeRepository)
    }

    fun changeBlockHidden(blockHidden: Boolean, result: () -> Unit) = realDataBaseRepository.changeBlockHidden(blockHidden) {
        result.invoke()
    }

    suspend fun getCountryCodeWithCountry(country: String) = countryCodeRepository.getCountryCodeWithCountry(country)
}