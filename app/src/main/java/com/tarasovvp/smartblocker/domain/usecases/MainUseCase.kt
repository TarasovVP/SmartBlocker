package com.tarasovvp.smartblocker.domain.usecases

import android.app.Application
import com.tarasovvp.smartblocker.domain.entities.models.CurrentUser
import com.tarasovvp.smartblocker.domain.entities.db_entities.*
import com.tarasovvp.smartblocker.domain.sealed_classes.Result

interface MainUseCase {

    fun getCurrentUser(result: (Result<CurrentUser>) -> Unit)

    suspend fun getSystemCountryCodes(result: (Int, Int) -> Unit): List<CountryCode>

    suspend fun insertAllCountryCodes(countryCodeList: List<CountryCode>)

    suspend fun getAllFilters(): List<Filter>

    suspend fun getSystemContacts(application: Application, result: (Int, Int) -> Unit): List<Contact>

    suspend fun insertContacts(contactList: List<Contact>)

    suspend fun getSystemLogCalls(application: Application, result: (Int, Int) -> Unit): List<LogCall>

    suspend fun insertAllLogCalls(logCallList: List<LogCall>)

    suspend fun insertAllFilteredCalls(filteredCallList: List<FilteredCall>)

    suspend fun insertAllFilters(filterList: List<Filter>)
}