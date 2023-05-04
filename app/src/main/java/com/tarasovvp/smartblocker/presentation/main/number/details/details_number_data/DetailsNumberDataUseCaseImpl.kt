package com.tarasovvp.smartblocker.presentation.main.number.details.details_number_data

import com.tarasovvp.smartblocker.domain.repository.CountryCodeRepository
import com.tarasovvp.smartblocker.domain.repository.FilterRepository
import com.tarasovvp.smartblocker.domain.repository.FilteredCallRepository
import com.tarasovvp.smartblocker.domain.usecase.DetailsNumberDataUseCase
import javax.inject.Inject

class DetailsNumberDataUseCaseImpl @Inject constructor(
    private val countryCodeRepository: CountryCodeRepository,
    private val filterRepository: FilterRepository,
    private val filteredCallRepository: FilteredCallRepository
): DetailsNumberDataUseCase {

    override suspend fun filterListWithNumber(number: String) = filterRepository.queryFilterList(number)

    override suspend fun filteredCallsByNumber(number: String) = filteredCallRepository.filteredCallsByNumber(number)

    override suspend fun getCountryCode(code: Int) = countryCodeRepository.getCountryCodeWithCode(code)
}