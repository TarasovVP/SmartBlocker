package com.tarasovvp.smartblocker.domain.usecases

import com.tarasovvp.smartblocker.domain.entities.dbentities.CountryCode
import com.tarasovvp.smartblocker.domain.entities.dbentities.Filter
import com.tarasovvp.smartblocker.domain.entities.dbviews.FilterWithFilteredNumber
import com.tarasovvp.smartblocker.domain.sealedclasses.Result
import kotlinx.coroutines.flow.Flow

interface ListFilterUseCase {
    suspend fun allFilterWithFilteredNumbersByType(isBlockerList: Boolean): List<FilterWithFilteredNumber>?

    suspend fun deleteFilterList(
        filterList: List<Filter>,
        isNetworkAvailable: Boolean,
        result: (Result<Unit>) -> Unit,
    )

    suspend fun getCurrentCountryCode(): Flow<CountryCode?>
}
