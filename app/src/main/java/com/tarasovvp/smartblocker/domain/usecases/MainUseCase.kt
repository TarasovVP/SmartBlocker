package com.tarasovvp.smartblocker.domain.usecases

import android.app.Application
import com.tarasovvp.smartblocker.domain.entities.models.CurrentUser
import com.tarasovvp.smartblocker.domain.entities.db_entities.*
import com.tarasovvp.smartblocker.domain.sealed_classes.Result

interface MainUseCase {

    fun getCurrentUser(result: (Result<CurrentUser>) -> Unit)

    suspend fun getSystemCountryCodeList(result: (Int, Int) -> Unit): List<CountryCode>

    suspend fun insertAllCountryCodes(countryCodeList: List<CountryCode>)

    suspend fun getAllFilters(): List<Filter>

    suspend fun getSystemContactList(application: Application, result: (Int, Int) -> Unit): List<Contact>

    suspend fun setFilterToContact(filterList: List<Filter>, contactList: List<Contact>, result: (Int, Int) -> Unit): List<Contact>

    suspend fun insertContacts(contactList: List<Contact>)

    suspend fun getSystemLogCallList(application: Application, result: (Int, Int) -> Unit): List<LogCall>

    suspend fun setFilterToLogCall(filterList: List<Filter>, logCallList: List<LogCall>, result: (Int, Int) -> Unit): List<LogCall>

    suspend fun insertAllLogCalls(logCallList: List<LogCall>)

    suspend fun getAllFilteredCalls(): List<FilteredCall>

    suspend fun setFilterToFilteredCall(filterList: List<Filter>, filteredCallList: List<FilteredCall>, result: (Int, Int) -> Unit): List<FilteredCall>

    suspend fun insertAllFilteredCalls(filteredCallList: List<FilteredCall>)

    suspend fun insertAllFilters(filterList: List<Filter>)
}