package com.tarasovvp.smartblocker.domain.usecase.number.details.details_filter

import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.domain.models.database_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.models.database_views.LogCallWithFilter
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.domain.models.NumberData
import com.tarasovvp.smartblocker.domain.repository.LogCallRepository
import com.tarasovvp.smartblocker.domain.repository.ContactRepository
import com.tarasovvp.smartblocker.domain.repository.FilterRepository
import com.tarasovvp.smartblocker.domain.repository.FilteredCallRepository
import javax.inject.Inject

class DetailsFilterUseCaseImpl @Inject constructor(
    private val contactRepository: ContactRepository,
    private val filterRepository: FilterRepository,
    private val logCallRepository: LogCallRepository,
    private val filteredCallRepository: FilteredCallRepository
): DetailsFilterUseCase  {

    val numberDataListLiveData = MutableLiveData<ArrayList<NumberData>>()
    val filteredCallListLiveData = MutableLiveData<ArrayList<NumberData>>()

    val filterActionLiveData = MutableLiveData<Filter>()

    override suspend fun getQueryContactCallList(filter: Filter): ArrayList<NumberData> {
            val calls =  logCallRepository.getLogCallWithFilterByFilter(filter.filter)
            val contacts =  contactRepository.getContactsWithFilterByFilter(filter.filter)
            val callList = calls
            val contactList = contacts
            val numberDataList = ArrayList<NumberData>().apply {
                addAll(callList)
                addAll(contactList)
                sortBy {
                    if (it is ContactWithFilter) it.contact?.number else if (it is LogCallWithFilter) it.call?.number else String.EMPTY
                }
            }
            return numberDataList
        }

    override suspend fun filteredNumberDataList(filter: Filter?, numberDataList: ArrayList<NumberData>, color: Int, ) = contactRepository.filteredNumberDataList(filter, numberDataList, color)

    override suspend fun filteredCallsByFilter(filter: String) = filteredCallRepository.filteredCallsByFilter(filter)

    override suspend fun deleteFilter(filter: Filter, result: () -> Unit) = filterRepository.deleteFilterList(listOf(filter)) {
        result.invoke()
    }

    override suspend fun updateFilter(filter: Filter, result: () -> Unit) = filterRepository.updateFilter(filter) {
        result.invoke()
    }
}