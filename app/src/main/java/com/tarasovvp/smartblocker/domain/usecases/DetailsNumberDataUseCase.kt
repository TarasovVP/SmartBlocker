package com.tarasovvp.smartblocker.domain.usecases

import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode
import com.tarasovvp.smartblocker.domain.entities.db_views.CallWithFilter
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithFilteredNumber
import kotlinx.coroutines.flow.Flow

interface DetailsNumberDataUseCase {

    suspend fun allFilterWithFilteredNumbersByNumber(number: String): List<FilterWithFilteredNumber>

    suspend fun allFilteredCallsByNumber(number: String, name: String): List<CallWithFilter>

    suspend fun getCurrentCountryCode(): Flow<CountryCode?>

    suspend fun getCountryCodeByCode(code: Int): CountryCode?

    suspend fun getBlockHidden(): Flow<Boolean?>
}