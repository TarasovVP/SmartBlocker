package com.tarasovvp.smartblocker.domain.usecases

import com.tarasovvp.smartblocker.domain.entities.dbentities.Filter
import com.tarasovvp.smartblocker.domain.entities.dbviews.CallWithFilter
import com.tarasovvp.smartblocker.domain.entities.dbviews.ContactWithFilter
import com.tarasovvp.smartblocker.domain.sealedclasses.Result

interface DetailsFilterUseCase {
    suspend fun allContactsWithFiltersByFilter(filter: String): List<ContactWithFilter>

    suspend fun allFilteredCallsByFilter(filter: String): List<CallWithFilter>

    suspend fun deleteFilter(
        filter: Filter,
        isNetworkAvailable: Boolean,
        result: (Result<Unit>) -> Unit,
    )

    suspend fun updateFilter(
        filter: Filter,
        isNetworkAvailable: Boolean,
        result: (Result<Unit>) -> Unit,
    )
}
