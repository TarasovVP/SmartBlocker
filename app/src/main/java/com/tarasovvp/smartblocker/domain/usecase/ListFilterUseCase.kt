package com.tarasovvp.smartblocker.domain.usecase

import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.models.entities.Filter

interface ListFilterUseCase {

    suspend fun getFilterList(isBlackList: Boolean): List<FilterWithCountryCode>?

    suspend fun getFilteredFilterList(filterList: List<FilterWithCountryCode>, searchQuery: String, filterIndexes: ArrayList<Int>): List<FilterWithCountryCode>

    suspend fun getHashMapFromFilterList(filterList: List<FilterWithCountryCode>): Map<String, List<FilterWithCountryCode>>?

    suspend fun deleteFilterList(filterList: List<Filter?>, isLoggedInUser: Boolean, result: () -> Unit)
}