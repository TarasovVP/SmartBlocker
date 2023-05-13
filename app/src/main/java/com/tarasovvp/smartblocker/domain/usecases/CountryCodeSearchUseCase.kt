package com.tarasovvp.smartblocker.domain.usecases

import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode

interface CountryCodeSearchUseCase  {

    suspend fun getCountryCodeList(): List<CountryCode>
}