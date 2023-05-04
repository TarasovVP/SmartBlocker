package com.tarasovvp.smartblocker.presentation.main.number.details.details_filter

import com.tarasovvp.smartblocker.domain.models.database_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.models.database_views.LogCallWithFilter
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.presentation.ui_models.NumberDataUIModel
import com.tarasovvp.smartblocker.domain.repository.*
import com.tarasovvp.smartblocker.domain.sealed_classes.OperationResult
import com.tarasovvp.smartblocker.domain.usecase.DetailsFilterUseCase
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.utils.extensions.highlightedSpanned
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class DetailsFilterUseCaseImpl @Inject constructor(
    private val contactRepository: ContactRepository,
    private val filterRepository: FilterRepository,
    private val realDataBaseRepository: RealDataBaseRepository,
    private val logCallRepository: LogCallRepository,
    private val filteredCallRepository: FilteredCallRepository
): DetailsFilterUseCase {

    override suspend fun getQueryContactCallList(filter: Filter): ArrayList<NumberDataUIModel> {
            val calls =  logCallRepository.getLogCallWithFilterByFilter(filter.filter)
            val contacts =  contactRepository.getContactsWithFilterByFilter(filter.filter)
            val numberDataUIModelList = ArrayList<NumberDataUIModel>().apply {
                addAll(calls)
                addAll(contacts)
                sortBy {
                    if (it is ContactWithFilter) it.contact?.number else if (it is LogCallWithFilter) it.callUIModel?.number else String.EMPTY
                }
            }
            return numberDataUIModelList
        }

    override suspend fun filteredNumberDataList(filter: Filter?, numberDataUIModelList: ArrayList<NumberDataUIModel>, color: Int, ): ArrayList<NumberDataUIModel> {
        val filteredList = arrayListOf<NumberDataUIModel>()
        val supposedFilteredList = arrayListOf<NumberDataUIModel>()
        numberDataUIModelList.forEach { numberData ->
            numberData.highlightedSpanned = numberData.highlightedSpanned(filter, color)
            if (numberData is ContactWithFilter && numberData.contact?.number?.startsWith(Constants.PLUS_CHAR).isTrue().not()) {
                supposedFilteredList.add(numberData)
            } else {
                filteredList.add(numberData)
            }
        }
        filteredList.addAll(supposedFilteredList)
        return filteredList
    }

    override suspend fun filteredCallsByFilter(filter: String) = filteredCallRepository.filteredCallsByFilter(filter)

    override suspend fun deleteFilter(filter: Filter, isLoggedInUser: Boolean, result: (OperationResult<Unit>) -> Unit) {
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

    override suspend fun updateFilter(filter: Filter, isLoggedInUser: Boolean, result: (OperationResult<Unit>) -> Unit) {
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