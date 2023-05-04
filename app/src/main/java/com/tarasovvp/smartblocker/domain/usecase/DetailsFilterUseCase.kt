package com.tarasovvp.smartblocker.domain.usecase

import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.presentation.ui_models.NumberDataUIModel
import com.tarasovvp.smartblocker.domain.models.database_views.FilteredCallWithFilter
import com.tarasovvp.smartblocker.domain.sealed_classes.OperationResult

interface DetailsFilterUseCase {

    suspend fun getQueryContactCallList(filter: Filter): ArrayList<NumberDataUIModel>

    suspend fun filteredNumberDataList(filter: Filter?, numberDataUIModelList: ArrayList<NumberDataUIModel>, color: Int) : ArrayList<NumberDataUIModel>

    suspend fun filteredCallsByFilter(filter: String): List<FilteredCallWithFilter>

    suspend fun deleteFilter(filter: Filter,  isLoggedInUser: Boolean, result: (OperationResult<Unit>) -> Unit)

    suspend fun updateFilter(filter: Filter,  isLoggedInUser: Boolean, result: (OperationResult<Unit>) -> Unit)
}