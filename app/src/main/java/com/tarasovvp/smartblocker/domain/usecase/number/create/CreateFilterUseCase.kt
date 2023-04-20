package com.tarasovvp.smartblocker.domain.usecase.number.create

import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.models.entities.CountryCode
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.domain.models.NumberData

interface CreateFilterUseCase {

    suspend fun getCountryCodeWithCode(code: Int):  CountryCode?

    suspend fun getNumberDataList(): ArrayList<NumberData>

    suspend fun checkFilterExist(filterWithCountryCode: FilterWithCountryCode): FilterWithCountryCode?

    suspend fun filterNumberDataList(filterWithCountryCode: FilterWithCountryCode?, numberDataList: ArrayList<NumberData>, color: Int): ArrayList<NumberData>

    suspend fun createFilter(filter: Filter,  result: () -> Unit)

    suspend fun updateFilter(filter: Filter,  result: () -> Unit)

    suspend fun deleteFilter(filter: Filter,  result: () -> Unit)

}