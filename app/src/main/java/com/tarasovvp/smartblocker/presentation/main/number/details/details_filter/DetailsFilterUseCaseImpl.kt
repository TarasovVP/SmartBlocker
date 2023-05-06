package com.tarasovvp.smartblocker.presentation.main.number.details.details_filter

import com.google.firebase.auth.FirebaseAuth
import com.tarasovvp.smartblocker.domain.entities.db_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.entities.db_views.LogCallWithFilter
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.domain.entities.models.NumberData
import com.tarasovvp.smartblocker.domain.repository.*
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.DetailsFilterUseCase
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.PLUS_CHAR
import com.tarasovvp.smartblocker.utils.extensions.highlightedSpanned
import com.tarasovvp.smartblocker.utils.extensions.isNotNull
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class DetailsFilterUseCaseImpl @Inject constructor(
    private val contactRepository: ContactRepository,
    private val filterRepository: FilterRepository,
    private val realDataBaseRepository: RealDataBaseRepository,
    private val logCallRepository: LogCallRepository,
    private val filteredCallRepository: FilteredCallRepository,
    private val firebaseAuth: FirebaseAuth
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

    override suspend fun filteredNumberDataList(filter: Filter?, numberDataList: ArrayList<NumberData>, color: Int, ): ArrayList<NumberData> {
        val filteredList = arrayListOf<NumberData>()
        val supposedFilteredList = arrayListOf<NumberData>()
        numberDataList.forEach { numberData ->
            numberData.highlightedSpanned = numberData.highlightedSpanned(filter, color)
            if (numberData is ContactWithFilter && numberData.contact?.number?.startsWith(PLUS_CHAR).isTrue().not()) {
                supposedFilteredList.add(numberData)
            } else {
                filteredList.add(numberData)
            }
        }
        filteredList.addAll(supposedFilteredList)
        return filteredList
    }

    override suspend fun filteredCallsByFilter(filter: String) = filteredCallRepository.filteredCallsByFilter(filter)

    override suspend fun deleteFilter(filter: Filter, isNetworkAvailable: Boolean, result: (Result<Unit>) -> Unit) {
        if (firebaseAuth.currentUser.isNotNull()) {
            if (isNetworkAvailable) {
                realDataBaseRepository.deleteFilterList(listOf(filter)) {
                    runBlocking {
                        filterRepository.deleteFilterList(listOf(filter))
                        result.invoke(Result.Success())
                    }
                }
            } else {
                result.invoke(Result.Failure())
            }
        } else {
            filterRepository.deleteFilterList(listOf(filter))
            result.invoke(Result.Success())
        }
    }

    override suspend fun updateFilter(filter: Filter, isNetworkAvailable: Boolean, result: (Result<Unit>) -> Unit) {
        if (firebaseAuth.currentUser.isNotNull()) {
            if (isNetworkAvailable) {
                realDataBaseRepository.insertFilter(filter) {
                    runBlocking {
                        filterRepository.updateFilter(filter)
                        result.invoke(Result.Success())
                    }
                }
            } else {
                result.invoke(Result.Failure())
            }
        } else {
            filterRepository.updateFilter(filter)
            result.invoke(Result.Success())
        }
    }
}