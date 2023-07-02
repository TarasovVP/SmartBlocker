package com.tarasovvp.smartblocker.domain.usecases

import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithFilteredNumber
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import kotlinx.coroutines.flow.Flow

interface ListFilterUseCase {

    suspend fun allFilterWithFilteredNumbersByType(isBlockerList: Boolean): List<FilterWithFilteredNumber>?

    suspend fun deleteFilterList(filterList: List<Filter>, isNetworkAvailable: Boolean, result: (Result<Unit>) -> Unit)

    suspend fun getCurrentCountryCode(): Flow<CountryCode?>
}