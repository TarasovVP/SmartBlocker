package com.tarasovvp.smartblocker.domain.usecase

import com.tarasovvp.smartblocker.domain.models.entities.CountryCode

interface CountryCodeSearchUseCase  {

    suspend fun getCountryCodeList(): List<CountryCode>
}