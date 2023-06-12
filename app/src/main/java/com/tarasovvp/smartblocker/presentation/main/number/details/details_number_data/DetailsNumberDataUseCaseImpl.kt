package com.tarasovvp.smartblocker.presentation.main.number.details.details_number_data

import com.tarasovvp.smartblocker.domain.repository.CountryCodeRepository
import com.tarasovvp.smartblocker.domain.repository.DataStoreRepository
import com.tarasovvp.smartblocker.domain.repository.FilterRepository
import com.tarasovvp.smartblocker.domain.repository.FilteredCallRepository
import com.tarasovvp.smartblocker.domain.usecases.DetailsNumberDataUseCase
import javax.inject.Inject

class DetailsNumberDataUseCaseImpl @Inject constructor(
    private val countryCodeRepository: CountryCodeRepository,
    private val filterRepository: FilterRepository,
    private val filteredCallRepository: FilteredCallRepository,
    private val dataStoreRepository: DataStoreRepository
): DetailsNumberDataUseCase {

    override suspend fun allFilterWithFilteredNumbersByNumber(number: String) = filterRepository.allFilterWithFilteredNumbersByNumber(number)

    override suspend fun allFilteredCallsByNumber(number: String, name: String) = filteredCallRepository.allFilteredCallsByNumber(number, name)

    override suspend fun getCurrentCountryCode() = dataStoreRepository.getCountryCode()

    override suspend fun getCountryCodeByCode(code: Int) = countryCodeRepository.getCountryCodeByCode(code)

    override suspend fun getBlockHidden() = dataStoreRepository.blockHidden()
}