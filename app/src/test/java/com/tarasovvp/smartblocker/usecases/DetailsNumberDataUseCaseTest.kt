package com.tarasovvp.smartblocker.usecases

import com.tarasovvp.smartblocker.domain.repository.CountryCodeRepository
import com.tarasovvp.smartblocker.domain.repository.FilterRepository
import com.tarasovvp.smartblocker.domain.repository.FilteredCallRepository
import com.tarasovvp.smartblocker.domain.usecase.number.details.details_number_data.DetailsNumberDataUseCaseImpl
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class DetailsNumberDataUseCaseTest {

    @Mock
    private lateinit var countryCodeRepository: CountryCodeRepository

    @Mock
    private lateinit var filterRepository: FilterRepository

    @Mock
    private lateinit var filteredCallRepository: FilteredCallRepository

    private lateinit var detailsNumberDataUseCaseImpl: DetailsNumberDataUseCaseImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        detailsNumberDataUseCaseImpl = DetailsNumberDataUseCaseImpl(countryCodeRepository, filterRepository, filteredCallRepository)
    }

    suspend fun filterListWithNumber(number: String) = filterRepository.queryFilterList(number)

    suspend fun filteredCallsByNumber(number: String) = filteredCallRepository.filteredCallsByNumber(number)

    suspend fun getCountryCode(code: Int) = countryCodeRepository.getCountryCodeWithCode(code)
}