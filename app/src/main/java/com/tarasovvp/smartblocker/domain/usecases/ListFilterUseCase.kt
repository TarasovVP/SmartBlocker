package com.tarasovvp.smartblocker.domain.usecases

import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.sealed_classes.Result

interface ListFilterUseCase {

    suspend fun allFilterWithCountryCodesByType(isBlockerList: Boolean): List<FilterWithCountryCode>?

    suspend fun getFilteredFilterList(filterList: List<FilterWithCountryCode>, searchQuery: String, filterIndexes: ArrayList<Int>): List<FilterWithCountryCode>

    suspend fun getHashMapFromFilterList(filterList: List<FilterWithCountryCode>): Map<String, List<FilterWithCountryCode>>?

    suspend fun deleteFilterList(filterList: List<Filter>, isNetworkAvailable: Boolean, result: (Result<Unit>) -> Unit)
}