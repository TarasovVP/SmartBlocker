package com.tarasovvp.smartblocker.domain.usecase

import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.models.entities.CountryCode
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.domain.sealed_classes.OperationResult
import com.tarasovvp.smartblocker.presentation.ui_models.NumberDataUIModel

interface CreateFilterUseCase {

    suspend fun getCountryCodeWithCode(code: Int):  CountryCode?

    suspend fun getNumberDataList(): ArrayList<NumberDataUIModel>

    suspend fun getFilter(filter: String): FilterWithCountryCode?

    suspend fun filterNumberDataList(filterWithCountryCode: FilterWithCountryCode?, numberDataUIModelList: ArrayList<NumberDataUIModel>, color: Int): ArrayList<NumberDataUIModel>

    suspend fun createFilter(filter: Filter,  isLoggedInUser: Boolean, result: (OperationResult<Unit>) -> Unit)

    suspend fun updateFilter(filter: Filter,  isLoggedInUser: Boolean, result: (OperationResult<Unit>) -> Unit)

    suspend fun deleteFilter(filter: Filter,  isLoggedInUser: Boolean, result: (OperationResult<Unit>) -> Unit)

}