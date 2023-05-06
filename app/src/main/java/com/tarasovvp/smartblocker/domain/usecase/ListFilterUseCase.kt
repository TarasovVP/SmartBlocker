package com.tarasovvp.smartblocker.domain.usecase

import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.domain.sealed_classes.Result

interface ListFilterUseCase {

    suspend fun getFilterList(isBlackList: Boolean): List<FilterWithCountryCode>?

    suspend fun getFilteredFilterList(filterList: List<FilterWithCountryCode>, searchQuery: String, filterIndexes: ArrayList<Int>): List<FilterWithCountryCode>

    suspend fun getHashMapFromFilterList(filterList: List<FilterWithCountryCode>): Map<String, List<FilterWithCountryCode>>?

    suspend fun deleteFilterList(filterList: List<Filter?>, isNetworkAvailable: Boolean, result: (Result<Unit>) -> Unit)
}