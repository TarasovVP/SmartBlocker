package com.tarasovvp.smartblocker.domain.usecase

import com.tarasovvp.smartblocker.data.database.entities.CountryCode
import com.tarasovvp.smartblocker.domain.repository.CountryCodeRepository
import javax.inject.Inject

class CountryCodeSearchUseCase @Inject constructor(private val countryCodeRepository: CountryCodeRepository)  {

    suspend fun getCountryCodeList(): List<CountryCode> = countryCodeRepository.getAllCountryCodes()
}