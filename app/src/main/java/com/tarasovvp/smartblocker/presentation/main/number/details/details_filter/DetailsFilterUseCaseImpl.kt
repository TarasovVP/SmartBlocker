package com.tarasovvp.smartblocker.presentation.main.number.details.details_filter

import com.google.firebase.auth.FirebaseAuth
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.repository.*
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.DetailsFilterUseCase
import com.tarasovvp.smartblocker.utils.extensions.isNotNull
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

    override suspend fun allCallWithFiltersByFilter(filter: String) = logCallRepository.allCallWithFiltersByFilter(filter)

    override suspend fun allContactsWithFiltersByFilter(filter: String) = contactRepository.allContactsWithFiltersByFilter(filter)

    override suspend fun allFilteredCallsByFilter(filter: String) = filteredCallRepository.allFilteredCallsByFilter(filter)

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