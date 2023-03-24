package com.tarasovvp.smartblocker.domain.usecase.number.list.list_filter

import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.models.entities.Filter

interface ListFilterUseCase {

    suspend fun getFilterList(isBlackList: Boolean): List<FilterWithCountryCode>?

    suspend fun getHashMapFromFilterList(filterList: List<FilterWithCountryCode>): Map<String, List<FilterWithCountryCode>>?

    suspend fun deleteFilterList(filterList: List<Filter?>, result: () -> Unit)
}