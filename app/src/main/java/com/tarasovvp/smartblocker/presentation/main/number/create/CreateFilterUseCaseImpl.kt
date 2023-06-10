package com.tarasovvp.smartblocker.presentation.main.number.create

import com.google.firebase.auth.FirebaseAuth
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.repository.*
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.CreateFilterUseCase
import com.tarasovvp.smartblocker.utils.extensions.isNotNull
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

    override suspend fun getCountryCodeWithCode(code: Int) = countryCodeRepository.getCountryCodeByCode(code)

    override suspend fun allCallsWithFiltersByCreateFilter(filter: String) = logCallRepository.allCallsWithFiltersByCreateFilter(filter)

    override suspend fun allContactsWithFiltersByCreateFilter(filter: String) = contactRepository.allContactsWithFiltersByCreateFilter(filter)

    override suspend fun getFilter(filter: String) = filterRepository.getFilter(filter)

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