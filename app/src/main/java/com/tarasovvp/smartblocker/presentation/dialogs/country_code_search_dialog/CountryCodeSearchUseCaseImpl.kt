package com.tarasovvp.smartblocker.presentation.dialogs.country_code_search_dialog

import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode
import com.tarasovvp.smartblocker.domain.repository.CountryCodeRepository
import com.tarasovvp.smartblocker.domain.usecases.CountryCodeSearchUseCase
import javax.inject.Inject

class CountryCodeSearchUseCaseImpl @Inject constructor(private val countryCodeRepository: CountryCodeRepository,

) : CountryCodeSearchUseCase {

    override suspend fun getCountryCodeList(): List<CountryCode> {
        return countryCodeRepository.allCountryCodes()
    }
}