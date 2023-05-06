package com.tarasovvp.smartblocker.domain.usecases

import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.entities.db_views.CallWithFilter
import com.tarasovvp.smartblocker.domain.entities.models.NumberData
import com.tarasovvp.smartblocker.domain.sealed_classes.Result

interface DetailsFilterUseCase {

    suspend fun getQueryContactCallList(filter: Filter): ArrayList<NumberData>

    suspend fun filteredNumberDataList(filter: Filter?, numberDataList: ArrayList<NumberData>, color: Int) : ArrayList<NumberData>

    suspend fun filteredCallsByFilter(filter: String): List<CallWithFilter>

    suspend fun deleteFilter(filter: Filter, isNetworkAvailable: Boolean, result: (Result<Unit>) -> Unit)

    suspend fun updateFilter(filter: Filter, isNetworkAvailable: Boolean, result: (Result<Unit>) -> Unit)
}