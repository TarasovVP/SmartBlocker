package com.tarasovvp.smartblocker.presentation.dialogs.country_code_search_dialog

import com.tarasovvp.smartblocker.domain.mappers.CountryCodeUIMapper
import com.tarasovvp.smartblocker.domain.repository.CountryCodeRepository
import com.tarasovvp.smartblocker.domain.usecases.CountryCodeSearchUseCase
import com.tarasovvp.smartblocker.presentation.ui_models.CountryCodeUIModel
import javax.inject.Inject

class CountryCodeSearchUseCaseImpl @Inject constructor(private val countryCodeRepository: CountryCodeRepository,
    private val countryCodeUIMapper: CountryCodeUIMapper
) : CountryCodeSearchUseCase {

    override suspend fun getCountryCodeList(): List<CountryCodeUIModel> {
        val countryCodes = countryCodeRepository.allCountryCodes()
        return countryCodeUIMapper.mapToUIModelList(countryCodes)
    }
}