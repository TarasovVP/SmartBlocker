package com.tarasovvp.smartblocker.usecases

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.tarasovvp.smartblocker.TestUtils.TEST_COUNTRY
import com.tarasovvp.smartblocker.domain.models.entities.CountryCode
import com.tarasovvp.smartblocker.domain.repository.CountryCodeRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.usecase.settings.settings_blocker.SettingsBlockerUseCase
import com.tarasovvp.smartblocker.domain.usecase.settings.settings_blocker.SettingsBlockerUseCaseImpl
import junit.framework.TestCase
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SettingsBlockerUseCaseTest {

    @Mock
    private lateinit var realDataBaseRepository: RealDataBaseRepository

    @Mock
    private lateinit var countryCodeRepository: CountryCodeRepository

    private lateinit var settingsBlockerUseCase: SettingsBlockerUseCase

    private val resultMock = mock<() -> Unit>()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        settingsBlockerUseCase = SettingsBlockerUseCaseImpl(realDataBaseRepository, countryCodeRepository)
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

    @Test
    fun countryCodeWithCountryTest() = runTest {
        val expectedCountryCode = CountryCode(countryCode = "+380", country = TEST_COUNTRY)
        Mockito.`when`(countryCodeRepository.getCountryCodeWithCountry(TEST_COUNTRY))
            .thenReturn(expectedCountryCode)

        val resultCountry = settingsBlockerUseCase.getCountryCodeWithCountry(TEST_COUNTRY)
        TestCase.assertEquals(expectedCountryCode.country, resultCountry?.country)
        TestCase.assertEquals(expectedCountryCode.countryCode, resultCountry?.countryCode)
    }
}