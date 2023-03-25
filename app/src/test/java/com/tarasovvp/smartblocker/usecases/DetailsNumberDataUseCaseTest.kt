package com.tarasovvp.smartblocker.usecases

import com.tarasovvp.smartblocker.domain.repository.CountryCodeRepository
import com.tarasovvp.smartblocker.domain.repository.FilterRepository
import com.tarasovvp.smartblocker.domain.repository.FilteredCallRepository
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import javax.inject.Inject

@RunWith(MockitoJUnitRunner::class)
class DetailsNumberDataUseCaseTest @Inject constructor(
    private val countryCodeRepository: CountryCodeRepository,
    private val filterRepository: FilterRepository,
    private val filteredCallRepository: FilteredCallRepository
) {

    suspend fun filterListWithNumber(number: String) = filterRepository.queryFilterList(number)

    suspend fun filteredCallsByNumber(number: String) = filteredCallRepository.filteredCallsByNumber(number)

    suspend fun getCountryCode(code: Int) = countryCodeRepository.getCountryCodeWithCode(code)
}