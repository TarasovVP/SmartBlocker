package com.tarasovvp.smartblocker.domain.usecases

import com.tarasovvp.smartblocker.domain.entities.dbentities.CountryCode
import com.tarasovvp.smartblocker.domain.entities.dbviews.CallWithFilter
import com.tarasovvp.smartblocker.domain.entities.dbviews.FilterWithFilteredNumber
import kotlinx.coroutines.flow.Flow

interface DetailsNumberDataUseCase {
    suspend fun allFilterWithFilteredNumbersByNumber(number: String): List<FilterWithFilteredNumber>

    suspend fun allFilteredCallsByNumber(
        number: String,
        name: String,
    ): List<CallWithFilter>

    suspend fun getCurrentCountryCode(): Flow<CountryCode?>

    suspend fun getCountryCodeByCode(code: Int): CountryCode?

    suspend fun getBlockHidden(): Flow<Boolean?>
}
