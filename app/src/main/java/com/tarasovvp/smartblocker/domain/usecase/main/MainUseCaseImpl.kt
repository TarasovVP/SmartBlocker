package com.tarasovvp.smartblocker.domain.usecase.main

import android.app.Application
import com.tarasovvp.smartblocker.domain.models.entities.*
import com.tarasovvp.smartblocker.domain.repository.*
import javax.inject.Inject

class MainUseCaseImpl @Inject constructor(
    private val contactRepository: ContactRepository,
    private val countryCodeRepository: CountryCodeRepository,
    private val filterRepository: FilterRepository,
    private val logCallRepository: LogCallRepository,
    private val filteredCallRepository: FilteredCallRepository,
    private val realDataBaseRepository: RealDataBaseRepository
): MainUseCase {

    override fun getCurrentUser(result: (CurrentUser?) -> Unit) = realDataBaseRepository.getCurrentUser {
        result.invoke(it)
    }

    override suspend fun getSystemCountryCodeList(result: (Int, Int) -> Unit) = countryCodeRepository.getSystemCountryCodeList { size, position ->
        result.invoke(size, position)
    }

    override suspend fun insertAllCountryCodes(countryCodeList: List<CountryCode>) {
        countryCodeRepository.insertAllCountryCodes(countryCodeList)
    }

    override suspend fun getAllFilters(): List<Filter> {
        return filterRepository.allFilters()
    }

    override suspend fun getSystemContactList(application: Application, result: (Int, Int) -> Unit) = contactRepository.getSystemContactList(application) { size, position ->
        result.invoke(size, position)
    }

    override suspend fun setFilterToContact(
        filterList: List<Filter>,
        contactList: List<Contact>,
        result: (Int, Int) -> Unit,
    ) = contactRepository.setFilterToContact(filterList, contactList) { size, position ->
        result.invoke(size, position)
    }

    override suspend fun insertContacts(contactList: List<Contact>) {
        contactRepository.insertContacts(contactList)
    }

    override suspend fun getSystemLogCallList(application: Application, result: (Int, Int) -> Unit) = logCallRepository.getSystemLogCallList(application) { size, position ->
        result.invoke(size, position)
    }

    override suspend fun setFilterToLogCall(
        filterList: List<Filter>,
        logCallList: List<LogCall>,
        result: (Int, Int) -> Unit,
    ) = logCallRepository.setFilterToLogCall(filterList, logCallList) { size, position ->
        result.invoke(size, position)
    }

    override suspend fun insertAllLogCalls(logCallList: List<LogCall>) {
        logCallRepository.insertAllLogCalls(logCallList)
    }

    override suspend fun getAllFilteredCalls(): List<FilteredCall> {
        return filteredCallRepository.allFilteredCalls()
    }

    override suspend fun setFilterToFilteredCall(
        filterList: List<Filter>,
        filteredCallList: List<FilteredCall>,
        result: (Int, Int) -> Unit,
    ) = filteredCallRepository.setFilterToFilteredCall(filterList, filteredCallList) { size, position ->
        result.invoke(size, position)
    }

    override suspend fun insertAllFilteredCalls(filteredCallList: List<FilteredCall>) {
        filteredCallRepository.insertAllFilteredCalls(filteredCallList)
    }

    override suspend fun insertAllFilters(filterList: List<Filter>) {
        filterRepository.insertAllFilters(filterList)
    }
}