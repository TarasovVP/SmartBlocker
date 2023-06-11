package com.tarasovvp.smartblocker.domain.usecases

import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.entities.db_views.CallWithFilter
import com.tarasovvp.smartblocker.domain.entities.db_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.sealed_classes.Result

interface DetailsFilterUseCase {

    suspend fun allContactsWithFiltersByFilter(filter: String): List<ContactWithFilter>

    suspend fun allFilteredCallsByFilter(filter: String): List<CallWithFilter>

    suspend fun deleteFilter(filter: Filter, isNetworkAvailable: Boolean, result: (Result<Unit>) -> Unit)

    suspend fun updateFilter(filter: Filter, isNetworkAvailable: Boolean, result: (Result<Unit>) -> Unit)
}