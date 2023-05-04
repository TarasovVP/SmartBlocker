package com.tarasovvp.smartblocker.presentation.main.number.create

import com.tarasovvp.smartblocker.domain.models.database_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.models.database_views.LogCallWithFilter
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.PLUS_CHAR
import com.tarasovvp.smartblocker.domain.models.NumberData
import com.tarasovvp.smartblocker.domain.repository.*
import com.tarasovvp.smartblocker.domain.usecase.CreateFilterUseCase
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class CreateFilterUseCaseImpl @Inject constructor(
    private val contactRepository: ContactRepository,
    private val countryCodeRepository: CountryCodeRepository,
    private val filterRepository: FilterRepository,
    private val realDataBaseRepository: RealDataBaseRepository,
    private val logCallRepository: LogCallRepository
) : CreateFilterUseCase {

    override suspend fun getCountryCodeWithCode(code: Int) = countryCodeRepository.getCountryCodeWithCode(code)

    override suspend fun getNumberDataList(): ArrayList<NumberData> {
            val contacts =  contactRepository.getContactsWithFilters()
            val calls =  logCallRepository.allCallNumberWithFilter()
            val numberDataList = ArrayList<NumberData>().apply {
                addAll(contacts)
                addAll(calls.filter { it.call?.number.isNullOrEmpty().not() })
                sortBy {
                    if (it is ContactWithFilter) it.contact?.number?.replace(PLUS_CHAR.toString(), String.EMPTY) else if (it is LogCallWithFilter) it.call?.number?.replace(PLUS_CHAR.toString(), String.EMPTY) else String.EMPTY
                }
            }
            return numberDataList
    }

    override suspend fun checkFilterExist(filterWithCountryCode: FilterWithCountryCode) = filterRepository.getFilter(filterWithCountryCode)

    override suspend fun filterNumberDataList(filterWithCountryCode: FilterWithCountryCode?, numberDataList: ArrayList<NumberData>, color: Int) = contactRepository.filteredNumberDataList(filterWithCountryCode?.filter, numberDataList, color)

    override suspend fun createFilter(filter: Filter,  isLoggedInUser: Boolean, result: () -> Unit) {
        if (isLoggedInUser) {
            realDataBaseRepository.insertFilter(filter) {
                runBlocking {
                    filterRepository.insertFilter(filter)
                    result.invoke()
                }
            }
        } else {
            filterRepository.insertFilter(filter)
            result.invoke()
        }
    }

    override suspend fun updateFilter(filter: Filter,  isLoggedInUser: Boolean, result: () -> Unit) {
        if (isLoggedInUser) {
            realDataBaseRepository.insertFilter(filter) {
                runBlocking {
                    filterRepository.updateFilter(filter)
                    result.invoke()
                }
            }
        } else {
            filterRepository.updateFilter(filter)
            result.invoke()
        }
    }

    override suspend fun deleteFilter(filter: Filter,  isLoggedInUser: Boolean, result: () -> Unit) {
        if (isLoggedInUser) {
            realDataBaseRepository.deleteFilterList(listOf(filter)) {
                runBlocking {
                    filterRepository.deleteFilterList(listOf(filter))
                    result.invoke()
                }
            }
        } else {
            filterRepository.deleteFilterList(listOf(filter))
            result.invoke()
        }
    }
}