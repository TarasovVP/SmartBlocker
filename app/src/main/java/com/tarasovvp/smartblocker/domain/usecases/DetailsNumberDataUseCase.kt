package com.tarasovvp.smartblocker.domain.usecases

import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode
import com.tarasovvp.smartblocker.domain.entities.models.NumberData

interface DetailsNumberDataUseCase {

    suspend fun filterListWithNumber(number: String): List<NumberData>

    suspend fun filteredCallsByNumber(number: String): List<NumberData>

    suspend fun getCountryCode(code: Int): CountryCode?
}