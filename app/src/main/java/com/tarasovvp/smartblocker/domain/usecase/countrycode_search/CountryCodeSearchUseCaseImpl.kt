package com.tarasovvp.smartblocker.domain.usecase.countrycode_search

import com.tarasovvp.smartblocker.domain.models.entities.CountryCode
import com.tarasovvp.smartblocker.domain.repository.CountryCodeRepository
import javax.inject.Inject

class CountryCodeSearchUseCaseImpl @Inject constructor(private val countryCodeRepository: CountryCodeRepository): CountryCodeSearchUseCase  {

    override suspend fun getCountryCodeList(): List<CountryCode> = countryCodeRepository.getAllCountryCodes()
}