package com.tarasovvp.smartblocker.domain.usecases

import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode
import com.tarasovvp.smartblocker.domain.entities.models.NumberData

interface DetailsNumberDataUseCase {

    suspend fun allFilterWithCountryCodesByNumber(number: String): List<NumberData>

    suspend fun allFilteredCallsByNumber(number: String): List<NumberData>

    suspend fun getCountryCodeWithCode(code: Int): CountryCode?
}