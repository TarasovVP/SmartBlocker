package com.tarasovvp.smartblocker.domain.usecase.number.details.details_filter

import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.domain.models.NumberData
import com.tarasovvp.smartblocker.domain.models.database_views.FilteredCallWithFilter

interface DetailsFilterUseCase {

    suspend fun getQueryContactCallList(filter: Filter): ArrayList<NumberData>

    suspend fun filteredNumberDataList(filter: Filter?, numberDataList: ArrayList<NumberData>, color: Int) : ArrayList<NumberData>

    suspend fun filteredCallsByFilter(filter: String): List<FilteredCallWithFilter>

    suspend fun deleteFilter(filter: Filter, result: () -> Unit)

    suspend fun updateFilter(filter: Filter, result: () -> Unit)
}