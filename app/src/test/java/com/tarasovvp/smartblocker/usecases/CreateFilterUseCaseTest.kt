package com.tarasovvp.smartblocker.usecases

import com.tarasovvp.smartblocker.domain.models.database_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.models.database_views.LogCallWithFilter
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.PLUS_CHAR
import com.tarasovvp.smartblocker.domain.models.NumberData
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.domain.repository.LogCallRepository
import com.tarasovvp.smartblocker.domain.repository.ContactRepository
import com.tarasovvp.smartblocker.domain.repository.CountryCodeRepository
import com.tarasovvp.smartblocker.domain.repository.FilterRepository
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import javax.inject.Inject

@RunWith(MockitoJUnitRunner::class)
class CreateFilterUseCaseTest @Inject constructor(
    private val contactRepository: ContactRepository,
    private val countryCodeRepository: CountryCodeRepository,
    private val filterRepository: FilterRepository,
    private val logCallRepository: LogCallRepository
) {

    suspend fun getCountryCodeWithCountry(country: String) = countryCodeRepository.getCountryCodeWithCountry(country)

    suspend fun getCountryCodeWithCode(code: Int) = countryCodeRepository.getCountryCodeWithCode(code)

    suspend fun getNumberDataList(): ArrayList<NumberData> {
            val contacts =  contactRepository.getContactsWithFilters()
            val calls =  logCallRepository.allCallNumberWithFilter()
            val contactList = contacts
            val callList = calls
            val numberDataList = ArrayList<NumberData>().apply {
                addAll(contactList)
                addAll(callList)
                sortBy {
                    if (it is ContactWithFilter) it.contact?.number?.replace(PLUS_CHAR.toString(), String.EMPTY) else if (it is LogCallWithFilter) it.call?.number?.replace(PLUS_CHAR.toString(), String.EMPTY) else String.EMPTY
                }
            }
            return numberDataList
    }

    suspend fun checkFilterExist(filterWithCountryCode: FilterWithCountryCode) = filterRepository.getFilter(filterWithCountryCode)

    suspend fun filterNumberDataList(filterWithCountryCode: FilterWithCountryCode?, numberDataList: ArrayList<NumberData>, color: Int) = contactRepository.filteredNumberDataList(filterWithCountryCode?.filter, numberDataList, color)

    suspend fun createFilter(filter: Filter,  result: () -> Unit) = filterRepository.insertFilter(filter) {
        result.invoke()
    }

    suspend fun updateFilter(filter: Filter,  result: () -> Unit) = filterRepository.updateFilter(filter) {
        result.invoke()
    }

    suspend fun deleteFilter(filter: Filter,  result: () -> Unit) = filterRepository.deleteFilterList(listOf(filter)) {
        result.invoke()
    }

}