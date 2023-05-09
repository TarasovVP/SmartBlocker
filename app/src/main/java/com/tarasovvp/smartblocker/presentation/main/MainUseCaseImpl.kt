package com.tarasovvp.smartblocker.presentation.main

import android.app.Application
import com.tarasovvp.smartblocker.domain.entities.models.CurrentUser
import com.tarasovvp.smartblocker.domain.entities.db_entities.*
import com.tarasovvp.smartblocker.domain.repository.*
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.MainUseCase
import javax.inject.Inject

class MainUseCaseImpl @Inject constructor(
    private val contactRepository: ContactRepository,
    private val countryCodeRepository: CountryCodeRepository,
    private val filterRepository: FilterRepository,
    private val logCallRepository: LogCallRepository,
    private val filteredCallRepository: FilteredCallRepository,
    private val realDataBaseRepository: RealDataBaseRepository
): MainUseCase {

    override fun getCurrentUser(result: (Result<CurrentUser>) -> Unit) = realDataBaseRepository.getCurrentUser { operationResult ->
        result.invoke(operationResult)
    }

    override suspend fun getSystemCountryCodes(result: (Int, Int) -> Unit) = countryCodeRepository.getSystemCountryCodeList { size, position ->
        result.invoke(size, position)
    }

    override suspend fun insertAllCountryCodes(countryCodeList: List<CountryCode>) {
        countryCodeRepository.insertAllCountryCodes(countryCodeList)
    }

    override suspend fun getAllFilters(): List<Filter> {
        return filterRepository.allFilters()
    }

    override suspend fun getSystemContacts(application: Application, result: (Int, Int) -> Unit) = contactRepository.getSystemContactList(application) { size, position ->
        result.invoke(size, position)
    }

    override suspend fun insertContacts(contactList: List<Contact>) {
        contactRepository.insertAllContacts(contactList)
    }

    override suspend fun getSystemLogCalls(application: Application, result: (Int, Int) -> Unit) = logCallRepository.getSystemLogCallList(application) { size, position ->
        result.invoke(size, position)
    }

    override suspend fun insertAllLogCalls(logCallList: List<LogCall>) {
        logCallRepository.insertAllLogCalls(logCallList)
    }

    override suspend fun getAllFilteredCalls(): List<FilteredCall> {
        return filteredCallRepository.allFilteredCalls()
    }

    override suspend fun insertAllFilteredCalls(filteredCallList: List<FilteredCall>) {
        filteredCallRepository.insertAllFilteredCalls(filteredCallList)
    }

    override suspend fun insertAllFilters(filterList: List<Filter>) {
        filterRepository.insertAllFilters(filterList)
    }
}