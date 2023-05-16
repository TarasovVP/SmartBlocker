package com.tarasovvp.smartblocker.domain.usecases

import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithFilteredNumbers
import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.entities.db_views.CallWithFilter
import com.tarasovvp.smartblocker.domain.entities.db_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.sealed_classes.Result

interface CreateFilterUseCase {

    suspend fun getCountryCodeWithCode(code: Int):  CountryCode?

    suspend fun allCallsByFilter(filter: String): List<CallWithFilter>

    suspend fun allContactsByFilter(filter: String): List<ContactWithFilter>

    suspend fun getFilter(filter: String): FilterWithFilteredNumbers?

    suspend fun createFilter(filter: Filter, isNetworkAvailable: Boolean, result: (Result<Unit>) -> Unit)

    suspend fun updateFilter(filter: Filter, isNetworkAvailable: Boolean, result: (Result<Unit>) -> Unit)

    suspend fun deleteFilter(filter: Filter, isNetworkAvailable: Boolean, result: (Result<Unit>) -> Unit)

}