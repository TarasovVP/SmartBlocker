package com.tarasovvp.smartblocker.domain.usecases

import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.entities.db_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithFilteredNumber
import com.tarasovvp.smartblocker.domain.sealed_classes.Result

interface CreateFilterUseCase {

    suspend fun allContactsWithFiltersByCreateFilter(filter: String, country: String, countryCode: String, isContain: Boolean): List<ContactWithFilter>

    suspend fun getFilter(filter: String): FilterWithFilteredNumber?

    suspend fun createFilter(filter: Filter, isNetworkAvailable: Boolean, result: (Result<Unit>) -> Unit)

    suspend fun updateFilter(filter: Filter, isNetworkAvailable: Boolean, result: (Result<Unit>) -> Unit)

    suspend fun deleteFilter(filter: Filter, isNetworkAvailable: Boolean, result: (Result<Unit>) -> Unit)

}