package com.tarasovvp.smartblocker.domain.usecase.settings.settings_blocker

import com.tarasovvp.smartblocker.domain.repository.CountryCodeRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import javax.inject.Inject

class SettingsBlockerUseCaseImpl @Inject constructor(
    private val realDataBaseRepository: RealDataBaseRepository,
    private val countryCodeRepository: CountryCodeRepository
): SettingsBlockerUseCase {

    override suspend fun changeBlockHidden(blockHidden: Boolean, result: () -> Unit) = realDataBaseRepository.changeBlockHidden(blockHidden) {
        result.invoke()
    }

    override suspend fun getCountryCodeWithCountry(country: String) = countryCodeRepository.getCountryCodeWithCountry(country)
}