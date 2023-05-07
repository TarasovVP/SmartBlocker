package com.tarasovvp.smartblocker.presentation.main.number.details.details_number_data

import com.tarasovvp.smartblocker.domain.repository.CountryCodeRepository
import com.tarasovvp.smartblocker.domain.repository.FilterRepository
import com.tarasovvp.smartblocker.domain.repository.FilteredCallRepository
import com.tarasovvp.smartblocker.domain.usecases.DetailsNumberDataUseCase
import javax.inject.Inject

class DetailsNumberDataUseCaseImpl @Inject constructor(
    private val countryCodeRepository: CountryCodeRepository,
    private val filterRepository: FilterRepository,
    private val filteredCallRepository: FilteredCallRepository
): DetailsNumberDataUseCase {

    override suspend fun allFilterWithCountryCodesByNumber(number: String) = filterRepository.allFilterWithCountryCodesByNumber(number)

    override suspend fun allFilteredCallsByNumber(number: String) = filteredCallRepository.allFilteredCallsByNumber(number)

    override suspend fun getCountryCodeWithCode(code: Int) = countryCodeRepository.getCountryCodeWithCode(code)
}