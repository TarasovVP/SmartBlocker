package com.tarasovvp.smartblocker.domain.usecases

import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.entities.models.NumberData
import com.tarasovvp.smartblocker.domain.sealed_classes.Result

interface CreateFilterUseCase {

    suspend fun getCountryCodeWithCode(code: Int):  CountryCode?

    suspend fun getNumberDataList(): ArrayList<NumberData>

    suspend fun getFilter(filter: String): FilterWithCountryCode?

    suspend fun filterNumberDataList(filterWithCountryCode: FilterWithCountryCode?, numberDataList: ArrayList<NumberData>, color: Int): ArrayList<NumberData>

    suspend fun createFilter(filter: Filter, isNetworkAvailable: Boolean, result: (Result<Unit>) -> Unit)

    suspend fun updateFilter(filter: Filter, isNetworkAvailable: Boolean, result: (Result<Unit>) -> Unit)

    suspend fun deleteFilter(filter: Filter, isNetworkAvailable: Boolean, result: (Result<Unit>) -> Unit)

}