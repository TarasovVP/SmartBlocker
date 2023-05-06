package com.tarasovvp.smartblocker.presentation.main.number.create

import com.google.firebase.auth.FirebaseAuth
import com.tarasovvp.smartblocker.domain.entities.db_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.entities.db_views.CallWithFilter
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.PLUS_CHAR
import com.tarasovvp.smartblocker.domain.entities.models.NumberData
import com.tarasovvp.smartblocker.domain.repository.*
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.CreateFilterUseCase
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.utils.extensions.highlightedSpanned
import com.tarasovvp.smartblocker.utils.extensions.isNotNull
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class CreateFilterUseCaseImpl @Inject constructor(
    private val contactRepository: ContactRepository,
    private val countryCodeRepository: CountryCodeRepository,
    private val filterRepository: FilterRepository,
    private val realDataBaseRepository: RealDataBaseRepository,
    private val logCallRepository: LogCallRepository,
    private val firebaseAuth: FirebaseAuth
) : CreateFilterUseCase {

    override suspend fun getCountryCodeWithCode(code: Int) = countryCodeRepository.getCountryCodeWithCode(code)

    override suspend fun getNumberDataList(): ArrayList<NumberData> {
            val contacts =  contactRepository.getContactsWithFilters()
            val calls =  logCallRepository.allDistinctCallWithFilter()
            val numberDataList = ArrayList<NumberData>().apply {
                addAll(contacts)
                addAll(calls.filter { it.call?.number.isNullOrEmpty().not() })
                sortBy {
                    if (it is ContactWithFilter) it.contact?.number?.replace(PLUS_CHAR.toString(), String.EMPTY) else if (it is CallWithFilter) it.call?.number?.replace(PLUS_CHAR.toString(), String.EMPTY) else String.EMPTY
                }
            }
            return numberDataList
    }

    override suspend fun getFilter(filter: String) = filterRepository.getFilter(filter)

    override suspend fun filterNumberDataList(filterWithCountryCode: FilterWithCountryCode?, numberDataUIModelList: ArrayList<NumberData>, color: Int): ArrayList<NumberData> {
        val filteredList = arrayListOf<NumberData>()
        val supposedFilteredList = arrayListOf<NumberData>()
        numberDataUIModelList.forEach { numberData ->
            numberData.highlightedSpanned = numberData.highlightedSpanned(filterWithCountryCode?.filter, color)
            if (numberData is ContactWithFilter && numberData.contact?.number?.startsWith(PLUS_CHAR).isTrue().not()) {
                supposedFilteredList.add(numberData)
            } else {
                filteredList.add(numberData)
            }
        }
        filteredList.addAll(supposedFilteredList)
        return filteredList
    }

    override suspend fun createFilter(filter: Filter,  isNetworkAvailable: Boolean, result: (Result<Unit>) -> Unit) {
        if (firebaseAuth.currentUser.isNotNull()) {
            if (isNetworkAvailable) {
                realDataBaseRepository.insertFilter(filter) {
                    runBlocking {
                        filterRepository.insertFilter(filter)
                        result.invoke(Result.Success())
                    }
                }
            } else {
                result.invoke(Result.Failure())
            }
        } else {
            filterRepository.insertFilter(filter)
            result.invoke(Result.Success())
        }
    }

    override suspend fun updateFilter(filter: Filter,  isNetworkAvailable: Boolean, result: (Result<Unit>) -> Unit) {
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

    override suspend fun deleteFilter(filter: Filter,  isNetworkAvailable: Boolean, result: (Result<Unit>) -> Unit) {
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
}