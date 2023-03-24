package com.tarasovvp.smartblocker.domain.usecase.number.details.details_number_data

import com.tarasovvp.smartblocker.domain.models.entities.CountryCode
import com.tarasovvp.smartblocker.domain.models.NumberData

interface DetailsNumberDataUseCase {

    suspend fun filterListWithNumber(number: String): List<NumberData>

    suspend fun filteredCallsByNumber(number: String): List<NumberData>

    suspend fun getCountryCode(code: Int): CountryCode?
}