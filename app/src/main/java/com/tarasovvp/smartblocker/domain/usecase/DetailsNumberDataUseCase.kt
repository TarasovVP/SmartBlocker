package com.tarasovvp.smartblocker.domain.usecase

import com.tarasovvp.smartblocker.domain.models.entities.CountryCode
import com.tarasovvp.smartblocker.presentation.ui_models.NumberDataUIModel

interface DetailsNumberDataUseCase {

    suspend fun filterListWithNumber(number: String): List<NumberDataUIModel>

    suspend fun filteredCallsByNumber(number: String): List<NumberDataUIModel>

    suspend fun getCountryCode(code: Int): CountryCode?
}