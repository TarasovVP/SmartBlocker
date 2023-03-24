package com.tarasovvp.smartblocker.domain.usecase.countrycode_search

import com.tarasovvp.smartblocker.domain.models.entities.CountryCode

interface CountryCodeSearchUseCase  {

    suspend fun getCountryCodeList(): List<CountryCode>
}