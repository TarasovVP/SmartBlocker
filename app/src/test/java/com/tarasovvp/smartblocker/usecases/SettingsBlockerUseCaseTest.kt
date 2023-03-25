package com.tarasovvp.smartblocker.usecases

import com.tarasovvp.smartblocker.domain.repository.CountryCodeRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import javax.inject.Inject

@RunWith(MockitoJUnitRunner::class)
class SettingsBlockerUseCaseTest @Inject constructor(
    private val realDataBaseRepository: RealDataBaseRepository,
    private val countryCodeRepository: CountryCodeRepository
) {

    fun changeBlockHidden(blockHidden: Boolean, result: () -> Unit) = realDataBaseRepository.changeBlockHidden(blockHidden) {
        result.invoke()
    }

    suspend fun getCountryCodeWithCountry(country: String) = countryCodeRepository.getCountryCodeWithCountry(country)
}