package com.tarasovvp.smartblocker.domain.usecase.settings.settings_blocker

import com.tarasovvp.smartblocker.domain.models.entities.CountryCode

interface SettingsBlockerUseCase {

    fun changeBlockHidden(blockHidden: Boolean, result: () -> Unit)

    suspend fun getCountryCodeWithCountry(country: String): CountryCode?
}