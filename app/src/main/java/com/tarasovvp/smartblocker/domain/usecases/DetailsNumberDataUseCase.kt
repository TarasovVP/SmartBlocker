package com.tarasovvp.smartblocker.domain.usecases

import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode
import com.tarasovvp.smartblocker.domain.entities.db_views.CallWithFilter
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithCountryCode
import kotlinx.coroutines.flow.Flow

interface DetailsNumberDataUseCase {

    suspend fun allFilterWithCountryCodesByNumber(number: String): List<FilterWithCountryCode>

    suspend fun allFilteredCallsByNumber(number: String): List<CallWithFilter>

    suspend fun getCountryCodeWithCode(code: Int): CountryCode?

    suspend fun getBlockHidden(): Flow<Boolean?>
}