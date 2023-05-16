package com.tarasovvp.smartblocker.domain.usecases

import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithFilteredNumbers
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.sealed_classes.Result

interface ListFilterUseCase {

    suspend fun allFilterWithFilteredNumbersByType(isBlockerList: Boolean): List<FilterWithFilteredNumbers>?

    suspend fun getFilteredFilterList(filterList: List<FilterWithFilteredNumbers>, searchQuery: String, filterIndexes: ArrayList<Int>): List<FilterWithFilteredNumbers>

    suspend fun deleteFilterList(filterList: List<Filter>, isNetworkAvailable: Boolean, result: (Result<Unit>) -> Unit)
}