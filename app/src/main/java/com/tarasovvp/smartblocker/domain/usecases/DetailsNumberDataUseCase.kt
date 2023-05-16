package com.tarasovvp.smartblocker.domain.usecases

import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode
import com.tarasovvp.smartblocker.domain.entities.db_views.CallWithFilter
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithFilteredNumbers
import kotlinx.coroutines.flow.Flow

interface DetailsNumberDataUseCase {

    suspend fun allFilterWithFilteredNumbersByNumber(number: String): List<FilterWithFilteredNumbers>

    suspend fun allFilteredCallsByNumber(number: String): List<CallWithFilter>

    suspend fun getCountryCodeByCode(code: Int): CountryCode?

    suspend fun getBlockHidden(): Flow<Boolean?>
}