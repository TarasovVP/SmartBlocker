package com.tarasovvp.smartblocker.presentation.main.number.details.details_filter

import com.tarasovvp.smartblocker.domain.models.database_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.models.database_views.LogCallWithFilter
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.domain.models.NumberData
import com.tarasovvp.smartblocker.domain.repository.*
import com.tarasovvp.smartblocker.domain.usecase.DetailsFilterUseCase
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class DetailsFilterUseCaseImpl @Inject constructor(
    private val contactRepository: ContactRepository,
    private val filterRepository: FilterRepository,
    private val realDataBaseRepository: RealDataBaseRepository,
    private val logCallRepository: LogCallRepository,
    private val filteredCallRepository: FilteredCallRepository
): DetailsFilterUseCase {

    override suspend fun getQueryContactCallList(filter: Filter): ArrayList<NumberData> {
            val calls =  logCallRepository.getLogCallWithFilterByFilter(filter.filter)
            val contacts =  contactRepository.getContactsWithFilterByFilter(filter.filter)
            val numberDataList = ArrayList<NumberData>().apply {
                addAll(calls)
                addAll(contacts)
                sortBy {
                    if (it is ContactWithFilter) it.contact?.number else if (it is LogCallWithFilter) it.call?.number else String.EMPTY
                }
            }
            return numberDataList
        }

    override suspend fun filteredNumberDataList(filter: Filter?, numberDataList: ArrayList<NumberData>, color: Int, ) = contactRepository.filteredNumberDataList(filter, numberDataList, color)

    override suspend fun filteredCallsByFilter(filter: String) = filteredCallRepository.filteredCallsByFilter(filter)

    override suspend fun deleteFilter(filter: Filter, isLoggedInUser: Boolean, result: () -> Unit) {
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

    override suspend fun updateFilter(filter: Filter, isLoggedInUser: Boolean, result: () -> Unit) {
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
}