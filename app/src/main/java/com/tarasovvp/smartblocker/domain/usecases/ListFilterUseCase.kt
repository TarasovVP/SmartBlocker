package com.tarasovvp.smartblocker.domain.usecases

import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithFilteredNumber
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.sealed_classes.Result

interface ListFilterUseCase {

    suspend fun allFilterWithFilteredNumbersByType(isBlockerList: Boolean): List<FilterWithFilteredNumber>?

    suspend fun getFilteredFilterList(filterList: List<FilterWithFilteredNumber>, searchQuery: String, filterIndexes: ArrayList<Int>): List<FilterWithFilteredNumber>

    suspend fun deleteFilterList(filterList: List<Filter>, isNetworkAvailable: Boolean, result: (Result<Unit>) -> Unit)
}