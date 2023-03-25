package com.tarasovvp.smartblocker.usecases

import com.tarasovvp.smartblocker.domain.models.entities.CountryCode
import com.tarasovvp.smartblocker.domain.repository.CountryCodeRepository
import com.tarasovvp.smartblocker.domain.usecase.countrycode_search.CountryCodeSearchUseCase
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import javax.inject.Inject

@RunWith(MockitoJUnitRunner::class)
class CountryCodeSearchUseCaseTest @Inject constructor(private val countryCodeRepository: CountryCodeRepository) {

    suspend fun getCountryCodeList(): List<CountryCode> = countryCodeRepository.getAllCountryCodes()
}